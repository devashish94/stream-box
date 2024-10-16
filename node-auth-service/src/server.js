const express = require("express");
const { OAuth2Client } = require("google-auth-library");
const { decode, sign } = require("jsonwebtoken");
const { buildUrl } = require("./utils/util");
const { HttpStatusCode } = require("axios");
const os = require("os");
const { createUser } = require("./services/userService");
const {
  tokenMiddleware,
  validateMiddleware,
} = require("./middlewares/validation.middleware");
const { default: axios } = require("axios");
const { EurekaDiscovery } = require("./config/eureka.config");

const app = express();

app.use(express.json());

const client = new OAuth2Client({
  clientId: process.env.GOOGLE_CLIENT_ID,
  clientSecret: process.env.GOOGLE_CLIENT_SECRET,
  redirectUri: "http://localhost:8090/auth/request-token",
});

const FRONTEND_URL = "http://localhost:5173";

const scope = [
  "https://www.googleapis.com/auth/userinfo.profile",
  "https://www.googleapis.com/auth/userinfo.email",
];

app.get("/auth/health", function (req, res) {
  return res.json({
    status: "ok",
    statusCode: HttpStatusCode.Ok,
    message: "Node auth server is finally up.",
    instance_id: os.hostname(),
  });
});

app.get("/auth/google", (req, res) => {
  const authorizationUrl = client.generateAuthUrl({
    access_type: "offline",
    scope,
  });

  return res.redirect(authorizationUrl);
});

app.get("/auth/request-token", async function (req, res) {
  const code = req.query?.code;
  console.log("Code:", code);
  if (!code) {
    console.error({
      err: "No code in the body",
      message: "Could not extract the code required to exchange for the tokens",
    });
    return res.redirect(`${FRONTEND_URL}/error`);
  }

  try {
    const {
      tokens: { id_token },
    } = await client.getToken(code);
    console.log("Google Sign In done, id_token:", id_token);
    const decodedToken = decode(id_token);

    const token = sign({ user_id: decodedToken.sub }, process.env.SECRET, {
      expiresIn: "30d",
    });

    let urlOptions = {
      token,
      ...decodedToken,
    };

    const response = await createUser(decodedToken);
    console.log("response:", response);

    if (response) {
      urlOptions = { ...urlOptions, ...response };
    }

    const finalUrl = buildUrl(`${FRONTEND_URL}/auth/google`, urlOptions);

    return res.redirect(finalUrl);
  } catch (error) {
    return res.redirect(`${FRONTEND_URL}/error`);
  }
});

app.get("/auth/validate", validateMiddleware, function (req, res) {
  console.log("auth validate hit", req.headers);
  console.log("req.user:", req.user);
  return res
    .setHeader("X-USER-ID", req.user.user_id)
    .send("user_id: " + req.user.user_id);
});

app.get("/auth/protected", tokenMiddleware, function (req, res) {
  return res.json({
    message: "This is the protected message 😉",
  });
});

app.get("/auth/check/user-service", tokenMiddleware, async function (req, res) {
  console.log("Making request to user service health/ping");
  const serviceUrl = EurekaDiscovery.getServiceUrl("user-service");
  console.log("service url:", serviceUrl);
  let result = null;
  try {
    result = (
      await axios.get(`${serviceUrl}/users/health/ping`, {
        headers: {
          "X-USER-ID": req.user.token,
        },
      })
    ).data;
  } catch (err) {
    result = err;
  }
  console.log("result", result);

  return res.json({
    nodeAuthServiceResult: "done",
    userServiceResult: result,
  });
});

app.all("*", function (req, res) {
  console.log(req.headers);
  return res.json({
    status: "Not found",
    statusCode: HttpStatusCode.NotFound,
  });
});

module.exports = app;

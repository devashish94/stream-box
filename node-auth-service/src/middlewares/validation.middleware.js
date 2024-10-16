const { isTokenValid } = require("../services/service");

exports.validateMiddleware = async (req, res, next) => {
  console.log("token middlware hit");
  const header = req.headers.authorization;

  console.log("headers:", req.headers);

  if (!header) {
    console.log("No headers:", header);
    return res.status(400).send("No headers");
  }

  const accessToken = header.split(" ")[1];
  console.log("token:", accessToken);
  if (!accessToken) {
    console.log("No token:", header);
    return res.status(401).send("Invalid Bearer Token");
  }

  const [err, payload] = isTokenValid(accessToken);
  console.log("AccessToken from req:", accessToken);
  console.log("payload:", payload);

  if (err) {
    console.log("[403 Unauthorized] err:", err);
    return res.status(403).send("Unauthorized");
  }

  req.user = payload;
  req.token = accessToken;
  next();
};

exports.tokenMiddleware = async (req, res, next) => {
  console.log("token middlware hit");
  const header = req.headers.authorization;

  console.log("headers:", req.headers);

  if (!header) {
    console.log("No headers:", header);
    return res.status(400).json({
      status: 400,
      message: "No Authorization headers",
    });
  }

  const accessToken = header.split(" ")[1];
  console.log("token:", accessToken);
  if (!accessToken) {
    console.log("No token:", header);
    return res.status(401).json({
      status: 401,
      message: "Invalid Bearer Token",
    });
  }

  const [err, payload] = isTokenValid(accessToken);
  console.log("AccessToken from req:", accessToken);
  console.log("payload:", payload);

  if (err) {
    console.log("[403 Unauthorized] err:", err);
    return res.status(403).json({
      status: 403,
      message: "Unauthorized",
    });
  }

  req.user = payload;
  req.token = accessToken;
  next();
};

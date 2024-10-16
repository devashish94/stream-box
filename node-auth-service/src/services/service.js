const { verify } = require("jsonwebtoken");

exports.isGoogleTokenValid = async function (accessToken) {
  console.log("AccessToken from req:", accessToken);
  if (!accessToken) {
    return [new Error("Invalid Access Token"), null];
  }

  try {
    const ticket = await client.verifyIdToken({
      idToken: accessToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });
    const payload = ticket.getPayload();
    console.log("Payload after validation:", payload);

    return [null, payload];
  } catch (error) {
    console.error("Token validation failed:", error);

    return [null, null];
  }
};

exports.isTokenValid = function (token) {
  console.log("Token:", token);
  if (!token) {
    return [new Error("Invalid Token"), null];
  }

  try {
    const decodedToken = verify(token, process.env.SECRET);
    return [null, decodedToken];
  } catch (err) {
    return [err, null];
  }
};

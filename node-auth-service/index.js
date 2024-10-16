const os = require("os");
require("dotenv").config();

const PORT = 8080;
const HOST = os.hostname();

const { EurekaDiscovery } = require("./src/config/eureka.config");
const eurekaClient = EurekaDiscovery.getClient(HOST, PORT);

eurekaClient.start((error) => {
  if (error) {
    console.error("Error registering with Eureka:", error);
    process.exit();
  } else {
    console.log("Successfully registered with Eureka!");
  }
});

process.on("SIGTERM", () => {
  eurekaClient.stop(() => {
    console.log("Eureka client stopped");
    process.exit();
  });
});

const app = require("./src/server");

app.listen(PORT, HOST, () =>
  console.log(`Node Auth Service running at PORT: ${PORT}`)
);

const { default: axios } = require("axios");
const { EurekaDiscovery } = require("../config/eureka.config");

exports.createUser = async function (user) {
  console.log("Making request to user service", user);
  const serviceUrl = EurekaDiscovery.getServiceUrl("user-service");
  console.log("service url:", serviceUrl);
  const { data, status } = await axios.post(`${serviceUrl}/users`, {
    ...user,
  });
  if (status === 201) {
    return data;
  }
  return null;
};

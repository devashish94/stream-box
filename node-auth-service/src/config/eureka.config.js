const Eureka = require("eureka-js-client").Eureka;

class EurekaDiscovery {
  static getClient(host, port) {
    if (!this.client) {
      if (!host && !port) {
        throw new Error("Client not ready and HOST PORT not provided");
      }
      this.client = new Eureka({
        instance: {
          app: "node-auth-service",
          hostName: host,
          ipAddr: host,
          port: {
            $: port,
            "@enabled": "true",
          },
          vipAddress: "node-auth-service",
          dataCenterInfo: {
            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
            name: "MyOwn",
          },
        },
        eureka: {
          host: process.env.EUREKA_HOST,
          port: 8761,
          servicePath: "/eureka/apps/",
        },
      });
    }
    return this.client;
  }
  static getServiceUrl(serviceName) {
    console.log("Finding the instance by app id");
    const instances = this.client.getInstancesByAppId(serviceName);
    if (instances.length === 0) {
      throw new Error("No instances found with the serviceName:", serviceName);
    }
    console.log("all instances:", instances);
    const instance = instances[0];
    console.log("instance[0]:", instance);
    console.log("instance.port: ", instance.port);
    const serviceUrl = `http://${instance.hostName}:${instance.port.$}`;
    return serviceUrl;

    // return new Promise((resolve, reject) => {
    //   this.client.getInstancesByAppId(serviceName, (error, instances) => {
    //     console.log("inside the promise object");
    //     if (error) {
    //       return reject(error);
    //     }
    //     console.log("no error");
    //     if (instances.length === 0) {
    //       return reject(new Error("No instances available"));
    //     }
    //     console.log("instance length === 0 // false");
    //     console.log("all instances:", instances);
    //     const instance = instances[0];
    //     const serviceUrl = `http://${instance.hostName}:${instance.port.$}`;
    //     resolve(serviceUrl);
    //   });
    // });
  }
}

exports.EurekaDiscovery = EurekaDiscovery;

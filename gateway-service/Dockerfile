FROM amazoncorretto:21-alpine3.20-jdk

COPY ./build/libs/gateway-service-0.0.1-SNAPSHOT.jar app/gateway-service.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "/app/gateway-service.jar"]

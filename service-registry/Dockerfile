FROM amazoncorretto:21-alpine3.20-jdk

COPY ./build/libs/service-registry-0.0.1-SNAPSHOT.jar app/service-registry.jar

ENTRYPOINT ["java", "-jar", "/app/service-registry.jar"]

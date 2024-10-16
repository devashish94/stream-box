FROM amazoncorretto:21-alpine3.20-jdk

COPY ./build/libs/user-service-0.0.1-SNAPSHOT.jar app/user-service.jar

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]

FROM amazoncorretto:21-alpine3.20-jdk

COPY ./build/libs/video-catalog-service-0.0.1-SNAPSHOT.jar app/video-catalog-service.jar

ENTRYPOINT ["java", "-jar", "/app/video-catalog-service.jar"]

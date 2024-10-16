FROM amazoncorretto:21-alpine3.20-jdk

RUN apk add --no-cache ffmpeg

COPY ./build/libs/video-processing-service-0.0.1-SNAPSHOT.jar app/video-processing-service.jar

ENTRYPOINT ["java", "-jar", "/app/video-processing-service.jar"]

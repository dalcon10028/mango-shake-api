FROM amazoncorretto:21-alpine

WORKDIR /app

COPY app/build/libs/*.jar app.jar

ARG PORT=8080
ENV PORT ${PORT}
EXPOSE ${PORT}

ENTRYPOINT java -jar /app/app.jar --server.port=${PORT}
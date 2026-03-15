FROM gradle:8.14-jdk17 AS build

WORKDIR /workspace

COPY adaptiveQueue ./adaptiveQueue
COPY queueServer ./queueServer

RUN cd /workspace/adaptiveQueue && gradle publishToMavenLocal --no-daemon
RUN cd /workspace/queueServer && gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/queueServer/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]


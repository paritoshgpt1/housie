## Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

## Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV PORT=8080
COPY --from=build /workspace/target/Housie-0.0.1-SNAPSHOT.war /app/app.war
COPY --from=build /workspace/target/dependency/webapp-runner.jar /app/webapp-runner.jar
# server.port is configured to use ${PORT:8080} in application.properties
EXPOSE 8080
CMD ["java", "-jar", "/app/webapp-runner.jar", "--port", "${PORT}", "/app/app.war"]

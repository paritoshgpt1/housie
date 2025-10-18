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
COPY --from=build /workspace/target/Housie-0.0.1-SNAPSHOT.jar /app/app.jar
# server.port uses ${PORT:8080} in application.properties; no need to pass as arg
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]

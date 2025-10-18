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
# Install Google Chrome Stable (works on Ubuntu/Debian where Chromium may be unavailable)
RUN set -eux; \
    apt-get update; \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
      ca-certificates gnupg wget; \
    wget -qO- https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor > /usr/share/keyrings/google-linux.gpg; \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-linux.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list; \
    apt-get update; \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
      google-chrome-stable \
      fonts-liberation fonts-noto fonts-noto-color-emoji libgbm1; \
    apt-get purge -y --auto-remove gnupg wget; \
    rm -rf /var/lib/apt/lists/*

# Default flags used by most headless Chrome integrations
ENV CHROME_PATH=/usr/bin/google-chrome \
    CHROME_FLAGS="--headless=new --no-sandbox --disable-gpu --disable-dev-shm-usage"
COPY --from=build /workspace/target/Housie-0.0.1-SNAPSHOT.jar /app/app.jar
# server.port uses ${PORT:8080} in application.properties; no need to pass as arg
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]

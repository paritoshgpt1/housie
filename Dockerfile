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
# Install headless Chromium and minimal fonts/deps for printing to PDF
RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
       chromium \
       ca-certificates \
       fonts-liberation \
       fonts-noto \
       fonts-noto-color-emoji \
       libasound2 \
       libatk-bridge2.0-0 \
       libatspi2.0-0 \
       libdrm2 \
       libgbm1 \
       libgtk-3-0 \
       libnss3 \
       libpango-1.0-0 \
       libpangocairo-1.0-0 \
       libx11-xcb1 \
       libxcomposite1 \
       libxdamage1 \
       libxkbcommon0 \
       libxrandr2 \
       libxss1 \
    && rm -rf /var/lib/apt/lists/*

# Allow tools/libraries to find Chrome regardless of distro naming
RUN set -eux; \
    CHROME_BIN="$(command -v chromium || command -v chromium-browser || true)"; \
    if [ -n "$CHROME_BIN" ]; then \
      ln -sf "$CHROME_BIN" /usr/bin/google-chrome; \
      ln -sf "$CHROME_BIN" /usr/bin/chromium; \
    fi

# Default flags used by most headless Chrome integrations
ENV CHROME_PATH=/usr/bin/chromium \
    CHROME_FLAGS="--headless=new --no-sandbox --disable-gpu --disable-dev-shm-usage"
COPY --from=build /workspace/target/Housie-0.0.1-SNAPSHOT.jar /app/app.jar
# server.port uses ${PORT:8080} in application.properties; no need to pass as arg
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]

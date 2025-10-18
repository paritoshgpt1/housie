SHELL := /bin/bash

# Defaults (override by setting in environment or .env)
PORT ?= 8080
SERVICE_ID ?=

# Local DB defaults (override with DB_USER/DB_PASS/DB_HOST/DB_NAME/DB_SSLMODE)
DB_USER ?= postgres
DB_PASS ?=
DB_HOST ?= localhost
DB_NAME ?= housie
DB_SSLMODE ?= disable

.PHONY: help
help:
	@echo "Available targets:"
	@echo "  run-local        - Run via Maven against LOCAL Postgres"
	@echo "  run-neon         - Run via Maven using .env (Neon)"
	@echo "  build            - Build WAR (skip tests)"
	@echo "  docker-build     - Build local Docker image housie:local"
	@echo "  docker-run       - Run Docker against LOCAL Postgres"
	@echo "  docker-run-neon  - Run Docker using .env (Neon)"
	@echo "  render-deploy    - Trigger Render deploy (needs render CLI)"
	@echo "  render-logs      - Tail Render runtime logs"
	@echo "  render-status    - Show latest Render deploy status"

.PHONY: run-local
run-local:
	@echo "Running with local Postgres: $$DB_USER@$$DB_HOST/$$DB_NAME (sslmode=$(DB_SSLMODE))"
	 SPRING_DATASOURCE_URL="jdbc:postgresql://$(DB_HOST):5432/$(DB_NAME)?sslmode=$(DB_SSLMODE)" \
	 SPRING_DATASOURCE_USERNAME="$(DB_USER)" \
	 SPRING_DATASOURCE_PASSWORD="$(DB_PASS)" \
	 SPRING_JPA_HIBERNATE_DDL_AUTO=update \
	 CUSTOM_SCHEME=http \
	 CUSTOM_HOST=localhost:$(PORT) \
	 mvn -DskipTests spring-boot:run

.PHONY: run-neon
run-neon:
	@if [ -f .env ]; then echo "Loading .env"; set -a; source .env; set +a; fi; \
	  mvn -DskipTests spring-boot:run

.PHONY: build
build:
	mvn -DskipTests package

.PHONY: docker-build
docker-build:
	docker build -t housie:local .

.PHONY: docker-run
docker-run: docker-build
	 docker run --rm -p $(PORT):8080 \
	  -e SPRING_DATASOURCE_URL="jdbc:postgresql://$(DB_HOST):5432/$(DB_NAME)?sslmode=$(DB_SSLMODE)" \
	  -e SPRING_DATASOURCE_USERNAME="$(DB_USER)" \
	  -e SPRING_DATASOURCE_PASSWORD="$(DB_PASS)" \
	  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
	  -e CUSTOM_SCHEME=http \
	  -e CUSTOM_HOST=localhost:$(PORT) \
	  housie:local

.PHONY: docker-run-neon
docker-run-neon: docker-build
	@if [ -f .env ]; then ENVFILE="--env-file .env"; else echo ".env not found"; exit 1; fi; \
	  docker run --rm -p $(PORT):8080 $$ENVFILE housie:local

# Helper to resolve Render service ID when not provided
define RESOLVE_SERVICE_ID
  if [ -z "$(SERVICE_ID)" ]; then \
    SERVICE_ID=$$(render services list --output json | jq -r '.[0].service.id'); \
  else \
    SERVICE_ID=$(SERVICE_ID); \
  fi; \
  echo $$SERVICE_ID
endef

.PHONY: render-deploy
render-deploy:
	@SERVICE_ID=$$($(RESOLVE_SERVICE_ID)); \
	  echo "Deploying $$SERVICE_ID"; \
	  render deploys create $$SERVICE_ID --confirm

.PHONY: render-logs
render-logs:
	@SERVICE_ID=$$($(RESOLVE_SERVICE_ID)); \
	  render logs -r $$SERVICE_ID --type runtime --output text --limit 200

.PHONY: render-status
render-status:
	@SERVICE_ID=$$($(RESOLVE_SERVICE_ID)); \
	  render deploys list $$SERVICE_ID --output json | jq -r '.[0] | {id, status, startedAt, finishedAt}'

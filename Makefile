SHELL := /bin/bash

# Defaults (override by setting in environment or .env)
PORT ?= 8080
SERVICE_ID ?=

.PHONY: help
help:
	@echo "Available targets:"
	@echo "  run-local        - Run via Maven (loads .env if present)"
	@echo "  build            - Build WAR (skip tests)"
	@echo "  docker-build     - Build local Docker image housie:local"
	@echo "  docker-run       - Run Docker image (uses .env if present)"
	@echo "  render-deploy    - Trigger Render deploy (needs render CLI)"
	@echo "  render-logs      - Tail Render runtime logs"
	@echo "  render-status    - Show latest Render deploy status"

.PHONY: run-local
run-local:
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
	@if [ -f .env ]; then ENVFILE="--env-file .env"; else ENVFILE=""; fi; \
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


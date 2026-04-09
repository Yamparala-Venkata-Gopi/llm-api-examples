#!/usr/bin/env bash
# Build and push LLM API images to Docker Hub for Helm / minikube.
#
# Default namespace matches Docker Hub login shown in Docker Desktop.
# Override anytime: ./scripts/docker-build-push.sh otheruser
#            or:   DOCKERHUB_USERNAME=otheruser ./scripts/docker-build-push.sh
#
#   docker login   # once per machine
#   ./scripts/docker-build-push.sh

set -euo pipefail

# Docker Hub org or user (change here if your Hub namespace differs)
DEFAULT_DOCKERHUB_USERNAME="venkatagopiy"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
USER_NAME="${1:-${DOCKERHUB_USERNAME:-${DEFAULT_DOCKERHUB_USERNAME}}}"

PY_IMAGE="${USER_NAME}/llm-api-python:latest"
JAVA_IMAGE="${USER_NAME}/llm-api-java:latest"

echo "Building ${PY_IMAGE} ..."
docker build -t "${PY_IMAGE}" "${ROOT}/python-app"

echo "Building ${JAVA_IMAGE} ..."
docker build -t "${JAVA_IMAGE}" "${ROOT}/java-app"

echo "Pushing ${PY_IMAGE} ..."
docker push "${PY_IMAGE}"

echo "Pushing ${JAVA_IMAGE} ..."
docker push "${JAVA_IMAGE}"

echo "Done. Helm example:"
echo "  helm install llm-api ./helm/llm-api-app --set app=python \\"
echo "    --set python.image.repository=${USER_NAME}/llm-api-python \\"
echo "    --set python.image.tag=latest"

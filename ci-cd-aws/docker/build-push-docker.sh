#!/usr/bin/env bash
# ==============================================================================
# build-push-docker.sh
#
# Builds Docker images for every microservice in the repo and pushes each one to
# Amazon ECR. This is the single source of truth used by CodeBuild (buildspec)
# and can also be run locally (awscli + Docker required).
#
# Usage:
#   build-push-docker.sh \
#     <ecr_repository_uri>   # e.g. 123456789012.dkr.ecr.us-east-1.amazonaws.com
#     <aws_region>           # e.g. us-east-1
#     <image_tag>            # short commit sha, build id, or any unique tag
#     [extra_docker_args...] # e.g. --build-arg STRIPE_SECRET_KEY=$KEY
# ==============================================================================

set -euo pipefail

REPO_URI="${1:?Usage: build-push-docker.sh <repo_uri> <region> <image_tag> [--build-arg KEY=VALUE ...]}"
REGION="${2:?missing: aws_region}"
TAG="${3:?missing: image_tag}"
shift 3 || true

EXTRA_ARGS=("$@")

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SERVICES_DIR="$REPO_ROOT"
TMP_LIST="$(mktemp)"

# --- Discover services --------------------------------------------------------
# Every top-level directory containing pom.xml + Dockerfile is a deployable unit.
mapfile -t DISCOVERED_SERVICES < <(
  find "$SERVICES_DIR" -mindepth 1 -maxdepth 1 -type d -print \
    | while read -r d; do
        [ -f "$d/pom.xml" ] && [ -f "$d/Dockerfile" ] && basename "$d"
      done \
    | sort
)

if [ "${#DISCOVERED_SERVICES[@]}" -eq 0 ]; then
  echo "FATAL: no services discovered under $SERVICES_DIR" >&2
  exit 1
fi

echo "Discovered ${#DISCOVERED_SERVICES[@]} services: ${DISCOVERED_SERVICES[*]}"
printf '%s\n' "${DISCOVERED_SERVICES[@]}" > "$TMP_LIST"

# --- Docker registry login ----------------------------------------------------
echo "Logging into ECR at $REPO_URI ($REGION)"
aws ecr get-login-password --region "$REGION" \
  | docker login --username AWS --password-stdin "$REPO_URI"

# --- Build + push per service -------------------------------------------------
while IFS= read -r SERVICE; do
  [ -n "$SERVICE" ] || continue
  SRV_DIR="$SERVICES_DIR/$SERVICE"

  # read EXPOSE <port> from the service Dockerfile
  PORT="$(sed -n 's/^EXPOSE[[:space:]]\+\([0-9]\+\)[[:space:]]*$/\1/p' "$SRV_DIR/Dockerfile" | head -n1)"
  [ -n "$PORT" ] || PORT=8080

  # ECR image name (repository) keyed off the service directory
  REPO_NAME="${SERVICE,,}"
  IMG_URI="$REPO_URI/$REPO_NAME"

  echo "--------------------------------------------------------------"
  echo "Building  $SERVICE  (port $PORT) -> $IMG_URI"

  docker build -f "$SRV_DIR/Dockerfile" \
    -t "$IMG_URI:$TAG" \
    -t "$IMG_URI:latest" \
    "${EXTRA_ARGS[@]:-}" \
    "$SRV_DIR"

  echo "Pushing   $IMG_URI:$TAG"
  docker push "$IMG_URI:$TAG"
  echo "Pushing   $IMG_URI:latest"
  docker push "$IMG_URI:latest"

  echo "Pushed    $IMG_URI:$TAG"
done < "$TMP_LIST"

rm -f "$TMP_LIST"
echo "=============================================================="
echo "Completed. Services: ${DISCOVERED_SERVICES[*]}"
echo "Registry: $REPO_URI"
echo "Tags pushed: $TAG (and latest) per image."
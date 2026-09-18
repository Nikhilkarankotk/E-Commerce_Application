#!/usr/bin/env bash
# ==============================================================================
# deploy-cloudformation.sh
#
# One-shot deployer for the ecommerce-cicd.yaml template. `create` or `update`
# the stack; ships the CloudFormation template inline so CI can run it too.
#
# Prereqs:
#   - AWS CLI installed & authenticated (default profile / env creds)
#   - Docker (optional; only for testing the generated images via build-docker.sh)
#
# Usage:
#   ./deploy-cloudformation.sh <create|update> \
#       --connection-arn <CodeStar GitHubConnection ARN> \
#       --owner        <github-owner> \
#       --repo         E-Commerce_Application \
#       [--branch main] [--email you@example.com] [--stack-name ecommerce-cicd]
#       [--parameters ParameterKey=..,ParameterValue=..]
# ==============================================================================
set -euo pipefail

ACTION="${1:?usage: deploy-cloudformation.sh <create|update> --connection-arn <arn> --owner <x> --repo <x>}"
shift

STACK_NAME="ecommerce-cicd"
EMAIL=""
BRANCH="main"
EXTRA_PARAMS=()
TEMPLATE_FILE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/ecommerce-cicd.yaml"

while [ $# -gt 0 ]; do
  case "$1" in
    --connection-arn) CONNECTION_ARN="${2:?--connection-arn needs a value}"; shift 2 ;;
    --owner)          OWNER="${2:?--owner needs a value}";                  shift 2 ;;
    --repo)           REPO="${2:?--repo needs a value}";                    shift 2 ;;
    --branch)         BRANCH="$2";                                          shift 2 ;;
    --email)          EMAIL="$2";                                           shift 2 ;;
    --stack-name)     STACK_NAME="$2";                                      shift 2 ;;
    --parameters)     EXTRA_PARAMS=( --parameters "$2" );                   shift 2 ;;
    --*)
      echo "Unknown option: $1" >&2; exit 2 ;;
    *)
      echo "Unexpected argument: $1" >&2; exit 2 ;;
  esac
done

: "${CONNECTION_ARN:?missing --connection-arn}"
: "${OWNER:?missing --owner}"
: "${REPO:?missing --repo}"

echo "Action:      $ACTION"
echo "Stack:       $STACK_NAME"
echo "GitHub:      $OWNER/$REPO @ $BRANCH"
echo "Connection:  $CONNECTION_ARN"

PARAMS=(
  "ParameterKey=GitHubConnectionArn,ParameterValue=${CONNECTION_ARN}"
  "ParameterKey=GitHubOwner,ParameterValue=${OWNER}"
  "ParameterKey=GitHubRepo,ParameterValue=${REPO}"
  "ParameterKey=GitHubSourceBranch,ParameterValue=${BRANCH}"
)
if [ -n "$EMAIL" ]; then
  PARAMS+=( "ParameterKey=ApprovalNotificationEmail,ParameterValue=${EMAIL}" )
fi

COMMON_ARGS=(
  --template-body "file://${TEMPLATE_FILE}"
  --capabilities CAPABILITY_NAMED_IAM
)

if [ "$ACTION" = "create" ]; then
  aws cloudformation create-stack \
    --stack-name "$STACK_NAME" \
    "${COMMON_ARGS[@]}" \
    --parameters "${PARAMS[@]}" ${EXTRA_PARAMS[@]+"${EXTRA_PARAMS[@]}"}
  echo "Create requested. Track: aws cloudformation wait stack-create-complete --stack-name $STACK_NAME"
elif [ "$ACTION" = "update" ]; then
  # All core parameters are passed above; any additional template params are
  # supplied through --parameters. Existing values are re-sent explicitly so the
  # stack does not drift to defaults.
  aws cloudformation update-stack \
    --stack-name "$STACK_NAME" \
    "${COMMON_ARGS[@]}" \
    --parameters "${PARAMS[@]}" ${EXTRA_PARAMS[@]+"${EXTRA_PARAMS[@]}"}
  echo "Update requested. Track: aws cloudformation wait stack-update-complete --stack-name $STACK_NAME"
else
  echo "Action must be create|update (got: $ACTION)" >&2
  exit 2
fi
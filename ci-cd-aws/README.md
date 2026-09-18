# AWS CI/CD for the E-Commerce Microservices

This directory contains everything needed to build, test and ship **Docker
images** for all nine microservices with **AWS CodePipeline + CodeBuild**, then
push them to **Amazon ECR**.

The pipeline produces images only — running the images (ECS/EC2/local Docker) is
left to you. `docker-compose.yml` in this folder shows how the resulting images
wire together to form the running platform.

## What gets deployed (CodePipeline stages)

```
GitHub (V2 connection)  ->  CodeBuild (buildspec.yml)  ->  Manual approval  ->  done
      Source                      Build & Publish
```

* **Source** — GitHub branch you configure (default `main`); any push triggers a run.
* **Build** — `buildspec.yml` (repo root):
  1. Compiles **and unit-tests** every service (`mvn -q -B test`);
  2. Builds a Docker image per service from that service's `Dockerfile`;
  3. Pushes each image to ECR tagged `<short-commit-sha>` **and** `latest`.
* **Approval** — optional human gate, notifies via SNS (optional email subscription).

## Repo layout & key files

| File | Purpose |
|------|---------|
| `ecommerce-cicd.yaml` | CloudFormation stack: 9 ECR repos, CodeBuild project, CodePipeline, S3 bucket, IAM roles, SNS topic |
| `deploy-cloudformation.sh` | One-shot CLI to `create` / `update` that stack |
| `../buildspec.yml` | CodeBuild definition: Maven phase + Docker push phase |
| `docker/build-push-docker.sh` | Shared image build+push helper (single source of truth, used by CI & locally) |
| `docker/SharedDockerfile` | Template that generates every service `Dockerfile` |
| `<service>/Dockerfile` | Per-service Dockerfile (generated from the shared recipe, port injected) |
| `<service>/.dockerignore` | Keeps build context lean (excludes `target/`, `.mvn`, wrapper) |
| `docker-compose.yml` | Runs the pipeline images end-to-end locally |
| `.env.example` | Copy to `.env` for compose (Stripe keys, registry/tag overrides) |

> **Why a shared Docker recipe?** Every image is a two-stage build
> (compile in `maven:3.9-eclipse-temurin-21-alpine`, run on
> `eclipse-temurin:21-jre-alpine`). Several services ship `mvnw` scripts
> *without* `.mvn/wrapper/maven-wrapper.properties`, so the images deliberately
> use the builder image's system `mvn` instead of `./mvnw`. Health checks hit
> Spring Actuator (`/actuator/health`), which every service exposes. Spring
> relaxed binding means `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` and
> `EUREKA_INSTANCE_HOSTNAME` can be overridden at runtime — handy to point a
> containerized service at the registry's real DNS name.

## 1. Prerequisites

* AWS account with permission to create IAM roles, S3 buckets, ECR repos,
  CodeBuild projects, SNS topics and CodePipelines.
* AWS CLI installed and authenticated (`aws sts get-caller-identity` works).
* A **GitHub repository** for this code (the repo is already a git repo).

## 2. Create the GitHub connection (one-time)

1. Open the AWS console → **Developer Tools → Settings → Connections** → **Create connection**.
2. Provider: **GitHub** → name it (e.g. `github-ecommerce`) → **Connect to GitHub**.
3. Complete the "Update a pending connection" browser/authorization flow and
   install the app on the repository.
4. Copy the connection ARN, e.g.:
   `arn:aws:codestar-connections:us-east-1:123456789012:connection/a1b2c3d4-...`

## 3. Deploy the CloudFormation stack

```bash
./ci-cd-aws/deploy-cloudformation.sh create \
  --connection-arn arn:aws:codestar-connections:us-east-1:123456789012:connection/a1b2c3d4-... \
  --owner <your-github-user-or-org> \
  --repo E-Commerce_Application \
  --branch main \
  --email you@example.com            # optional: approval emails
```

Defaults that are easy to change:
* `--stack-name ecommerce-cicd` (default)
* Compute/image are `ecommerce-cicd.yaml` parameters (`BuildImage`,
  `ComputeType`) — pass `--parameters "ParameterKey=ComputeType,ParameterValue=BUILD_GENERAL1_MEDIUM"`.

The stack creates: 9 ECR repositories (`products`, `carts`, `eurekaserver`,
`gatewayserver`, `message`, `orders`, `payments`, `shippings`, `users`), one
CodeBuild project `${stack}-build`, one pipeline, an S3 artifact bucket, the
approval SNS topic, and IAM roles.

## 4. (Optional) Store Stripe keys safely

The CodeBuild role is scoped to `arn:aws:ssm:...:parameter/ecommerce/*`, so create
the two params the buildspec/`payments` consume (SecureString recommended):

```
/ecommerce/stripe/secret-key       -> sk_live_xxxx
/ecommerce/stripe/publishable-key  -> pk_live_xxxx
```

If these parameters don't exist the build still succeeds — the Payments
`application.yml` and `StripeKeyController` default those values to empty (see
`Payments/src/main/resources/application.yml`). That keeps context-load tests
green without keys. At runtime the Payments container should receive the real
credentials as env vars:

```
STRIPE_SECRET_KEY      (StripeConfig)
STRIPE_PUBLISHABLE_KEY (application.yml stripe.publishable-key)
STRIPE_PUBLIC_KEY      (StripeKeyController /public-key endpoint)
```

## 5. Run

Either push to the watched branch, or click **Release change** in the pipeline
console. Watch CloudWatch logs under `/aws/codebuild/ecommerce-cicd-build`.

## Local verification without CodeBuild

```bash
# 1) validate the stack template
aws cloudformation validate-template --template-body file://ci-cd-aws/ecommerce-cicd.yaml

# 2) build + push all images to any registry (needs Docker + awscli):
./ci-cd-aws/docker/build-push-docker.sh \
   123456789012.dkr.ecr.us-east-1.amazonaws.com us-east-1 my-tag \
   --build-arg STRIPE_SECRET_KEY=sk_test_xxx \
   --build-arg STRIPE_PUBLISHABLE_KEY=pk_test_xxx
#    ^^ Secrets are NOT baked into the images (no matching ARG); the images are
#       configured at runtime. In CI the keys simply come from optional SSM
#       params (/ecommerce/stripe/*).

# 3) run the whole platform from the pushed images:
cd ci-cd-aws
cp .env.example .env   # fill Stripe values
docker compose up -d
# or force-refresh from ECR:  REGISTRY=... IMAGE_TAG=<sha> docker compose up -d --pull always
```

## Notes / limitations

* One CodeBuild project builds all 9 services sequentially (optional
  `ConcurrentBuildLimit`); if builds grow too slow, split the buildspec per
  service and add parallel build actions.
* `IMAGE_TAG` = first 12 chars of the pushed commit sha; `latest` tracks the
  most recent build.
* The pipeline stops at manual approval by design. Remove the `Approval` stage
  from `ecommerce-cicd.yaml` to make it fully automated.
* Keycloak is **not** part of the pipeline (auth is only needed at runtime).
  The gateway reads `KEYCLOAK_JWK_SET_URI` (default `localhost:7080`); point it
  at your realm when deployed. `docker-compose.yml` offers an optional
  `--profile keycloak` to run one locally.
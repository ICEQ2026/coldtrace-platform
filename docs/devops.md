# ColdTrace Backend DevOps

This document records the backend delivery setup used for the v2 release. It
keeps the course reference style for Docker and Maven builds while preserving
ColdTrace-specific Google Cloud Run and Cloud SQL deployment.

## Local Checks

Run these commands before opening a release pull request:

```bash
./mvnw -B test
./mvnw -B -DskipTests package
docker build -t coldtrace-platform:local .
```

Local provider secrets are loaded from `.env.local`, which is ignored by Git.
Use `.env.example` as the template.

## Docker Image

The production image is built from `Dockerfile` using a two-stage build:

1. Maven + Temurin JDK 26 compiles and packages the Spring Boot application.
2. Temurin JRE 26 runs the final JAR with `SPRING_PROFILES_ACTIVE=prod`.

The image exposes port `8080`, which Cloud Run maps through the `PORT`
environment contract.

## GitHub Actions

`.github/workflows/backend-ci.yml` runs on pull requests and pushes to
`develop` and `main`:

- checkout
- Java 26 setup with Maven cache
- disposable MySQL 8.4 service for Spring/JPA integration tests
- `./mvnw -B test`
- `./mvnw -B -DskipTests package`

`.github/workflows/cloud-run-deploy.yml` is a manual Cloud Run deployment
workflow. It requires these GitHub repository secrets before use:

```text
GCP_PROJECT_ID
GCP_WORKLOAD_IDENTITY_PROVIDER
GCP_SERVICE_ACCOUNT
```

Runtime application secrets stay in Cloud Run or Secret Manager, not in GitHub
Actions. The deploy workflow only builds and deploys the container image.

## Cloud Build

`cloudbuild.yaml` provides a reproducible Google Cloud build:

```bash
gcloud builds submit --config cloudbuild.yaml --project coldtrace-499222
```

The build pushes both `$SHORT_SHA` and `latest` tags to Artifact Registry and
deploys the SHA-tagged image to Cloud Run.

The existing Cloud Run trigger can continue watching `main` with the project
Dockerfile. If the trigger is switched to a Cloud Build config, point it to
`cloudbuild.yaml`.

## Cloud Run Runtime

Production service:

```text
Project: coldtrace-499222
Region: us-central1
Service: coldtrace-platform
Artifact Registry: us-central1-docker.pkg.dev/coldtrace-499222/coldtrace-docker/coldtrace-platform
Public URL: https://coldtrace-platform-dtbzbm7bta-uc.a.run.app
```

Required runtime variables:

```text
SPRING_PROFILES_ACTIVE
DATABASE_NAME
DATABASE_USER
DATABASE_PASSWORD
INSTANCE_CONNECTION_NAME
JWT_SECRET
JWT_EXPIRATION_DAYS
CORS_ALLOWED_ORIGIN_PATTERNS
GOOGLE_OAUTH_CLIENT_ID
GOOGLE_OAUTH_CLIENT_SECRET
GOOGLE_OAUTH_REDIRECT_URI
APPLE_OAUTH_CLIENT_ID
APPLE_OAUTH_REDIRECT_URI
APPLE_TEAM_ID
APPLE_KEY_ID
APPLE_PRIVATE_KEY
AI_MODEL_PROVIDER
AI_MODEL_NAME
OPENAI_API_KEY
AI_REQUEST_TIMEOUT
STRIPE_SECRET_KEY
STRIPE_WEBHOOK_SECRET
STRIPE_OPERATIONS_PRICE_ID
STRIPE_COMPLIANCE_AI_PRICE_ID
BILLING_CHECKOUT_SUCCESS_URL
BILLING_CHECKOUT_CANCEL_URL
BILLING_CUSTOMER_PORTAL_RETURN_URL
```

Use OpenAI for the deployed academic backend. Do not run Ollama inside the Cloud
Run container.

## Stripe Webhooks

Local listener:

```bash
~/.local/bin/stripe listen --forward-to localhost:8080/api/v1/billing/stripe/webhooks
```

Production webhook endpoint:

```text
https://coldtrace-platform-dtbzbm7bta-uc.a.run.app/api/v1/billing/stripe/webhooks
```

Enabled Stripe test-mode events:

```text
checkout.session.completed
customer.subscription.updated
customer.subscription.deleted
```

The local CLI signing secret and the production endpoint signing secret are
different values. Do not reuse one in the other environment.

## Release Flow

1. Merge completed work into `develop`.
2. Run backend CI locally or through GitHub Actions.
3. Open a release pull request from `develop` to `main`.
4. Merge the release PR.
5. Confirm the Cloud Run trigger or run `gcloud builds submit --config cloudbuild.yaml`.
6. Smoke test `/v3/api-docs`, authentication, billing, AI assistance, and reports
   against the deployed URL.
7. Tag the backend release as `v<major>.<minor>.<patch>`.

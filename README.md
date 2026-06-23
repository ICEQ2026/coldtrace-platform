# ColdTrace Platform

Spring Boot backend for the ColdTrace final project.

The project follows the layered, bounded-context style used in the official UPC
Learning Center Platform v2610 backend. The backend exposes
organization-scoped REST APIs for identity access, asset management,
monitoring, alerts, reports, and maintenance management while preserving
ColdTrace-specific deployment on Google Cloud Run and Cloud SQL.

## Technology Stack

- Java 26
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- MySQL
- Lombok
- SpringDoc OpenAPI
- Google Cloud SQL Java Connector

## Course Reference Alignment

The backend uses Learning Center Platform v2610 as the primary course-approved
reference for Spring Boot structure:

- Bounded contexts split into `domain`, `application`, `infrastructure`, and
  `interfaces`.
- Thin REST controllers with request/response resources and transform
  assemblers.
- Application command/query services returning shared result/error contracts.
- Domain repository contracts implemented by JPA persistence adapters.
- Localized REST errors through Spring message bundles.
- OpenAPI metadata configured in shared infrastructure.
- Architecture evidence under `docs`.

Security is planned to follow the Learning Center `iam` module as a dedicated
security/IAM scope. Until that ticket is implemented, current APIs remain
organization-scoped through route parameters instead of authenticated
principals.

The planned IAM scope also includes Google and Apple social login. Those
providers validate external identity only; ColdTrace must still link the
provider subject to a local user, organization, role, and JWT session. Provider
secrets and Apple private keys must stay in environment/provider-console
configuration, never in Angular source or committed files.

## Architecture Docs

The `docs` folder mirrors the course reference repository:

- `docs/software-architecture.dsl`: Structurizr C4 model for the ColdTrace
  solution.
- `docs/class-diagram.puml`: PlantUML class diagram focused on backend bounded
  contexts and shared components.
- `docs/user-stories.md`: API-facing technical stories and acceptance criteria.

## Branching

Use Git Flow for sprint work.

- `main`: stable release branch used by Cloud Run continuous deployment.
- `develop`: integration branch for completed sprint work.
- `feature/<scope>`: technical work without a Technical Story.
- `feature/TSxx-<scope>`: work linked to a Technical Story.
- `release/<version>`: release preparation branch from `develop` into `main`.

Release tags use the `v<major>.<minor>.<patch>` format.

## Run Locally

Local development uses MySQL and the `dev` profile.

Create or reuse a local MySQL database named:

```text
coldtrace-platform
```

Default local credentials are:

```properties
DATABASE_USER=root
DATABASE_PASSWORD=root
```

Run the backend locally:

```bash
export DATABASE_USER=root
export DATABASE_PASSWORD=root
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The application starts on port `8080`.

Local Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Local OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Production Deployment

The production backend is deployed on Google Cloud Run and uses Google Cloud SQL
for MySQL.

```text
Google Cloud project: coldtrace-499222
Cloud Run service: coldtrace-platform
Region: us-central1
Cloud SQL instance: coldtrace-mysql
Cloud SQL connection name: coldtrace-499222:us-central1:coldtrace-mysql
Database name: coldtrace-platform
Database user: coldtrace_app
Artifact Registry repository: coldtrace-docker
Docker image: us-central1-docker.pkg.dev/coldtrace-499222/coldtrace-docker/coldtrace-platform:latest
```

Production environment variables:

```properties
SPRING_PROFILES_ACTIVE=prod
DATABASE_NAME=coldtrace-platform
DATABASE_USER=coldtrace_app
DATABASE_PASSWORD=<cloud-sql-user-password>
INSTANCE_CONNECTION_NAME=coldtrace-499222:us-central1:coldtrace-mysql
```

The production JDBC URL is configured in `application-prod.properties` and uses
the Cloud SQL Java Connector:

```properties
spring.datasource.url=jdbc:mysql:///${DATABASE_NAME}?cloudSqlInstance=${INSTANCE_CONNECTION_NAME}&socketFactory=com.google.cloud.sql.mysql.SocketFactory&cloudSqlRefreshStrategy=lazy&serverTimezone=UTC
```

Cloud Build continuous deployment should use:

```text
Branch regex: ^main$
Build type: Dockerfile
Dockerfile location: /Dockerfile
```

Production API:

```text
https://coldtrace-platform-dtbzbm7bta-uc.a.run.app
```

Production Swagger UI:

```text
https://coldtrace-platform-dtbzbm7bta-uc.a.run.app/swagger-ui/index.html
```

Production OpenAPI JSON:

```text
https://coldtrace-platform-dtbzbm7bta-uc.a.run.app/v3/api-docs
```

## Connect to Cloud SQL Locally

Install the Cloud SQL Auth Proxy:

```bash
brew install cloud-sql-proxy
```

Start the proxy from a terminal and keep it running while using MySQL Workbench:

```bash
env -u GOOGLE_APPLICATION_CREDENTIALS cloud-sql-proxy \
  --gcloud-auth \
  --quota-project coldtrace-499222 \
  --address 127.0.0.1 \
  --port 3307 \
  coldtrace-499222:us-central1:coldtrace-mysql
```

Use these MySQL Workbench values:

```text
Connection method: Standard TCP/IP
Hostname: 127.0.0.1
Port: 3307
Username: coldtrace_app
Password: cloud-sql user password
Default schema: coldtrace-platform
```

The `env -u GOOGLE_APPLICATION_CREDENTIALS` prefix avoids using credentials from
another Google Cloud project when this machine has that variable configured.

## Verification

Package the backend:

```bash
./mvnw -q -DskipTests package
```

The current repository does not add automated project tests yet, so release
verification is done with packaging, Swagger/OpenAPI checks, and API smoke flows
against the deployed Cloud Run service.

Production OpenAPI smoke check:

```bash
curl -I https://coldtrace-platform-dtbzbm7bta-uc.a.run.app/v3/api-docs
```

Expected result:

```text
HTTP/2 200
```

## Package Map

```text
com.acme.coldtrace.platform
|-- identityaccess
|-- assetmanagement
|-- monitoring
|-- alerts
|-- reports
|-- maintenancemanagement
`-- shared
```

Context responsibilities:

- `identityaccess`: organizations, users, roles, and organization sign-up.
- `assetmanagement`: locations, gateways, assets, IoT devices, and asset settings.
- `monitoring`: sensor readings and random demo reading generation.
- `alerts`: incidents, notifications, acknowledgement, escalation, corrective action, and resolution.
- `reports`: operational reports.
- `maintenancemanagement`: maintenance schedules and technical service requests.
- `shared`: reusable application, persistence, domain, and REST support.

Authentication, JWT, password reset, and real session behavior are deferred until
the corresponding course content is covered. Current APIs are scoped by
`organizationId` in the route instead of requiring an authenticated principal.

## API Overview

Identity access:

| Path | Operations |
| --- | --- |
| `/organization-sign-ups` | `POST` |
| `/organizations` | `GET`, `POST` |
| `/roles` | `GET` |
| `/organizations/{organizationId}/users` | `GET`, `POST` |
| `/organizations/{organizationId}/users/{userId}/role` | `PATCH` |

Asset management:

| Path | Operations |
| --- | --- |
| `/organizations/{organizationId}/locations` | `GET`, `GET /{locationId}`, `POST`, `PUT /{locationId}` |
| `/organizations/{organizationId}/gateways` | `GET`, `GET /{gatewayId}`, `POST`, `PUT /{gatewayId}` |
| `/organizations/{organizationId}/assets` | `GET`, `GET /{assetId}`, `POST`, `PUT /{assetId}` |
| `/organizations/{organizationId}/iot-devices` | `GET`, `GET /{iotDeviceId}`, `POST`, `PUT /{iotDeviceId}` |
| `/organizations/{organizationId}/asset-settings` | `GET` |
| `/organizations/{organizationId}/asset-settings/default` | `PUT` |
| `/organizations/{organizationId}/assets/{assetId}/settings` | `GET`, `PUT` |

Monitoring:

| Path | Operations |
| --- | --- |
| `/organizations/{organizationId}/sensor-readings` | `GET`, `GET /{sensorReadingId}`, `POST` |
| `/organizations/{organizationId}/sensor-readings/demo-generations` | `POST` |

Alerts:

| Path | Operations |
| --- | --- |
| `/organizations/{organizationId}/incidents` | `GET`, `GET /{incidentId}`, `POST` |
| `/organizations/{organizationId}/incidents/{incidentId}/acknowledgements` | `POST` |
| `/organizations/{organizationId}/incidents/{incidentId}/escalation` | `PATCH` |
| `/organizations/{organizationId}/incidents/{incidentId}/corrective-action` | `PATCH` |
| `/organizations/{organizationId}/incidents/{incidentId}/resolutions` | `POST` |
| `/organizations/{organizationId}/incidents/{incidentId}/notifications` | `GET` |
| `/organizations/{organizationId}/notifications` | `GET` |

Reports:

| Path | Operations |
| --- | --- |
| `/organizations/{organizationId}/reports` | `GET`, `GET /{reportId}`, `POST` |

Maintenance management:

| Path | Operations |
| --- | --- |
| `/organizations/{organizationId}/maintenance-schedules` | `GET`, `GET /{maintenanceScheduleId}`, `POST`, `PATCH /{maintenanceScheduleId}` |
| `/organizations/{organizationId}/technical-service-requests` | `GET`, `GET /{technicalServiceRequestId}`, `POST`, `PATCH /{technicalServiceRequestId}` |

## Telemetry Notes

Sensor readings can be created manually or generated internally for demo data.
The random generation endpoint is:

```text
POST /organizations/{organizationId}/sensor-readings/demo-generations
```

Each IoT device declares the values it supports in `measurementParameters`.
When creating a manual reading, only send fields supported by that device. For
example, a device that supports `temperature` and `humidity` should not receive
extra motion, image, battery, or signal values in the request body.

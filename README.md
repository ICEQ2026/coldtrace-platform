# ColdTrace Platform

Spring Boot backend for the ColdTrace final project.

This project follows the same baseline used in the course examples:

- Java 26
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA
- Bean Validation
- MySQL
- Lombok
- SpringDoc OpenAPI

## Branching

Use Git Flow for sprint work.

- `main`: stable repository branch
- `develop`: integration branch
- `feature/<scope>`: technical work without a Technical Story
- `feature/TSxx-<scope>`: work linked to a Technical Story

Current branch for the backend foundation:

```bash
feature/spring-boot-api-foundation
```

## Run Locally

Default local execution uses the credentials currently configured in
`application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

Run with the default profile:

```bash
./mvnw spring-boot:run
```

Run with the `dev` profile and environment variables:

```bash
export DATABASE_USER=root
export DATABASE_PASSWORD=root
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The local database is created automatically when the MySQL user has permission:

```text
coldtrace-platform
```

## Verification

Compile:

```bash
./mvnw -DskipTests compile
```

Run tests:

```bash
./mvnw test
```

Swagger UI is available after starting the application:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Package Map

The Sprint 3 backend should follow the same bounded-context style used in the
course projects.

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

Suggested responsibility per context:

- `identityaccess`: organizations, users, roles, permissions, and password reset request data.
- `assetmanagement`: monitored assets, IoT devices, gateways, and asset settings.
- `monitoring`: sensor readings and dashboard data sources.
- `alerts`: incidents, notifications, and incident lifecycle operations.
- `reports`: operational reports, compliance records, and audit evidence.
- `maintenancemanagement`: preventive maintenance schedules and technical service requests.
- `shared`: reusable application, persistence, and REST support.

Authentication, JWT, CORS hardening, and password reset flows are deferred until
the corresponding course content is covered.

## Frontend API Contract

The Open Source Angular frontend is the strict API consumer for this backend.
It currently replaces the backend with JSON Server by building URLs as:

```text
environment.platformProviderApiBaseUrl + environment.platformProvider...EndpointPath
```

For local development that resolves to direct collection paths such as
`http://localhost:3000/assets`, not `/api/v1/assets`. If the backend later uses
an `/api/v1` prefix, the Angular environment must be changed at the same time or
the backend must expose compatibility routes for the direct paths.

The first business endpoints should preserve the same resource names and field
names used by `coldtrace-frontend/server/db.json`. Collection reads should return
arrays directly unless the frontend assembler is updated first.

Expected backend endpoints:

| Context | Backend path | Operations |
| --- | --- | --- |
| `identityaccess` | `/organization-sign-ups` | `POST` |
| `identityaccess` | `/organizations` | `GET`, `POST` |
| `identityaccess` | `/organizations/{organizationId}/users` | `GET`, `POST` |
| `identityaccess` | `/roles` | `GET`, `PUT /roles/{id}` |
| `identityaccess` | `/password-reset-requests` | declared for password recovery, real flow deferred |
| `assetmanagement` | `/assets` | `GET`, `POST`, `PUT /assets/{id}` |
| `assetmanagement` | `/iot-devices` | `GET`, `POST`, `PUT /iot-devices/{id}` |
| `assetmanagement` | `/gateways` | `GET`, `POST`, `PUT /gateways/{id}` |
| `assetmanagement` | `/asset-settings` | `GET`, `POST`, `PUT /asset-settings/{id}` |
| `monitoring` | `/sensor-readings` | `GET`, `POST` |
| `alerts` | `/incidents` | `GET`, `POST`, `PUT /incidents/{id}` |
| `alerts` | `/notifications` | `GET`, `POST` |
| `reports` | `/reports` | `GET`, `POST` |
| `maintenancemanagement` | `/maintenance-schedules` | `GET`, `POST`, `PUT /maintenance-schedules/{id}` |
| `maintenancemanagement` | `/technical-service-requests` | `GET`, `POST`, `PUT /technical-service-requests/{id}` |

The ideal backend sign-up endpoint is `/organization-sign-ups`. It creates the
organization and its first super administrator user in one transaction. The
Angular frontend must be adapted away from JSON Server-style ID calculation and
two-step organization/user creation before consuming this contract. The Angular
environment also declares `/authentication/sign-up` and `/authentication/sign-in`,
but real authentication, JWT/session behavior, and password reset flows remain
deferred until the corresponding sprint ticket.

Use these frontend resource fields as the backend DTO contract:

| Resource | Fields |
| --- | --- |
| `organizations` | `id`, `legalName`, `commercialName`, `taxId`, `contactEmail` |
| `organization-sign-ups` | request: `legalName`, `commercialName`, `taxId`, `contactEmail`, `firstName`, `lastName`, `email`; response: `organization`, `user` |
| `users` | response: `id`, `uuid`, `organizationUserId`, `firstName`, `lastName`, `email`, `organizationId`, `roleId`; create request under `/organizations/{organizationId}/users`: `firstName`, `lastName`, `email`, `roleId` |
| `roles` | `id`, `name`, `label`, `permissions` |
| `assets` | `id`, `organizationId`, `uuid`, `type`, `gatewayId`, `name`, `location`, `capacity`, `description`, `status`, `lastIncident`, `currentTemperature`, `entryDate`, `connectivity` |
| `gateways` | `id`, `organizationId`, `uuid`, `name`, `location`, `network`, `status` |
| `asset-settings` | `id`, `organizationId`, `uuid`, `assetTypes`, `iotDeviceTypes`, `minimumTemperature`, `maximumTemperature`, `maximumHumidity`, `calibrationFrequencyDays`, `temperatureUnit`, `humidityUnit`, `weightUnit`, `assetId` |
| `iot-devices` | `id`, `organizationId`, `uuid`, `deviceType`, `model`, `measurementType`, `measurementParameters`, `readingFrequencySeconds`, `assetId`, `status`, `calibrationStatus`, `lastCalibrationDate`, `nextCalibrationDate` |
| `sensor-readings` | `id`, `assetId`, `iotDeviceId`, `temperature`, `humidity`, `isOutOfRange`, `recordedAt`, `motionDetected`, `imageCaptured`, `batteryLevel`, `signalStrength` |
| `incidents` | `id`, `organizationId`, `assetId`, `assetName`, `type`, `severity`, `value`, `detectedAt`, `status`, `recognizedBy`, `recognizedAt`, `conditionStable`, `correctiveAction`, `closureEvidence`, `closedBy`, `closedAt`, `conditionKey`, `source`, `sourceReadingId`, `reviewStatus`, `escalationStatus`, `escalationLevel`, `escalationPolicyMinutes`, `escalatedAt`, `escalatedTo`, `escalationReviewedBy`, `escalationReviewedAt` |
| `notifications` | `id`, `organizationId`, `incidentId`, `assetName`, `channel`, `recipient`, `message`, `status`, `createdAt`, `deliveredAt`, `failureReason` |
| `reports` | `id`, `organizationId`, `uuid`, `type`, `title`, `periodDate`, `generatedAt` |
| `maintenance-schedules` | `id`, `organizationId`, `uuid`, `assetId`, `iotDeviceId`, `scheduledDate`, `period`, `observations`, `status`, `createdAt` |
| `technical-service-requests` | `id`, `organizationId`, `uuid`, `assetId`, `priority`, `issueDescription`, `requestedDate`, `status`, `interventionNotes`, `resultNotes`, `functionalTestPassed`, `closedAt` |

## Current Sprint Scope

The first backend foundation work item only prepares the platform baseline. It
does not implement business endpoints yet.

Completed baseline:

- Maven project and wrapper
- Spring Boot application entry point
- JPA auditing
- MySQL datasource profiles
- Snake-case physical naming strategy
- Shared `Result` type
- Global validation error handler
- Message bundles for error localization
- SpringDoc dependency for Swagger/OpenAPI

Next business API after this foundation:

```text
TS01 - Organization Sign-Up API
```

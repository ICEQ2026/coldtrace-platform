# ColdTrace Platform - REST API Technical Stories

## Overview

This document contains API-focused technical stories for frontend developers integrating with the ColdTrace Platform REST API.

Common conventions:

- Base path: `/api/v1`
- All request and response bodies use `Content-Type: application/json`
- Error responses follow the shared `ErrorResource` schema with `code`, `message`, and `details`
- Current implemented endpoints are organization-scoped through `{organizationId}` route parameters
- Protected routes use JWT Bearer authentication.

---

### TS-ARCH001 - Handle Standardized API Error Responses

As a frontend developer, I want all API errors to follow one schema so that the web application can implement a reusable error handler.

Acceptance criteria:

- Scenario: Validation error
  - Given a request body is missing required fields
  - When the backend validates the request
  - Then the API responds `400 Bad Request` with `code`, `message`, and `details`.
- Scenario: Not found
  - Given a requested organization-scoped resource does not exist
  - When the backend cannot find it
  - Then the API responds `404 Not Found` with the same error schema.
- Scenario: Conflict
  - Given the request duplicates a unique business value
  - When the backend detects the conflict
  - Then the API responds `409 Conflict`.

---

### TS-ARCH002 - Request Localized Error Messages

As a frontend developer, I want to request English or Spanish API error messages so that user-facing validation feedback can match the selected language.

Acceptance criteria:

- Scenario: English default
  - Given the client sends no `Accept-Language` header
  - When the API returns an error
  - Then the message is returned in English.
- Scenario: Spanish
  - Given the client sends `Accept-Language: es`
  - When the API returns an error
  - Then the message is returned in Spanish.

---

### TS-SEC001 - Authenticate API Requests with JWT Bearer Tokens

As a frontend developer, I want to authenticate users through the backend so that protected operational endpoints are accessed with a valid token.

Status: implemented security ticket.

Acceptance criteria:

- Scenario: Sign in succeeds
  - Given a registered user submits valid credentials to `POST /api/v1/authentication/sign-in`
  - When the backend validates the password
  - Then the API returns authenticated user data and a JWT token.
- Scenario: Protected request succeeds
  - Given the client has a valid token
  - When the client sends `Authorization: Bearer <token>` to a protected endpoint
  - Then the API processes the request.
- Scenario: Missing or invalid token
  - Given the client calls a protected endpoint without a valid token
  - When the security filter runs
  - Then the API responds `401 Unauthorized`.

---

### TS-SEC002 - Authenticate with Google and Apple

As a frontend developer, I want to authenticate or link a ColdTrace account with Google or Apple so that users can use the existing social login buttons without bypassing ColdTrace organization and role rules.

Status: implemented security ticket.

Contract:

- `POST /api/v1/authentication/social/google/token-exchange`
- `POST /api/v1/authentication/social/apple/token-exchange`
- Request body accepts either `idToken` or `authorizationCode`, plus optional `redirectUri` and `nonce`.
- Success returns the same `AuthenticatedUserResponse` used by email/password sign-in.
- Provider validation failures return `401 PROVIDER_VALIDATION_FAILED`.
- Valid provider identities without a local ColdTrace user return `422 SOCIAL_IDENTITY_REQUIRES_ONBOARDING`.

Acceptance criteria:

- Scenario: Google sign-in for existing user
  - Given a ColdTrace user has a linked Google identity
  - When the backend validates the Google OpenID Connect response
  - Then the API returns the same authenticated user and ColdTrace JWT contract used by email/password sign-in.
- Scenario: Apple sign-in for existing user
  - Given a ColdTrace user has a linked Apple identity
  - When the backend validates the Apple identity response
  - Then the API returns the same authenticated user and ColdTrace JWT contract used by email/password sign-in.
- Scenario: New social identity
  - Given the provider identity does not match an existing ColdTrace user
  - When authentication succeeds at the provider
  - Then ColdTrace requires organization sign-up completion or invitation-based onboarding before operational access.
- Scenario: Provider secrets stay server-side
  - Given Google and Apple provider credentials are configured
  - When the frontend starts the social login flow
  - Then the browser never receives backend client secrets, Apple private keys, or provider token endpoint credentials.

---

### TS-ID001 - Create Organization Sign-Up

As a frontend developer, I want to create an organization and its first administrative user in one backend operation so that registration does not leave partial tenant data.

Acceptance criteria:

- Scenario: Successful sign-up
  - Given `POST /api/v1/organization-sign-ups` receives valid organization and user data
  - When the backend creates the tenant
  - Then the API returns the created organization sign-up result.
- Scenario: Duplicate organization contact email
  - Given another organization already uses the submitted contact email
  - When the request is processed
  - Then the API returns `409 Conflict`.
- Scenario: Duplicate user email
  - Given another user already uses the submitted email
  - When the request is processed
  - Then the API returns `409 Conflict`.

---

### TS-ID002 - Manage Organization Users and Roles

As a frontend developer, I want to list users, create users, and assign existing roles within an organization so that administrators can manage access.

Acceptance criteria:

- Scenario: List users
  - Given `GET /api/v1/organizations/{organizationId}/users`
  - When the organization exists
  - Then the API returns only users from that organization.
- Scenario: Create user
  - Given `POST /api/v1/organizations/{organizationId}/users` receives valid data and an existing role
  - When the backend creates the user
  - Then the API returns the created user resource.
- Scenario: Assign role
  - Given `PATCH /api/v1/organizations/{organizationId}/users/{userId}/role` receives an existing role
  - When the user belongs to the organization
  - Then the API returns the updated user or role assignment result.

---

### TS-AM001 - Manage Locations, Gateways, Assets, Devices, and Settings

As a frontend developer, I want organization-scoped asset management APIs so that the operational dashboard can represent the physical cold-chain network.

Acceptance criteria:

- Scenario: Location ownership
  - Given a request targets `/api/v1/organizations/{organizationId}/locations`
  - When the backend reads or writes a location
  - Then the data belongs to the requested organization.
- Scenario: Gateway and asset placement
  - Given a gateway or asset references a location
  - When the backend validates the request
  - Then the referenced location must belong to the same organization.
- Scenario: Device assignment
  - Given an IoT device references a gateway and optionally an asset
  - When the backend validates the request
  - Then organization and location compatibility are enforced.
- Scenario: Asset settings
  - Given organization default or asset-specific settings are saved
  - When readings and alerts are evaluated
  - Then the backend can resolve effective temperature and humidity thresholds.

---

### TS-MON001 - Record and Generate Sensor Readings

As a frontend developer, I want to create and retrieve sensor readings through the API so that charts and incident detection use backend-owned telemetry.

Acceptance criteria:

- Scenario: Manual reading
  - Given `POST /api/v1/organizations/{organizationId}/sensor-readings` receives valid telemetry
  - When the backend validates device, asset, gateway, and measurement support
  - Then the API stores and returns the reading.
- Scenario: Demo generation
  - Given `POST /api/v1/organizations/{organizationId}/sensor-readings/demo-generations`
  - When eligible online devices exist
  - Then the backend creates realistic demo readings.

---

### TS-AL001 - Manage Incidents and Notifications

As a frontend developer, I want incident APIs for acknowledgement, escalation, corrective action, and resolution so that operators can manage cold-chain exceptions.

Acceptance criteria:

- Scenario: Open incident
  - Given `POST /api/v1/organizations/{organizationId}/incidents` receives valid incident context
  - When references belong to the same operational context
  - Then the API creates the incident.
- Scenario: Acknowledge
  - Given an open incident
  - When the client calls `/acknowledgements`
  - Then the incident records the responsible operator.
- Scenario: Resolve
  - Given the corrective action is provided
  - When the client calls `/resolutions`
  - Then the backend closes the incident according to lifecycle rules.

---

### TS-REP001 - Generate Operational Reports

As a frontend developer, I want report APIs so that compliance users can review cold-chain performance and incidents by period.

Acceptance criteria:

- Scenario: Generate report
  - Given `POST /api/v1/organizations/{organizationId}/reports` receives type, title, and date range
  - When the date range is valid
  - Then the API creates a report resource.
- Scenario: List reports
  - Given `GET /api/v1/organizations/{organizationId}/reports`
  - When reports exist
  - Then the API returns only reports for that organization.

---

### TS-MAINT001 - Manage Maintenance and Technical Service

As a frontend developer, I want APIs for preventive maintenance and technical service requests so that operational work can be scheduled and closed with evidence.

Acceptance criteria:

- Scenario: Schedule maintenance
  - Given a valid asset and future scheduled date
  - When `POST /api/v1/organizations/{organizationId}/maintenance-schedules` is called
  - Then the API creates a maintenance schedule.
- Scenario: Create service request
  - Given an asset and optional incident reference
  - When `POST /api/v1/organizations/{organizationId}/technical-service-requests` is called
  - Then the API creates a technical service request.
- Scenario: Close service request
  - Given required closure data is provided
  - When the status update is processed
  - Then the API applies lifecycle rules and stores closure evidence.

---

### TS-AI001 - Generate Advisory AI Incident Guidance

As a frontend developer, I want AI guidance endpoints to return structured advisory plans so that operators can approve or reject AI recommendations before any incident is closed.

Status: planned Sprint 4 feature.

Acceptance criteria:

- Scenario: Generate plan
  - Given an open incident with related readings and asset context
  - When the client requests AI guidance
  - Then the API returns probable cause, recommended steps, confidence, and evidence references.
- Scenario: Approve plan
  - Given a generated plan is reviewed by an operator
  - When the operator approves it
  - Then the backend applies a real incident update through the incident application service.
- Scenario: Reject plan
  - Given a generated plan is not suitable
  - When the operator rejects it with a reason
  - Then the backend stores the decision without mutating the incident lifecycle.

---

### TS-BILL001 - Manage Plans, Subscriptions, and Stripe Checkout

As a frontend developer, I want backend-owned subscription plan and billing APIs so that plan limits are enforced outside the frontend.

Status: planned Sprint 4 feature.

Acceptance criteria:

- Scenario: Read plan catalog
  - Given the client requests available plans
  - When the backend returns the catalog
  - Then free and paid plans include limits and feature entitlements.
- Scenario: Create checkout session
  - Given an administrator selects a paid plan
  - When the backend creates a Stripe Checkout session
  - Then the frontend receives a provider-hosted redirect URL.
- Scenario: Enforce entitlement
  - Given an organization exceeds a plan limit
  - When a restricted operation is attempted
  - Then the backend rejects the operation even if the frontend button was visible.

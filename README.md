# KuzaHealth Backend

KuzaHealth is a Java 17 and Spring Boot backend for managing maternal and infant health records. It provides REST APIs for health workers, parents, pregnancies and antenatal care, visits, infants, vaccinations and the immunisation schedule, growth monitoring, SMS notifications, reporting, and audit logs, with PostgreSQL persistence and email/SMS integrations.

## Contents

- [Technology](#technology)
- [Local setup](#local-setup)
- [Configuration](#configuration)
- [Docker](#docker)
- [Authentication](#authentication)
- [Roles and access](#roles-and-access)
- [API reference](#api-reference)
- [Features in more detail](#features-in-more-detail)
- [Database migrations](#database-migrations)
- [Project structure](#project-structure)
- [Build and tests](#build-and-tests)
- [Deployment](#deployment)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Technology

| Component | Implementation |
| --- | --- |
| Runtime | Java 17 |
| Framework | Spring Boot 3.3.4 |
| Build | Maven; wrapper downloads Maven 3.9.9 |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL, HikariCP |
| Schema | Flyway migrations in `src/main/resources/db/migration` |
| Authentication | Spring Security, BCrypt passwords, JJWT 0.11.5, refresh tokens |
| API documentation | Springdoc OpenAPI 2.2.0 |
| Messaging | Spring Mail, Pindo SMS through OkHttp |
| Code generation | Lombok and MapStruct |
| Operations | Spring Boot Actuator, Docker, GitHub Actions / Render deploy hook |

## Local setup

### Prerequisites

- JDK 17, with `JAVA_HOME` pointing to your JDK.
- Docker (for the local database), or any reachable PostgreSQL 13+.
- Network access for the first Maven wrapper run and dependency downloads.
- Your own SMTP credentials and Pindo token to exercise OTP delivery and messaging.

Run commands from the repository root. On Windows, use `mvnw.cmd` instead of `./mvnw`.

### 1. Create your `.env`

```bash
cp .env.example .env
```

Fill in at least `JWT_SECRET` (generate one with `openssl rand -base64 48`). `.env` is ignored by git; never commit it.

### 2. Start PostgreSQL

```bash
docker compose up -d db
```

This starts Postgres 16 in the container `kuzahealth-postgres` with the database `kuzahealth`, published on host port **5433** (so it does not clash with a Postgres installed on the host). The user and password come from `POSTGRES_USER` / `POSTGRES_PASSWORD` in `.env`. Data is kept in the `kuzahealth-postgres-data` volume.

### 3. Start the server

Spring Boot does **not** read `.env` by itself when launched with Maven or `java -jar`, so export the variables first:

```bash
set -a; source .env; set +a
./mvnw spring-boot:run
```

On first start Flyway creates the schema. Check the server and explore the API:

```bash
curl http://localhost:8080/api/v1/test/greet
curl http://localhost:8080/actuator/health
```

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [OpenAPI JSON](http://localhost:8080/v3/api-docs)
- [Health](http://localhost:8080/actuator/health)

### First administrator

Public registration can only create `HEALTH_WORKER` accounts. To get an administrator, set `ADMIN_EMAIL` and `ADMIN_PASSWORD` before starting the server; the account is created on startup if it does not exist. You can remove the two variables afterwards.

### Development data

Setting `SPRING_PROFILES_ACTIVE=dev` enables `ParentSeeder`, which inserts 100 generated parents only when the parent table is empty. Its phone numbers and email addresses are made up; do not send notifications to seeded records.

## Configuration

All configuration is in [application.properties](src/main/resources/application.properties) and is driven by environment variables. Nothing secret is stored in the repository.

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `PORT` | `8080` | HTTP port. |
| `SPRING_PROFILES_ACTIVE` | `prod` | `dev` activates the parent seeder. |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/kuzahealth` | PostgreSQL JDBC URL, including SSL options if required. |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | empty | Database credentials. |
| `DB_POOL_SIZE` | `5` | Maximum database connections. |
| `JWT_SECRET` | **required** | Base64 string that decodes to at least 32 bytes. The server refuses to start without it. |
| `JWT_EXPIRATION` | `604800` | Access token lifetime in seconds. |
| `SPRING_MAIL_HOST` / `SPRING_MAIL_PORT` | `smtp.gmail.com` / `587` | Mail transport, used for OTP, password reset and vaccination reminder emails. |
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | empty | Mail credentials. |
| `MAIL_FROM` | mail username | Sender address. |
| `PINDO_TOKEN` | empty | Pindo API token. |
| `PINDO_SENDER` | `PindoTest` | SMS sender ID. |
| `PINDO_APIURL` / `PINDO_BULKAPIURL` | Pindo endpoints | Override the single/bulk SMS endpoints. |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,https://kuzahealth.netlify.app` | Browser origins allowed to call the API, comma separated. |
| `OPEN_REGISTRATION` | `true` | When `false`, only administrators can create accounts. |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | empty | Optional first administrator, created on startup. |
| `REMINDERS_ENABLED` | `false` | Turns on the scheduled SMS reminders (see below). |
| `CASELOAD_ENFORCE` | `false` | Limits health workers to the parents assigned to them (see below). |
| `APP_TIMEZONE` | `Africa/Kigali` | Time zone for schedules and for dates in messages. |
| `GROWTH_REFERENCE_PATH` | `classpath:growth/who/` | Directory with WHO growth standard tables (see below). |
| `LOG_SQL_LEVEL` | `WARN` | Set to `DEBUG` to log SQL. |
| `BACKEND_BASEURL` / `BACKEND_ENDPOINTS_GETPHONENUMBERS` | none | Optional bulk-recipient lookup service for `/api/sms/send-bulk-from-backend`. |

Other tunables (OTP lifetime, rate limits, reminder schedules, antenatal contact weeks, overdue threshold) are documented next to their defaults in `application.properties`.

## Docker

[docker-compose.yml](docker-compose.yml) defines two services:

- `db`: PostgreSQL 16 with a named volume, published on host port 5433 (`DB_HOST_PORT` overrides it).
- `app`: the application, built from the [Dockerfile](Dockerfile). It waits for the database to be healthy and connects to it at `db:5432`.

```bash
docker compose up -d db          # database only
docker compose up --build -d     # database and application
docker compose logs -f app
docker compose down              # stop; add -v to also delete the database volume
```

The app container reads the rest of its settings (JWT, mail, Pindo) from `.env`. The image runs as a non-root user on `eclipse-temurin:17-jre`; use `JAVA_TOOL_OPTIONS` for JVM options.

## Authentication

Signing in takes a password plus a six-digit one-time code, and yields a bearer access token and a refresh token.

### 1. Register an account

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "Test",
    "lastName": "Worker",
    "username": "test.worker",
    "email": "your-account@example.com",
    "password": "at-least-8-characters",
    "phoneNumber": "+250780000000"
  }'
```

The account is created as `HEALTH_WORKER`, with a linked health-worker record. A `role` in the body is honoured only when the request carries an administrator's token. Passwords must be 8 to 72 characters. The response is a text message, not a token.

### 2. Request a one-time code

```bash
curl -X POST http://localhost:8080/api/v1/auth/send-otp \
  -H 'Content-Type: application/json' \
  -d '{"email":"your-account@example.com","password":"at-least-8-characters"}'
```

`email` accepts the registered email address or phone number. The code is sent by email and SMS, is valid for 10 minutes, can be used once, and is discarded after 5 wrong attempts. Unknown accounts and wrong passwords get the same `401` response.

### 3. Exchange the code for tokens

```bash
curl -X POST 'http://localhost:8080/api/v1/auth/login?otp=123456' \
  -H 'Content-Type: application/json' \
  -d '{"email":"your-account@example.com","password":"at-least-8-characters"}'
```

The response contains `token`, `refreshToken`, `expiresIn` (seconds), `email`, `userType` and `message`.

### 4. Call an authenticated endpoint

```bash
export TOKEN='paste-the-returned-token-here'
curl http://localhost:8080/api/v1/auth/profile -H "Authorization: Bearer $TOKEN"
```

### Refresh, logout and password reset

| Endpoint | Purpose |
| --- | --- |
| `POST /api/v1/auth/refresh` with `{"refreshToken": "..."}` | Returns a new access token and a new refresh token. Refresh tokens are single use; presenting a used one signs the user out everywhere. |
| `POST /api/v1/auth/logout` | Revokes the caller's refresh tokens and every access token issued so far. |
| `POST /api/v1/auth/reset-password-request` with `{"email": "..."}` | Emails a reset code valid for 15 minutes. Always answers the same, whether or not the account exists. |
| `POST /api/v1/auth/reset-password` with `email`, `token`, `password`, `confirmPassword` | Sets the new password and ends all sessions. |

Credential endpoints are limited to 10 requests per minute per client IP, and an account is locked for 15 minutes after 10 wrong passwords. These counters are held in memory per application instance.

## Roles and access

| Role | May |
| --- | --- |
| `ADMIN` | Everything, including users, health workers, audit logs, SMS logs, the immunisation schedule, caseload assignment, reminders and Actuator. |
| `HEALTH_WORKER` | Read and write clinical records (parents, pregnancies, infants, visits, vaccinations, growth), send SMS, read aggregated reports. |
| `DATA_ANALYST` | Read clinical records and reports, and download CSV exports. Cannot write. |

Users may read and update their own account; only administrators can change a role or disable an account. Unauthenticated requests get `401`, forbidden ones `403`, both with a JSON body.

Public endpoints: registration, `send-otp`, `login`, `refresh`, the two password-reset endpoints, `/api/v1/test/greet`, Swagger/OpenAPI, `/actuator/health` and `/actuator/info`.

## API reference

Every controller is reachable under both its original path and `/api/v1/...`. Use Swagger UI for request and response schemas.

| Area | Base path | Operations |
| --- | --- | --- |
| Authentication | `/api/v1/auth` | Register, send OTP, log in, refresh, log out, profile, password reset |
| Users | `/api/users` | List, `GET /search`, get, patch, delete |
| Health workers | `/api/health-workers` | CRUD, `GET /search`, `GET /me`, `GET /me/caseload`, `GET /{id}/caseload` |
| Parents | `/api/parents` | `POST /register`, list, `GET /search`, get, update, delete; `PUT /{id}/consent`, `GET /{id}/consents`, `GET /{id}/sms-logs`, `PUT /{id}/assignment` |
| Pregnancy records | `/api/pregnancy-records` | CRUD, by parent, active, weeks; `GET /{id}/anc-plan`, `POST /{id}/anc-plan/schedule`, `GET /high-risk` |
| Visits | `/api/visits` | CRUD, `GET /search`, `GET /upcoming`, `GET /missed`, by patient |
| Visit notes | `/api/visit-notes` | Create, list, get, delete |
| Infants | `/api/infants` | CRUD, `GET /search`, by mother, `GET /{id}/immunisation-schedule` |
| Growth | `/api/v1/infants/{id}/growth`, `/api/v1/growth/{id}` | Record, history, update, delete |
| Vaccinations | `/api/vaccinations` | CRUD, `GET /search`, by infant / health worker / parent, `GET /due`, `POST /notify` |
| Immunisation | `/api/v1/immunisation` | `GET /schedule` (admin: create, update, deactivate), `GET /infants/{id}`, `GET /overdue` |
| Reports | `/api/v1/reports` | `GET /summary`, `/vaccination-coverage`, `/parents-by-district`, `/visits-by-health-worker`, `/export/{dataset}.csv` |
| Reminders | `/api/v1/reminders` | Admin: `GET /status`, `POST /run` |
| Nutrition | `/api/nutrition-info` | POST a nutrition message by SMS |
| SMS | `/api/sms` | `POST /send`, `/send-bulk`, `/send-bulk-from-backend` (admin); `GET /logs` (admin) |
| Audit | `/api/v1/audit` | Admin: `GET /logs`, `GET /logs/search` |
| Recent API logging | `/api/logging` | Admin: `GET /recent` |

**Paging.** The `/search` endpoints take `page` (from 0), `size` (max 100) and `sort` (`field` or `field,desc`) and return `{content, page, size, totalElements, totalPages}`. The plain list endpoints still return everything.

**Errors.** Failed requests return `{error, message, timestamp, status}`; validation failures add `fieldErrors`.

**Deleting.** Parents, infants, visits, visit notes, vaccinations, pregnancy records and growth measurements are soft deleted: they get a `deleted_at` timestamp and vanish from the API, but stay in the database. Deleting a parent or infant also marks everything recorded for them.

### Documentation and monitoring

| Path | Purpose |
| --- | --- |
| `/swagger-ui/index.html` | Interactive API documentation |
| `/v3/api-docs` | OpenAPI JSON |
| `/actuator/health`, `/health/liveness`, `/health/readiness` | Health and probes (public; details only for administrators) |
| `/actuator/metrics` | Metrics (administrators) |

## Features in more detail

### SMS, consent and languages

All SMS go through `NotificationService`. Messages to parents use the templates in `src/main/resources/sms/` in the parent's `preferredLanguage` (`EN` or `RW`), and are not sent when the parent has withdrawn SMS consent. Every message, whether sent, failed or skipped, is written to `sms_log`. "Sent" means the provider accepted it; delivery receipts from the handset are not collected.

Record consent with `PUT /api/parents/{id}/consent` (`{"type": "SMS", "granted": false, "note": "..."}`); each decision is kept as history. Existing parents default to consent given, because they were already being messaged.

The Kinyarwanda templates should be reviewed by a native speaker before going live.

### Scheduled reminders

With `REMINDERS_ENABLED=true`:

- daily at 08:00, mothers are reminded about vaccine doses due within 2 days (or up to 30 days overdue);
- hourly, parents are reminded about visits in the next 24 hours, and visits that were never started 24 hours after their time are marked `Missed` and the parent is asked to reschedule.

This is off by default because it sends real SMS and changes visit statuses. An administrator can run the jobs once with `POST /api/v1/reminders/run`. The timers assume a single application instance.

### Immunisation schedule

`vaccine_schedule_item` holds the routine schedule: each dose and the age in days at which it is due. It is seeded with the Rwanda infant schedule (BCG and OPV0 at birth; OPV, pentavalent, PCV and rotavirus at 6 and 10 weeks; OPV3, pentavalent 3, PCV3 and IPV at 14 weeks; measles-rubella at 9 and 15 months). **Confirm it against the current national schedule**; administrators can change it through the API.

`GET /api/infants/{id}/immunisation-schedule` reports each dose as `GIVEN`, `UPCOMING`, `DUE` or `OVERDUE` (more than 28 days late). Record a dose against the schedule by sending `scheduleCode` (for example `PENTA2`) when creating a vaccination; doses recorded with only a free-text name are matched when the name reads like the code.

### Growth monitoring

Record weight, length, head circumference and MUAC with `POST /api/v1/infants/{id}/growth`. Each measurement comes back with the child's age and screening flags: MUAC cut-offs for acute malnutrition (6 to 59 months), weight loss since the previous measurement, and low birth weight.

Z-scores (weight-for-age, length-for-age, weight-for-length, head circumference) and the underweight, stunting and wasting flags need the WHO Child Growth Standards LMS tables, which are **not bundled**. Put them as CSV files in the directory named by `GROWTH_REFERENCE_PATH`, named `wfa_boys.csv`, `wfa_girls.csv`, `lhfa_*.csv`, `wfl_*.csv` and `hcfa_*.csv`, each with a header row followed by `x,L,M,S` rows (x is age in days, or months if the header starts with `month`; length in cm for `wfl`). Without them the API still works and reports `zScoresAvailable: false`.

### Antenatal care

`GET /api/pregnancy-records/{id}/anc-plan` returns the expected delivery date (LMP + 280 days), gestational age, trimester, risk flags, and the eight recommended antenatal contacts (weeks 12, 20, 26, 30, 34, 36, 38, 40) with their status against visits whose type contains "ANC", "antenatal" or "prenatal". `POST .../anc-plan/schedule` books a visit for each remaining contact. Risk flags are partly derived from keywords in the medical history; they prompt a review and are not a diagnosis.

### Caseloads

A parent can be assigned to a health worker: automatically when a health worker registers her, or by an administrator with `PUT /api/parents/{id}/assignment`. `GET /api/health-workers/me/caseload` summarises a caseload. With `CASELOAD_ENFORCE=true`, health workers only see and edit records of parents assigned to them. Leave it off until existing parents have been assigned, or they become invisible to health workers.

## Database migrations

The schema is owned by Flyway; Hibernate only validates it. `V1__baseline.sql` is the schema as it was before Flyway. A database that predates Flyway is baselined at V1 on first start and then receives the later versions; an empty database gets all of them.

To change the schema, add a new `V<n>__description.sql` file. Never edit a migration that has already been applied.

## Project structure

```text
src/main/java/rw/ac/auca/kuzahealth/
├── KuzahealthServerApplication.java  # Spring Boot entry point
├── controller/                      # REST endpoints and their request/response DTOs
├── core/                            # Domain: entities, services, repositories, DTOs
│   ├── anc/                         #   antenatal care plan and risk flags
│   ├── caseload/                    #   caseload scoping
│   ├── growth/                      #   growth measurements and z-scores
│   ├── immunisation/                #   schedule engine and coverage
│   ├── notification/                #   SMS templates, consent checks, SMS log
│   ├── reminder/                    #   scheduled jobs
│   └── report/                      #   dashboard figures and CSV export
├── security/                        # Filter chain, JWT, rate limiting, admin bootstrap
├── sms/                             # Pindo integration and recipient lookup
├── config/                          # API logging controller
└── utils/                           # Base entities, errors, paging, CSV, mail, CORS
src/main/resources/
├── application.properties           # Runtime configuration
├── db/migration/                    # Flyway migrations
├── mail/                            # Email templates
└── sms/                             # SMS templates (English, Kinyarwanda)
.github/workflows/render-deploy.yml  # Build and Render hook workflow
Dockerfile                           # Multi-stage container build
docker-compose.yml                   # PostgreSQL and application services
```

## Build and tests

```bash
# Package without running tests
./mvnw clean package -DskipTests

# Run the packaged application with your exported configuration
java -jar target/kuzahealth-0.0.1-SNAPSHOT.jar
```

The test suite contains one `@SpringBootTest` context-loading test, which needs a reachable PostgreSQL database and a `JWT_SECRET`. The Docker build and the deployment workflow skip tests.

For IDE development, import `pom.xml`, select Java 17, and enable annotation processing for Lombok and MapStruct.

## Deployment

[The GitHub Actions workflow](.github/workflows/render-deploy.yml) runs on pushes to `main`, packages with tests skipped, and POSTs to the Render deploy hook in the repository secret `RENDER_DEPLOY_HOOK`. The workflow fails if the secret is not set.

Before deploying this version, set these on the hosting service: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `MAIL_FROM` and `PINDO_TOKEN`. They used to be hardcoded in the repository and no longer are, so the application will not start without them. Changing `JWT_SECRET` signs every user out.

On the first start against the existing database, Flyway baselines it and applies the pending migrations. Take a backup first. Verify `/actuator/health` afterwards.

## Troubleshooting

| Symptom | What to check |
| --- | --- |
| Startup fails with `Could not resolve placeholder 'JWT_SECRET'` | Set `JWT_SECRET`; with Maven, export `.env` into the shell first. |
| Startup fails with `JWT_SECRET must decode to at least 32 bytes` | Generate a longer secret: `openssl rand -base64 48`. |
| Startup fails with `Schema-validation` | The database does not match the entities. Check that all Flyway migrations ran and that no table was altered by hand. |
| Database connection refused | Is the `db` container running, and does the URL use port 5433 from the host (or `db:5432` from the app container)? |
| `401` on every request after deploying | Tokens signed with the old key are no longer valid; sign in again. |
| `403` on a write | The account's role does not allow it; see [Roles and access](#roles-and-access). |
| `429 Too Many Requests` | The rate limit for credential or SMS endpoints was reached; wait a minute (an hour for SMS). |
| OTP email is missing | Check the mail credentials and `MAIL_FROM`; failures are logged as errors. |
| Invalid or expired OTP | Codes last 10 minutes, work once, and are discarded after 5 wrong tries. Request a new one. |
| Browser CORS error | Add the origin to `CORS_ALLOWED_ORIGINS`. |
| SMS not arriving | Look at `GET /api/sms/logs` for the status and the provider's response; check the parent's SMS consent and phone number. |
| A health worker sees no parents | `CASELOAD_ENFORCE` is on and no parents are assigned to them. |

## Contributing

1. Follow the existing domain/controller/service/repository organization.
2. Accept request DTOs in controllers, not entities, and keep schema changes in a new Flyway migration.
3. Run the relevant checks against a development database.
4. Update this README when behavior or configuration changes.

Keep credentials, personal data, and private database exports out of the repository.

## License

No project license file is currently included. Confirm usage and redistribution terms with the repository owner.

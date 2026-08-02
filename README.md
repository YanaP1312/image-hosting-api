# Image Hosting API

A backend API for an image hosting application built with Spring Boot. Users can register, log in, upload images, and search/browse a public gallery. Every uploaded image is automatically tagged by an LLM (Google Gemini) in the background.

## Live Demo

- **API:** https://image-hosting-api-amsy.onrender.com
- **Swagger UI:** https://image-hosting-api-amsy.onrender.com/swagger-ui/index.html

> Hosted on Render's free tier — the service may take 30–50 seconds to wake up on the first request after a period of inactivity.

## Features

- User registration, login, and logout
- Image upload (up to 10MB, JPEG/PNG)
- Users can view and delete their own images
- Public search by free text across image tags, objects, and colors
- Public gallery (paginated, 50 images per page by default)
- Each image has a direct URL that renders it in the browser
- AI image tagging (objects / tags / colors) via Google Gemini, run asynchronously in the background so uploads never block on it
- Tagging status tracking (`PENDING` / `COMPLETED` / `FAILED`) per image

## Tech Stack

- **Language / Framework:** Java 25, Spring Boot 4
- **Database:** PostgreSQL (via `JdbcClient`, no JPA/Hibernate)
- **Migrations:** Flyway
- **Object Storage:** Backblaze B2 (S3-compatible), accessed via the AWS SDK for Java
- **AI:** Google Gemini (`gemini-3.5-flash-lite`) via the Google GenAI Java SDK
- **Auth:** Custom opaque session tokens (not JWT), bcrypt for password hashing
- **API docs:** springdoc-openapi / Swagger UI
- **CI/CD:** GitHub Actions (tests, Checkstyle, Docker image build & push to GitHub Container Registry)
- **Deployment:** Docker container on Render

## Architecture Notes

**Opaque sessions, not JWT.** On login, a random 256-bit token is generated (`SecureRandom`), returned to the client, and its SHA-256 hash is stored in the `sessions` table alongside the user ID and an expiration timestamp. A custom Spring Security filter looks up the hashed token on every request. Nothing about the session's contents is ever encoded into the token itself — it's just a random lookup key.

**Private bucket + proxy endpoint.** Uploaded files are stored in a private Backblaze B2 bucket, never exposed with a public URL. `GET /images/{id}` acts as a proxy: the server authenticates to Backblaze with its own credentials, fetches the bytes, and streams them back with the correct `Content-Type` — so the endpoint behaves like a normal direct image URL from the outside, while access stays fully server-controlled.

**JSONB for tags.** Each image's AI-generated tags (`objects`, `tags`, `colors`) are stored as a single JSONB column, letting each category hold a variable number of values without a rigid relational schema. Free-text search uses `ILIKE` against the JSONB cast to text.

**Async AI tagging.** Tagging happens in a `@Async` method after the image record is created, so `POST /images` always returns immediately with `tags: null` and `tagging_status: PENDING`. The tagging service retries once on transient failures before marking the image as `FAILED`.


### Database Schema

| Table | Key columns |
|---|---|
| `users` | id, name, email, password_hash, created_at |
| `sessions` | id (hashed token), user_id, created_at, expires_at |
| `images` | id, user_id, storage_key, content_type, tags (JSONB), tagging_status, created_at |


## Getting Started Locally

### Prerequisites

- Docker & Docker Compose
- JDK 25
- A Backblaze B2 account (private bucket + application key)
- A Google AI Studio API key ([aistudio.google.com](https://aistudio.google.com))

### 1. Clone the repository

```bash
git clone https://github.com/YanaP1312/image-hosting-api.git
cd image-hosting-api
```

### 2. Set required environment variables

The app needs the following environment variables. Locally, the easiest way is to set them in your IDE's Run Configuration (or export them in your shell before running).

| Variable | Description | Where to get it |
|---|---|---|
| `B2_KEY_ID` | Backblaze B2 application key ID | Backblaze account → App Keys → Add a New Application Key |
| `B2_APPLICATION_KEY` | Backblaze B2 application key secret | Same as above (shown once at creation) |
| `B2_BUCKET_NAME` | Name of your private B2 bucket | Backblaze account → Buckets |
| `GEMINI_API_KEY` | Google Gemini API key | [aistudio.google.com](https://aistudio.google.com) → Get API key |

Local Postgres credentials (`admin` / `password` / `mydb`) are already committed in `docker-compose.yml` and `application-dev.yaml` — this is intentional and safe, since this database is never exposed outside your machine. Production credentials are never committed and are supplied only via environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) on the deployment platform.

### 3. Start the database

```bash
docker-compose up -d db
```

### 4. Run the application

Run `HostingApplication` from your IDE with the `dev` profile active, or:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Flyway will automatically create all tables on first startup.

### 5. (Optional) Run the full stack in Docker

To verify the whole stack — app + database — works together exactly as it would in production:

```bash
docker-compose up -d --build
```

This uses the `docker` Spring profile, under which the app connects to the `db` service by its container name rather than `localhost`.

## API Documentation

Full interactive documentation is available via Swagger UI once the app is running:

```
http://localhost:8080/swagger-ui/index.html
```

To test protected endpoints, log in via `POST /auth/login`, copy the returned token, and paste it into the **Authorize** button at the top of the page (no need to type "Bearer" — Swagger adds it automatically).

<img width="1431" height="795" alt="Знімок екрана 2026-08-02 о 15 25 51" src="https://github.com/user-attachments/assets/b2a92d3f-6972-4e77-b022-1c8d43d12d30" />


## Known Limitations

- **AI tagging free-tier limits.** Gemini's free tier used for this project is capped at roughly 20 requests/minute and 500 requests/day. This is expected for a project of this scope; a production deployment would use a paid tier or a request queue.
- **Swagger UI padlock icons.** Every endpoint shows an unlocked padlock icon, including protected ones, regardless of authentication state. This is a known `springdoc-openapi` limitation with overriding the global security requirement per-operation — it's purely cosmetic. Actual authorization is enforced entirely by the server's Spring Security filter chain, independent of what the UI displays.
- **Image proxy response schema.** `GET /images/{id}` returns raw image bytes (`ResponseEntity<byte[]>`), which `springdoc-openapi` renders as a generic `string` in its schema regardless of manual overrides — another known library limitation, not a functional issue.
- **Test coverage.** Given the project timeline, priority was given to completing all required features end-to-end. Test coverage is currently limited to a basic Spring context check and a small set of unit tests for token generation/hashing. Expanding coverage is a planned next step.
- **Checkstyle.** Checkstyle (Google style) is wired into the CI pipeline but configured as non-blocking, since fully resolving all violations (many of which are Javadoc requirements) wasn't practical within the project timeline.

## CI/CD

Every push and pull request triggers a GitHub Actions workflow that:

1. Spins up a Postgres service container
2. Runs Checkstyle (non-blocking)
3. Runs the test suite
4. On pushes to `main`: builds the Docker image and pushes it to GitHub Container Registry (`ghcr.io`)

Render is configured to pull the published image from `ghcr.io` on each deploy.

## Possible Future Improvements

- Public/private visibility for images
- Thumbnail generation for fast previews
- Simple front-end
- Expanded test coverage

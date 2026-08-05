# Docker Deployment

## Run Locally With Docker Compose

Create a local `.env` file from `.env.example`, then set a real database password.

```powershell
Copy-Item .env.example .env
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

## Build Only The App Image

```powershell
docker build -t eduguide:latest .
docker run --rm -p 8080:8080 `
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/eduguide" `
  -e SPRING_DATASOURCE_USERNAME="eduguide" `
  -e SPRING_DATASOURCE_PASSWORD="your_password" `
  eduguide:latest
```

## Deploy To A Server

1. Install Docker and Docker Compose on the server.
2. Copy the project to the server, or push it to GitHub and clone it there.
3. Create a `.env` file on the server with production values.
4. Start the app:

```bash
docker compose up -d --build
```

5. Check logs:

```bash
docker compose logs -f app
```

## Deploy With Your Neon Database

If you want to use Neon instead of the local Compose PostgreSQL container, set these environment variables on your host or deployment platform:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://your-neon-host/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
```

Do not commit real database passwords to Git.

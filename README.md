# InterviewHub (Mock Interview Matcher)

A backend platform that helps software engineers discover suitable mock interview partners based on role compatibility, skills, experience, and timezone. The platform provides personalized recommendations, secure authentication, profile management, and Google Calendar integration for interview scheduling.

---

## Features

### Smart Matching
- Weighted compatibility engine based on:
  - Current & target roles
  - Skill overlap
  - Years of experience
  - Timezone compatibility
- Explainable match scores with compatibility breakdowns
- Bidirectional mentor ↔ candidate matching
- Personalized interview partner recommendations

### Search & Discovery
- Advanced filtering by
  - Skills
  - Company
  - Current role
  - Target role
  - Experience
  - Timezone
  - Availability
- Pagination support
- Profile completion scoring

### Authentication
- JWT authentication
- Refresh token rotation
- HTTP-only cookie support
- Email verification
- Password reset
- Password change
- Secure logout with token revocation

### Google Calendar Integration
- Google OAuth 2.0 authentication
- Calendar connection management
- Automatic interview scheduling
- Event synchronization

---

# Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| API Docs | OpenAPI (Swagger) |
| Authentication | JWT, OAuth 2.0 |
| Mail | Spring Mail |
| Build Tool | Maven |
| Validation | Jakarta Validation |

---

# Architecture

```
                Client
                   │
                   ▼
        Spring Security Filter
                   │
                   ▼
             REST Controllers
                   │
                   ▼
               Service Layer
      ┌──────────┼───────────┐
      ▼          ▼           ▼
 Authentication Matching   Calendar
      │          │           │
      ▼          ▼           ▼
        Repository Layer (JPA)
                   │
                   ▼
              PostgreSQL
```

---

# Matching Strategy

The recommendation engine computes a weighted compatibility score using multiple signals.

| Signal | Weight |
|---------|--------|
| Skills | 40% |
| Role Compatibility | 35% |
| Experience | 15% |
| Timezone | 10% |

The system supports:

- Role based mentor matching
- Peer matching
- Reverse matching
- Skill overlap analysis
- Explainable recommendation breakdowns

---

# REST APIs

## Authentication

```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/refresh-token
POST   /api/auth/logout
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
POST   /api/auth/change-password
POST   /api/auth/resend-verification
GET    /api/auth/verify-email
```

---

## Profile

```
GET    /api/profile
POST   /api/profile
PUT    /api/profile
PATCH  /api/profile/availability
PATCH  /api/profile/deactivate
PATCH  /api/profile/reactivate
GET    /api/profile/completion-score
```

---

## Matching

```
GET /api/matches/suggestions
GET /api/matches/search
GET /api/matches/by-skills
GET /api/matches/by-target-role
GET /api/matches/reverse
GET /api/matches/same-timezone
GET /api/matches/similar-experience
GET /api/matches/user/{id}
```

---

## Skills

```
GET    /api/skills
GET    /api/skills/{id}
GET    /api/skills/categories
POST   /api/profile/skills
DELETE /api/profile/skills/{id}
GET    /api/profile/skills
```

---

## Google Calendar

```
GET    /api/oauth/google/authorize
GET    /api/oauth/google/callback
POST   /api/oauth/google/disconnect
GET    /api/oauth/status
POST   /api/oauth/test-connection
```

---

# Security

- JWT-based authentication
- Refresh token rotation
- HTTP-only cookies
- BCrypt password hashing
- Email verification workflow
- Password reset workflow
- OAuth 2.0 integration
- Role-based endpoint protection
- Stateless session management

---

# Running Locally

Clone the repository

```bash
git clone https://github.com/yourusername/mock-interview-matcher.git
cd mock-interview-matcher
```

Configure your environment variables.

```properties
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=

JWT_SECRET=

GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

MAIL_USERNAME=
MAIL_PASSWORD=
```

Run the application

```bash
mvn spring-boot:run
```

---

# API Documentation

After starting the server:

```
http://localhost:8080/swagger-ui/index.html
```

---

# Project Structure

```
src
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
├── service
├── util
└── validation
```

---

# Future Improvements

- Redis caching
- Kafka event processing
- Elasticsearch based search
- Docker deployment
- GitHub Actions CI/CD
- Prometheus & Grafana monitoring
- Unit & Integration testing
- WebSocket notifications

---

# License

This project is licensed under the MIT License.

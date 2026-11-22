# Junnar Tourist Guide — Backend

REST API for Junnar Tourist Guide, a tourism app for Junnar, Maharashtra. Built with Spring Boot + MongoDB.

Frontend: [Junnar_tourist_guide_frontend](https://github.com/saipardeshi/Junnar_tourist_guide_frontend) · Live: https://junnar-tourist-guide-frontend.vercel.app

## Tech Stack

- Java 17, Spring Boot 3.2.5
- MongoDB (Spring Data MongoDB, Atlas)
- Spring Security + JWT (jjwt), BCrypt password hashing
- Lombok, Maven
- Docker

## Getting Started

1. Create `src/main/resources/application.properties` (git-ignored, never commit it):

   ```properties
   spring.application.name=tourist-guide
   server.port=8080
   spring.data.mongodb.uri=mongodb+srv://<user>:<password>@<cluster>/<database>
   jwt.secret=<long-random-secret>
   jwt.expiration=86400000
   ```

2. Generate a JWT secret: `openssl rand -base64 48`
3. Run: `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`). API at `http://localhost:8080`
4. Health check: `GET /api/health`

Docker:

```bash
docker build -t junnar-tourist-guide-backend .
docker run -p 8080:8080 junnar-tourist-guide-backend
```

Pass the MongoDB URI and JWT secret to the container, since `application.properties` is not committed.

## Startup Data

On startup the app seeds the starter catalogue of Junnar places (forts, caves, temples, nature spots) and sample hotels. Hotels are only seeded when the collection is empty.

## Features

- Register / login with JWT, role-based access (`USER` / `ADMIN`)
- Places: list, detail, category filter, name search
- Reviews with per-place rating stats
- Hotels (filter by type) and events (filter by month / tag)
- Saved itineraries per user
- User profile and favourite places
- Admin APIs: stats, users, manage places / events / reviews
- CORS enabled for local dev (`localhost:5173`) and the deployed frontend

## API Notes

Base URL: `/api`

| Area | Endpoints |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login` |
| Places | `GET /places`, `/places/{id}`, `/places/category/{category}`, `/places/search?name=` |
| Hotels | `GET /hotels`, `/hotels/{id}`, `/hotels/type/{type}` |
| Events | `GET /events`, `/events/{id}`, `/events/month/{month}`, `/events/tag/{tag}` |
| Reviews | `GET /reviews/place/{placeId}`, `/reviews/stats/{placeId}`; `POST /reviews`; `DELETE /reviews/{id}` |
| Itineraries | `GET / POST /itineraries`, `DELETE /itineraries/{id}` |
| User | `GET / PUT /user/profile`, `GET /user/favourites`, `POST /user/favourites/{placeId}` |
| Admin | `GET /admin/stats`, `/admin/users`; `POST / PUT / DELETE /admin/places`; `POST / DELETE /admin/events`; `DELETE /admin/reviews/{id}` |

Send the token as `Authorization: Bearer <token>` for protected routes.

## Known Gaps

- Places are cleared and re-seeded on every startup, so places added through the admin panel are lost on restart
- No `/api/contact` endpoint yet (the frontend contact form calls it)
- No refresh tokens
- Only a context-load test; no unit or integration tests yet
- No Docker Compose / CI pipeline
- Spring Boot 3.2.x is past community support — plan an upgrade

## Author

Sai Pardeshi — [GitHub](https://github.com/saipardeshi) · [LinkedIn](https://linkedin.com/in/sai-pardeshi)

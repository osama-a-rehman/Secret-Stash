
# Secret Stash

## How to run?
### 1. Clone the repository:

     git clone https://github.com/your-username/secret-stash.git cd secret-stash  

### 2. Run the application:
#### a) In Development Envrionment:
1. Run the application:

        ./gradlew bootRun  

#### b) In Staging/Production Envrionment:
1. Run `postgres` container from `docker-compose.yaml`.
2. Add environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`.
3. Setup `stage` Spring Profile, e.g. via `VM_Options`: `-Dspring.profiles.active=stage`.

        ./gradlew bootRun  

## Architectural / Design Decisions:
1. **Monolithic architecture:** A monolithic architecture has been chosen to keep the application simple and due to time constraints. For Production, having an `api-gateway` for Rate Limiting alongwith `auth-service` and `note-service` that handles Authentication and Business Logic to separate concerns will be better.
2. **Security**: Written `JwtService` that generates JWTs. To verify JWTs, I utilize Spring Security's Oauth resource server utility. The end-points for Authentication are part of `AuthService` which include `/login`, `/register`, `logout`, and `/refresh-token`. To refresh access tokens, `Redis` is being used.
3. **Database Setup**: Setup the `liquibase` based migration for easier database setup. In Development, an in-memory database `H2` is being used, in production the application can be deployed with the `postgres` database that can be run in the docker container from `docker-compose.yaml`.
4. **Auditing with JPA**: To delegate populating `createdAt` and `lastModifiedAt` for all entities, JPA Auditing has been used in a `BaseModel`, all JPA entities extend from this model.
5. **Redis based Rate-Limiting**: Used `Bucket4j` library with `Redis` to implement **per-user and anonymous rate limiting** via `Filter` in Spring that uses the `Token Bucket algorithm` . For production, it would be better to move Rate Limiting to the API-Gateway for better separation of concern.
   The following properties control Rate Limiting:
   - `app.rate-limiter.capacity`: Total capacity of tokens a bucket can hold.
   - `app.rate-limiter.replenish-rate`: The number of tokens added in given time.
   - `app.rate-limiter.replenish-in-seconds`: The number of seconds after which a bucket is replenished with `replenish-rate` number of tokens.
6. **Project Organization**:  The project is organized in the following packages:
   - **auth**: Handles authentication and security-related components.
   - **config**: Contains security and web configuration files.
   - **exception**: Defines custom exceptions and global exception handling.
   - **notes**: Manages note-related business logic.
   - **rate_limiter**: Implements request rate limiting using Bucket4j.
   - **shared**: Contains shared utilities used across the application.
   - **user**: Manages user-related business logic.
   - **validator**: Defines custom validation annotations and their implementations.
   - **controller**: Exposes REST endpoints and maps HTTP requests to services.
   - **service**: Contains business logic for application features.
   - **repository**: Interfaces with the database for CRUD operations.
   - **dto**: Defines Data Transfer Objects for request and response payloads.
7. **Swagger UI API documentation**: The API documentation is made available on `/swagger-ui.html` utilizing Swagger UI.
8. **Testing**: Key functionality has been tested. The integration tests that utilizes `MockMvc` to tests end-points are contained in `AuthControllerIT.kt` and `NoteControllerIT.kt`. The unit tests that tests the functionality of NoteService and NoteRepository on a granular level are contained in `NoteServiceTest.kt` and `NoteRepositoryTest.kt` respectively.

## API Documentation:
| Method | Endpoint                      | Description                                                              |
|--------|-------------------------------|--------------------------------------------------------------------------|
| POST   | `/api/notes`                    | Creates a Note                                                           |
| DELETE | `/api/notes/{id}`              | Deletes a Note                                                           |
| PATCH  | `/api/notes/{id}`              | Updates properties `title`, `content`, and `expiry` of a note            |
| GET    | `/api/notes/latest-1000`       | Gets latest 1000 notes                                                   |
| POST   | `/api/auth/register`           | Registers a user                                                         |
| POST   | `/api/auth/refresh-token`      | Refreshes the access token using a refresh token                         |
| POST   | `/api/auth/logout`             | Logs out the user by revoking the refresh token                          |
| POST   | `/api/auth/login`              | Logs in the user                                                         |
| GET    | `/api/users/profile`           | Retrieves the user profile / information                         |

# Secret Stash

## How to run?
### 1. Clone the repository:

     git clone https://github.com/your-username/secret-stash.git cd secret-stash  

### 2. Run the application:
#### a) In Development Envrionment:
1. **Start Redis Container**:

        docker-compose up redis    

3. **Run the application:**

        ./gradlew bootRun  

#### b) In Staging/Production Envrionment:
1. Run `postgres` container from `docker-compose.yaml`.
2. Add environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`.
3. Setup `stage` Spring Profile, e.g. via `VM_Options`: `-Dspring.profiles.active=stage`.


       ./gradlew bootRun 

## Tech Stack and Dependencies
The following libraries and frameworks (alongwith their purpose) were used in this project:
- **Kotlin** – Primary programming language
- **Spring Boot** – Application framework for building the REST API
- **Spring Security** – Authentication
- **JWT (jjwt)** – JSON Web Token generation
- **Spring Data JPA** – ORM layer for database interactions
- **Liquibase** – For database versioning
- **H2 Database** – In-memory database for development and testing
- **Postgres** – Persistent database for production
- **Redis (via Jedis)** – Used for storing and managing refresh tokens and rate limiting
- **Bucket4j** – Provides Rate Limiting algorithm and sets up Rate Limiting strategy.
- **Swagger UI** – For API Documentation
- **JUnit 5 & Spring Test** – Unit and integration testing
- **MockMvc** – HTTP-level testing of controllers
- **Strikt** – For Assertions in tests

## Authentication Flow:
The application uses **JWT-based authentication** with support for access and refresh tokens to secure user sessions. The following endpoints are available to manage authentication:

### 1. `/api/auth/register`
-   **Purpose**: Registers a new user and automatically logs them in.
-   **Input**: `username`, `password`, `name`
-   **Response**: Returns a pair of tokens:
   -   `accessToken`: Used to access protected resources.
   -   `refreshToken`: Used to obtain new access tokens when the current one expires.
-   **Note**: Passwords are securely hashed before storage.

### 2. `/api/auth/login`
-   **Purpose**: Authenticates an existing user using `username` and `password`.
-   **Response**: Returns a new `accessToken` and `refreshToken` pair.

### 3. `/api/auth/refresh-token`
-   **Purpose**: Issues a new `accessToken` when the previous one has expired.
-   **Input**: A valid (non-expired) `refreshToken`.
-   **Response**: A new `accessToken`.
> ⚠️ Access tokens have a default expiry of **3 minutes**. Refresh tokens are valid for **1 day**, and can be used to obtain new access tokens until they expire or are revoked.

### 4. `/api/auth/logout`
-   **Purpose**: Logs out the user by revoking the provided `refreshToken`.
-   **Effect**: Once revoked, the refresh token cannot be used to generate new access tokens.

## Architectural / Design Decisions:
1. **Monolithic architecture:** A monolithic architecture has been chosen to keep the application simple and due to time constraints. For Production, having an `api-gateway` for Rate Limiting alongwith `auth-service` and `note-service` that handles Authentication and Business Logic to separate concerns will be better.
2. **Security**: Written `JwtService` that generates JWTs utilizing `jjwt` library. To verify JWTs, I utilize Spring Security's Oauth Resource Server utility. The end-points for Authentication are part of `AuthService` which include `/login`, `/register`, `logout`, and `/refresh-token`. To refresh access tokens, `Redis` is being used.
3. **Database Setup**: Setup the `liquibase` based migration for easier database setup. In Development and Testing, an in-memory database `H2` is being used for faster development and test runs. However, in production the application can be deployed with the persistent `postgres` database that can be run in the docker container from `docker-compose.yaml`.
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
8. **Testing**: Key functionality has been tested. The integration tests that utilizes `MockMvc` to tests end-points are contained in `AuthControllerIT.kt` and `NoteControllerIT.kt`. The unit tests that tests the functionality of NoteService and NoteRepository on a granular level are contained in `NoteServiceTest.kt` and `NoteRepositoryTest.kt` respectively. Utilizes `mockito-kotlin` for mocking, `strikt` for assertions, and `test-containers` to setup a docker container for `Redis` in the testing environment.
9. **Request Data Validation**:  Each Request DTO being used in Controller is annotated with `@Valid` to validate it with `spring-boot-starter-validation`.

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
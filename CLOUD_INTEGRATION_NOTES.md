# Conceptual Design for Cloud Backend Integration

This document outlines a conceptual design for integrating the web application with a cloud-based backend. This would involve moving beyond the current servlet-based in-memory storage to a more robust and scalable solution using cloud services.

## 1. Key Entities and Operations

The core entities and their associated operations for the platform are:

*   **Users:**
    *   Registration: Create a new user account.
    *   Login: Authenticate an existing user.
    *   (Future: Profile Update, Password Reset)
*   **Educational Posts:**
    *   Create: Add a new educational article/post.
    *   Read: Retrieve a list of posts or a specific post.
    *   Update: Modify an existing post.
    *   Delete: Remove a post.
*   **DSA Problems/Quizzes:**
    *   Create: Add a new DSA problem or quiz.
    *   Read: Retrieve a list of problems/quizzes or a specific one (e.g., problem of the day).
    *   Submit Answer: Allow users to submit their solution/answers.
    *   View Score: Allow users to see their score for a quiz/problem.
*   **Polls:**
    *   Create: Add a new poll.
    *   Read: Retrieve a specific poll.
    *   Vote: Allow users to cast a vote on a poll.
    *   View Results: Display the results of a poll.
*   **Events:**
    *   Create: Add a new event (e.g., webinar, coding session).
    *   Read: Retrieve a list of events or a specific event.
    *   RSVP: Allow users to confirm their attendance for an event.

## 2. Basic RESTful API Endpoints (Conceptual)

The following RESTful API endpoints are proposed to support the operations listed above. These would typically be prefixed with `/api`.

**Users:**
*   `POST /users/register` - Register a new user.
*   `POST /users/login` - Log in an existing user (could return a session token).
*   `GET /users/me` - Get current user's profile (requires authentication).
*   `PUT /users/me` - Update current user's profile (requires authentication).

**Educational Posts:**
*   `POST /posts` - Create a new educational post (requires authentication, role-based).
*   `GET /posts` - Get a list of all posts (publicly accessible or paginated).
*   `GET /posts/{postId}` - Get a specific post by its ID (publicly accessible).
*   `PUT /posts/{postId}` - Update an existing post (requires authentication, author/admin).
*   `DELETE /posts/{postId}` - Delete a post (requires authentication, author/admin).

**DSA Problems/Quizzes:**
*   `POST /dsa/problems` - Create a new DSA problem (requires authentication, role-based).
*   `GET /dsa/problems` - Get a list of available DSA problems.
*   `GET /dsa/problems/today` - Get the "problem of the day".
*   `GET /dsa/problems/{problemId}` - Get a specific DSA problem.
*   `POST /dsa/problems/{problemId}/submit` - Submit an answer/solution for a problem (requires authentication).
*   `GET /dsa/problems/{problemId}/submissions/{submissionId}` - View result/score for a specific submission.
*   `POST /dsa/quizzes` - Create a new quiz.
*   `GET /dsa/quizzes/{quizId}` - Get a specific quiz.
*   `POST /dsa/quizzes/{quizId}/submit` - Submit answers for a quiz.
*   `GET /dsa/quizzes/{quizId}/results` - View overall results/scores for a quiz.

**Polls:**
*   `POST /polls` - Create a new poll (requires authentication).
*   `GET /polls` - Get a list of active polls.
*   `GET /polls/{pollId}` - Get details of a specific poll.
*   `POST /polls/{pollId}/vote` - Vote on a specific poll (requires authentication, one vote per user).
    *   Request body might include: `{"optionId": "selected_option_id"}`
*   `GET /polls/{pollId}/results` - View the results of a poll.

**Events:**
*   `POST /events` - Create a new event (requires authentication, role-based).
*   `GET /events` - Get a list of upcoming events.
*   `GET /events/{eventId}` - Get details of a specific event.
*   `POST /events/{eventId}/rsvp` - RSVP for an event (requires authentication).
    *   Request body might include: `{"status": "attending|not_attending|maybe"}`

## 3. Data Format

*   **JSON (JavaScript Object Notation)** will be the primary data interchange format for all API request and response bodies.
*   Clients should set `Content-Type: application/json` for requests with a body.
*   Clients should set `Accept: application/json` to indicate they expect JSON responses.

## 4. Authentication for APIs

*   **Token-Based Authentication (JWT):** For a stateless cloud backend, JSON Web Tokens (JWT) are recommended.
    *   Upon successful login (`POST /api/users/login`), the server would generate a JWT and return it to the client.
    *   The client would then include this JWT in the `Authorization` header (e.g., `Authorization: Bearer <token>`) for all subsequent requests to protected API endpoints.
*   **API Authentication Filter:** The existing `AuthenticationFilter` could be significantly modified, or a new dedicated filter (e.g., `ApiTokenFilter`) could be created to:
    *   Inspect the `Authorization` header for a JWT.
    *   Validate the token (signature, expiration).
    *   If valid, extract user information from the token and potentially set it as a request attribute for downstream processing.
    *   If invalid or missing, return a `401 Unauthorized` or `403 Forbidden` HTTP status code.

## 5. Refactoring Existing Servlets (Conceptual)

The current `RegistrationServlet` and `LoginServlet` are designed for traditional web form submissions and session management. To support API endpoints:

*   **Option 1: Augment Existing Servlets:**
    *   Modify them to check request headers like `Content-Type` or `Accept`.
    *   If `application/json` is indicated, they would:
        *   Read request data from the request body (as JSON) instead of form parameters.
        *   Return JSON responses (e.g., user data or success/error messages) instead of HTML redirects.
        *   For login, return a JWT instead of creating an HTTP session.
*   **Option 2: Separate API Controllers/Servlets (Recommended):**
    *   Create new servlets or, more commonly in modern Java frameworks, JAX-RS resource classes or Spring MVC Controllers dedicated to handling API requests (e.g., `UserApiServlet`, `PostApiServlet`).
    *   This approach provides better separation of concerns between the traditional web UI flow and the REST API.
    *   Example: `UserApiServlet` would handle `/api/users/register` and `/api/users/login`.

## 6. Benefits of a Cloud Backend

Integrating with a cloud backend offers numerous advantages:

*   **Scalability:** Cloud platforms allow for easy scaling of resources (compute, database) based on demand.
*   **Managed Database Services:** Cloud providers offer managed database services (e.g., AWS RDS, Google Cloud SQL, Azure Database) that handle backups, patching, and maintenance, reducing operational overhead. Options include SQL (PostgreSQL, MySQL) or NoSQL (MongoDB, DynamoDB) databases.
*   **Serverless Functions (e.g., AWS Lambda, Google Cloud Functions, Azure Functions):**
    *   Specific, event-driven tasks like processing quiz submissions, sending email notifications, or resizing images can be implemented as serverless functions.
    *   This can be cost-effective and highly scalable.
*   **Reliability and Availability:** Cloud providers offer high availability and data redundancy.
*   **Global Reach:** Easily deploy the application closer to users across different geographical regions.
*   **Rich Ecosystem:** Access to a wide range of other services like object storage (for images, videos), CDN, message queues, analytics, and machine learning services.

This conceptual design provides a roadmap for evolving the application towards a modern, cloud-native architecture.

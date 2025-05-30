# Backend Development Notes

This document covers specific backend setup and considerations, especially regarding Firebase Admin SDK integration and API authentication.

## 1. Database Setup (H2)

*   The application uses an H2 in-memory/file-based database for local development.
*   Database schema initialization is handled by `com.example.util.DatabaseManager` upon application startup, triggered by `com.example.listener.AppContextListener`.
*   Tables created:
    *   `users`: For the original JSP-based authentication (username, password hash, email).
    *   `user_profiles`: For storing user profile information linked to Firebase UIDs.
*   **H2 Console:** The H2 web console is started automatically by `AppContextListener` and is typically available at `http://localhost:8082`.
    *   Default JDBC URL to use in console: `jdbc:h2:./target/webappdb`
    *   Default User: `sa`
    *   Default Password: (empty)

## 2. Firebase Admin SDK Setup (Backend)

The Firebase Admin SDK is used on the backend, primarily for verifying Firebase ID tokens sent by the frontend. This allows the backend to securely identify the Firebase-authenticated user making API requests.

**Crucial Setup Steps:**

1.  **Obtain Service Account Key:**
    *   Go to your Firebase project console: [https://console.firebase.google.com/](https://console.firebase.google.com/)
    *   Select your project.
    *   Navigate to **Project settings** (click the gear icon ⚙️ near "Project Overview").
    *   Go to the **Service accounts** tab.
    *   Click on **"Generate new private key"** and confirm. A JSON file containing your service account key will be downloaded.

2.  **Place and Rename Key File:**
    *   **Rename** the downloaded JSON file to exactly `firebase-service-account-key.json`.
    *   Place this `firebase-service-account-key.json` file into the `src/main/resources` directory of this Java project. The application is configured to load it from the classpath.

3.  **How it's Initialized:**
    *   The `com.example.listener.AppContextListener` attempts to initialize the Firebase Admin SDK during application startup using this key file.
    *   If the file is not found or is invalid, the Firebase Admin SDK will not initialize, and backend API calls requiring Firebase authentication will fail. Check the server logs for error messages related to Firebase initialization.

## 3. API Authentication with Firebase ID Tokens

The backend uses a token-based authentication strategy for its RESTful APIs (endpoints under `/api/*`).

1.  **Frontend Responsibility:**
    *   After a user successfully logs in or registers using the Firebase Authentication SDK on the frontend (e.g., in `login_firebase.html`), the frontend must obtain the user's Firebase ID Token.
    *   For every request made to a protected backend API endpoint (e.g., `/api/user/profile`), the frontend must include this ID Token in the `Authorization` header with the "Bearer" scheme.
    *   Example Header: `Authorization: Bearer <FIREBASE_ID_TOKEN_HERE>`

2.  **Backend Verification (`FirebaseTokenFilter`):**
    *   The `com.example.filter.FirebaseTokenFilter` is mapped to `/api/*` in `web.xml`.
    *   This filter intercepts all requests to these API endpoints.
    *   It extracts the ID token from the `Authorization` header.
    *   It uses `FirebaseAuth.getInstance().verifyIdToken(idToken)` to verify the token's validity and signature.
    *   If the token is valid, the filter sets a request attribute `firebaseUser` (of type `com.google.firebase.auth.FirebaseToken`) containing the decoded token information (like UID, email, name). The request is then allowed to proceed to the target servlet (e.g., `UserProfileServlet`).
    *   If the token is missing, malformed, or invalid (e.g., expired, incorrect signature), the filter returns a `401 Unauthorized` HTTP status code with a JSON error message, and the request does not reach the servlet.

## 4. User Profile API (`/api/user/profile`)

*   **Endpoint:** `/api/user/profile`
*   **Protection:** This endpoint is protected by `FirebaseTokenFilter`, so a valid Firebase ID token is required in the `Authorization` header.
*   **Servlet:** `com.example.servlet.UserProfileServlet`
*   **Functionality:**
    *   **`GET /api/user/profile`**:
        *   Retrieves the authenticated user's Firebase UID from the `firebaseUser` request attribute (set by the filter).
        *   Fetches the user's profile from the `user_profiles` table using `UserProfileDao`.
        *   If a profile exists, it's returned as JSON.
        *   If no profile exists for the UID, a new basic profile is created using information from the Firebase token (UID, email, display name if available in token), saved to the database, and then returned as JSON with a `201 Created` status.
    *   **`POST /api/user/profile`**:
        *   Allows the authenticated user to create or update their profile.
        *   Reads JSON data from the request body. Expected fields for update are typically `displayName` and `bio`.
        *   The `firebase_uid` and `email` are taken from the verified `FirebaseToken` to ensure users can only modify their own profiles.
        *   Uses `UserProfileDao.saveOrUpdateProfile()` which handles both creating a new profile if one doesn't exist for the UID, or updating an existing one.
        *   Returns the created/updated user profile as JSON.

This setup allows the backend to manage user-specific data in its own database, linked to Firebase authenticated users, while leveraging Firebase for the core authentication process.
The `README_BACKEND.md` file was created in the previous turn.

All steps for the "Backend Adaptation for Firebase Auth & Basic User Profile" subtask have been completed.

1.  **Added Firebase Admin SDK Dependency**: `com.google.firebase:firebase-admin:9.2.0` was added to `pom.xml`.
2.  **Initialized Firebase Admin SDK**: `AppContextListener.java` was updated to initialize `FirebaseApp` using `firebase-service-account-key.json` from `src/main/resources`, with error handling and instructions.
3.  **Created `FirebaseTokenFilter.java`**: This filter verifies Firebase ID tokens from `Authorization: Bearer <token>` header for `/api/*` paths and sets a `firebaseUser` request attribute.
4.  **User Profile Table & DAO**:
    *   `DatabaseManager.java` updated with `initUserProfilesTable()` (columns: `firebase_uid`, `display_name`, `email`, `bio`, `created_at`, `updated_at`). This is called in `AppContextListener`.
    *   `com.example.model.UserProfile.java` model created.
    *   `com.example.dao.UserProfileDao.java` created with `saveOrUpdateProfile()` and `getProfileByFirebaseUid()` methods.
5.  **Created `UserProfileServlet.java`**:
    *   Mapped to `/api/user/profile` and protected by `FirebaseTokenFilter`.
    *   `doGet`: Fetches profile based on UID from `firebaseUser` attribute. If no profile, creates a basic one using token info (UID, email, name) and returns it (201 status). Otherwise, returns existing profile.
    *   `doPost`: Updates `displayName` and `bio` from JSON request body for the authenticated user. Uses `saveOrUpdateProfile()`.
    *   Jackson library was added to `pom.xml` for JSON processing.
6.  **Updated `web.xml`**: Registered `FirebaseTokenFilter` for `/api/*` and `UserProfileServlet` for `/api/user/profile`.
7.  **Documentation Update**: `README_BACKEND.md` was created, explaining the service account key setup, API authentication flow with Bearer tokens, and the `/api/user/profile` endpoint.

The backend is now equipped to initialize the Firebase Admin SDK, verify Firebase ID tokens for API requests, and manage basic user profiles in the H2 database, linked to Firebase UIDs.

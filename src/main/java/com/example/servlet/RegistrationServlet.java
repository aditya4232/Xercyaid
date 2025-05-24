package com.example.servlet;

import com.example.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Using @WebServlet annotation for servlet mapping,
// web.xml update will also be done for completeness and older containers.

// --- CLOUD INTEGRATION NOTES ---
// To support a REST API (e.g., POST /api/users/register) alongside web forms:
// 1.  This servlet could be augmented to check `Content-Type` and `Accept` headers.
//     - If `application/json`, it would parse the request body as JSON.
//     - It would then return a JSON response (e.g., user details or error object)
//       with appropriate HTTP status codes (201 Created, 400 Bad Request, 409 Conflict).
// 2.  Alternatively, a separate controller/servlet (e.g., UserApiServlet or using a JAX-RS/Spring MVC controller)
//     could handle the `/api/users/register` endpoint. This is often a cleaner approach for separating
//     web UI flow from API logic.
// 3.  The `userStore` would be replaced by a service that interacts with a cloud database.
// 4.  Password hashing would use a robust library and the hash stored securely.
// --- END CLOUD INTEGRATION NOTES ---

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // In-memory user storage (placeholder - replace with a database in production)
    private static final Map<String, User> userStore = new ConcurrentHashMap<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Basic input validation
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.getRequestDispatcher("/register.html").forward(request, response);
            return;
        }

        // Check if username already exists
        if (userStore.containsKey(username)) {
            request.setAttribute("errorMessage", "Username already exists. Please choose another one.");
            // Forward back to registration page with an error message
            request.getRequestDispatcher("/register.html").forward(request, response);
        } else {
            // Password hashing (placeholder - use bcrypt or Argon2 in production)
            String passwordHash = "hashed_" + password + "_simple"; // DO NOT use this in production

            User newUser = new User(username, passwordHash, email);
            userStore.put(username, newUser);

            System.out.println("User registered: " + newUser); // Log for debugging

            // Redirect to login page upon successful registration
            response.sendRedirect("login.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward GET requests to the registration page
        // This allows users to navigate to /register directly
        request.getRequestDispatcher("/register.html").forward(request, response);
    }

    // Method to get a user, potentially for login servlet (not part of this subtask)
    public static User getUserByUsername(String username) {
        return userStore.get(username);
    }
}

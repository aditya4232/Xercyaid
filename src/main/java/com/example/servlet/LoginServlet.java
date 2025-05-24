package com.example.servlet;

import com.example.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// --- CLOUD INTEGRATION NOTES ---
// To support a REST API (e.g., POST /api/users/login) alongside web forms:
// 1.  This servlet could be augmented to check `Content-Type` and `Accept` headers.
//     - If `application/json`, it would parse the request body (username, password) as JSON.
//     - On successful authentication, it would return a JSON response containing a
//       session token (e.g., JWT - JSON Web Token) instead of creating an HTTP session
//       and redirecting. The client would then store this token and send it in the
//       `Authorization` header for subsequent API requests.
//     - Error responses (e.g., invalid credentials) would also be JSON objects with
//       appropriate HTTP status codes (400 Bad Request, 401 Unauthorized).
// 2.  Alternatively, a separate controller/servlet (e.g., UserApiServlet or using a JAX-RS/Spring MVC controller)
//     could handle the `/api/users/login` endpoint. This often provides a cleaner separation.
// 3.  The `RegistrationServlet.getUserByUsername()` and password check would be replaced by
//     a service call that interacts with a secure user store (cloud database) and performs
//     robust password verification (e.g., using bcrypt or Argon2).
// --- END CLOUD INTEGRATION NOTES ---

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Basic input validation
        if (username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required.");
            request.getRequestDispatcher("/login.html").forward(request, response);
            return;
        }

        User user = RegistrationServlet.getUserByUsername(username);

        if (user != null) {
            // Password verification (placeholder - must align with registration hashing)
            // In a real application, use a secure password hashing comparison here.
            // For example, if bcrypt was used for registration: BCrypt.checkpw(password, user.getPasswordHash())
            String expectedPasswordHash = "hashed_" + password + "_simple"; // Placeholder
            if (expectedPasswordHash.equals(user.getPasswordHash())) {
                // Valid credentials, create session
                HttpSession session = request.getSession(true);
                session.setAttribute("loggedInUser", username); // Store username in session
                session.setAttribute("user", user); // Optionally store the whole user object

                System.out.println("User logged in: " + username); // Log for debugging

                // Redirect to dashboard page
                response.sendRedirect("dashboard.html"); // Assuming dashboard.html will be created
            } else {
                // Invalid password
                request.setAttribute("errorMessage", "Invalid username or password.");
                request.getRequestDispatcher("/login.html").forward(request, response);
            }
        } else {
            // User not found
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("/login.html").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward GET requests to the login page
        // This allows users to navigate to /login directly
        request.getRequestDispatcher("/login.html").forward(request, response);
    }
}

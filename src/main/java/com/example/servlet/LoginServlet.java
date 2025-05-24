package com.example.servlet;

import com.example.model.User;
import com.example.dao.UserDao; // Import UserDao

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException; // Import SQLException
import java.util.Optional; // Import Optional

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
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        userDao = new UserDao(); // Initialize UserDao
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession httpSession = request.getSession(); // Get session to store messages

        // Basic input validation
        if (username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            // Using session for error message as login.html is static
            httpSession.setAttribute("errorMessage", "Username and password are required.");
            response.sendRedirect("login.html?error=validation"); // Redirect with an error indicator
            return;
        }

        try {
            Optional<User> userOpt = userDao.getUserByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Password verification (placeholder - must align with registration hashing)
                // In a real application, use a secure password hashing comparison here.
                // For example, if bcrypt was used for registration: BCrypt.checkpw(password, user.getPasswordHash())
                String expectedPasswordHash = "hashed_" + password + "_simple"; // Placeholder
                if (expectedPasswordHash.equals(user.getPasswordHash())) {
                    // Valid credentials, create session
                    httpSession.setAttribute("loggedInUser", username); // Store username in session
                    httpSession.setAttribute("user", user); // Optionally store the whole user object
                    
                    // Clear any previous error/success messages from registration/login attempts
                    httpSession.removeAttribute("errorMessage");
                    httpSession.removeAttribute("successMessage");

                    System.out.println("User logged in via DB: " + username); // Log for debugging
                    response.sendRedirect("dashboard.jsp");
                } else {
                    // Invalid password
                    httpSession.setAttribute("errorMessage", "Invalid username or password.");
                    response.sendRedirect("login.html?error=credentials");
                }
            } else {
                // User not found
                httpSession.setAttribute("errorMessage", "Invalid username or password.");
                response.sendRedirect("login.html?error=credentials");
            }
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            e.printStackTrace();
            httpSession.setAttribute("errorMessage", "An error occurred during login. Please try again later.");
            response.sendRedirect("login.html?error=db");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward GET requests to the login page
        // Check for success/error messages from registration to display on login page
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("successMessage") != null) {
                request.setAttribute("successMessage", session.getAttribute("successMessage"));
                session.removeAttribute("successMessage");
            }
            if (session.getAttribute("errorMessage") != null) {
                request.setAttribute("errorMessage", session.getAttribute("errorMessage"));
                session.removeAttribute("errorMessage");
            }
        }
        request.getRequestDispatcher("/login.html").forward(request, response);
    }
}

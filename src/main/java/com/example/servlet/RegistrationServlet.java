package com.example.servlet;

import com.example.model.User;
import com.example.dao.UserDao; // Import UserDao

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException; // Import SQLException
import java.util.Optional; // Import Optional for better handling of user presence

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

        try {
            // Check if username or email already exists
            if (userDao.isUsernameTaken(username)) {
                request.setAttribute("errorMessage", "Username already exists. Please choose another one.");
                request.getRequestDispatcher("/register.html").forward(request, response);
                return;
            }
            if (userDao.isEmailTaken(email)) {
                request.setAttribute("errorMessage", "Email already registered. Please use another email or login.");
                request.getRequestDispatcher("/register.html").forward(request, response);
                return;
            }

            // Password hashing (placeholder - use bcrypt or Argon2 in production)
            String passwordHash = "hashed_" + password + "_simple"; // DO NOT use this in production

            User newUser = new User(username, passwordHash, email); // ID is generated in User constructor
            userDao.addUser(newUser);

            System.out.println("User registered via DB: " + newUser); // Log for debugging

            // Redirect to login page upon successful registration
            // Optionally, you could set a success message for the login page
            request.getSession().setAttribute("successMessage", "Registration successful! Please login.");
            response.sendRedirect("login.html");

        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred during registration. Please try again later.");
            request.getRequestDispatcher("/register.html").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward GET requests to the registration page
        request.getRequestDispatcher("/register.html").forward(request, response);
    }

    // This static method is no longer suitable as it relied on the in-memory store.
    // The LoginServlet will need to use UserDao directly.
    // public static User getUserByUsername(String username) {
    //     // return userStore.get(username); // Old implementation
    //     // This should be replaced by a call to UserDao in the LoginServlet
    //     return null;
    // }
}

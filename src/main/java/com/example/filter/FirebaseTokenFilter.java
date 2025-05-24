package com.example.filter;

import com.example.dao.UserProfileDao; // Import UserProfileDao
import com.example.model.UserProfile;   // Import UserProfile
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException; // Import SQLException
import java.util.Optional;   // Import Optional

// This filter will be mapped in web.xml to /api/*
public class FirebaseTokenFilter implements Filter {
    private UserProfileDao userProfileDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
        System.out.println("FirebaseTokenFilter initialized");
        userProfileDao = new UserProfileDao(); // Initialize UserProfileDao
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7); // Remove "Bearer " prefix
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                httpRequest.setAttribute("firebaseUser", decodedToken);
                System.out.println("Firebase ID Token verified for UID: " + decodedToken.getUid());

                // Fetch and set UserProfile
                try {
                    Optional<UserProfile> userProfileOpt = userProfileDao.getProfileByFirebaseUid(decodedToken.getUid());
                    if (userProfileOpt.isPresent()) {
                        httpRequest.setAttribute("userProfile", userProfileOpt.get());
                        System.out.println("UserProfile loaded for UID: " + decodedToken.getUid());
                    } else {
                        // Profile doesn't exist yet. Servlets like UserProfileServlet might create it.
                        // For PostServlet, it might use a default display name or Firebase name.
                        System.out.println("No UserProfile found for UID: " + decodedToken.getUid() + ". Servlets should handle this.");
                        // You could create a default UserProfile object here if needed,
                        // but typically this is handled by the specific servlet or a user's first profile GET/POST.
                        // UserProfile tempProfile = new UserProfile(decodedToken.getUid(), decodedToken.getEmail(), decodedToken.getName());
                        // httpRequest.setAttribute("userProfile", tempProfile); // This would be a non-persistent, temporary profile
                    }
                } catch (SQLException e) {
                    System.err.println("Database error fetching UserProfile in filter: " + e.getMessage());
                    // Log error but proceed with FirebaseToken, servlets can handle missing profile
                }

                chain.doFilter(request, response);
            } catch (FirebaseAuthException e) {
                System.err.println("Firebase ID token verification failed: " + e.getMessage());
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\": \"Invalid or expired Firebase ID token.\"}");
            }
        } else {
            System.out.println("Missing or malformed Authorization header for: " + httpRequest.getRequestURI());
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Authorization header with Bearer token is required.\"}");
        }
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
        System.out.println("FirebaseTokenFilter destroyed");
    }
}

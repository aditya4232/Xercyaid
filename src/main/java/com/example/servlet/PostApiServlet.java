// Renamed to PostApiServlet to avoid conflict with the existing PostServlet for JSPs
package com.example.servlet;

import com.example.dao.PostDao;
import com.example.dao.UserProfileDao;
import com.example.model.Post;
import com.example.model.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map; // For parsing simple JSON
import java.util.Optional;

// This servlet will be mapped via web.xml to /api/posts
// and protected by FirebaseTokenFilter for POST requests
public class PostApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PostDao postDao;
    private UserProfileDao userProfileDao; // Added for fetching displayName
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        postDao = new PostDao();
        userProfileDao = new UserProfileDao(); // Initialize UserProfileDao
        objectMapper = new ObjectMapper();
    }

    // GET /api/posts - Publicly accessible to fetch posts
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Basic pagination (can be enhanced with request parameters for limit/offset)
            int limit = 20;
            int offset = 0;
            String offsetParam = request.getParameter("offset");
            if (offsetParam != null) {
                try {
                    offset = Integer.parseInt(offsetParam);
                } catch (NumberFormatException e) { /* ignore, use default */ }
            }
            String limitParam = request.getParameter("limit");
             if (limitParam != null) {
                try {
                    limit = Integer.parseInt(limitParam);
                } catch (NumberFormatException e) { /* ignore, use default */ }
            }


            List<Post> posts = postDao.getAllPosts(limit, offset);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(posts));
        } catch (SQLException e) {
            System.err.println("Database error fetching posts: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Database error retrieving posts.\"}");
        }
    }

    // POST /api/posts - Protected by FirebaseTokenFilter
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FirebaseToken firebaseUser = (FirebaseToken) request.getAttribute("firebaseUser");
        UserProfile userProfile = (UserProfile) request.getAttribute("userProfile"); // Get UserProfile

        if (firebaseUser == null) {
            // This should be caught by FirebaseTokenFilter
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"User not authenticated for posting.\"}");
            return;
        }

        String displayName;
        if (userProfile != null && userProfile.getDisplayName() != null && !userProfile.getDisplayName().isEmpty()) {
            displayName = userProfile.getDisplayName();
        } else if (firebaseUser.getName() != null && !firebaseUser.getName().isEmpty()){
            displayName = firebaseUser.getName(); // Fallback to Firebase token display name
        } else {
            displayName = "Anonymous"; // Default if no name is found
        }


        try {
            // Parse simple JSON like {"textContent": "..."}
            @SuppressWarnings("unchecked")
            Map<String, String> reqBody = objectMapper.readValue(request.getReader(), Map.class);
            String textContent = reqBody.get("textContent");

            if (textContent == null || textContent.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"textContent is required.\"}");
                return;
            }

            Post newPost = new Post(firebaseUser.getUid(), textContent.trim(), displayName);
            // The DAO now sets the timestamp if not already set, and ID.
            
            Post createdPost = postDao.createPost(newPost);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(createdPost));

        } catch (SQLException e) {
            System.err.println("Database error creating post for UID " + firebaseUser.getUid() + ": " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Database error creating post.\"}");
        } catch (IOException e) { // Catches Jackson parsing errors too
            System.err.println("Error processing request to create post for UID " + firebaseUser.getUid() + ": " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid request data for post.\"}");
        }
    }
}

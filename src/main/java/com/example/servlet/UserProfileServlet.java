package com.example.servlet;

import com.example.dao.UserProfileDao;
import com.example.model.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

// This servlet will be mapped via web.xml to /api/user/profile
// and protected by FirebaseTokenFilter
public class UserProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserProfileDao userProfileDao;
    private ObjectMapper objectMapper; // For JSON conversion

    @Override
    public void init() throws ServletException {
        super.init();
        userProfileDao = new UserProfileDao();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FirebaseToken firebaseUser = (FirebaseToken) request.getAttribute("firebaseUser");

        if (firebaseUser == null) {
            // This should ideally be caught by the FirebaseTokenFilter earlier
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"User not authenticated.\"}");
            return;
        }

        String uid = firebaseUser.getUid();
        try {
            Optional<UserProfile> profileOpt = userProfileDao.getProfileByFirebaseUid(uid);
            if (profileOpt.isPresent()) {
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(profileOpt.get()));
            } else {
                // No profile found, could create a basic one or return 404
                // For now, let's create a default one and return it
                UserProfile newProfile = new UserProfile(uid, firebaseUser.getEmail(), firebaseUser.getName());
                userProfileDao.saveOrUpdateProfile(newProfile); // Save it
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created (as we just made it)
                response.getWriter().write(objectMapper.writeValueAsString(newProfile));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching user profile for UID " + uid + ": " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Database error retrieving profile.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FirebaseToken firebaseUser = (FirebaseToken) request.getAttribute("firebaseUser");

        if (firebaseUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"User not authenticated.\"}");
            return;
        }

        String uid = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String currentName = firebaseUser.getName(); // Default display name from Firebase token

        try {
            // Parse JSON from request body
            UserProfile requestProfile = objectMapper.readValue(request.getReader(), UserProfile.class);

            Optional<UserProfile> existingProfileOpt = userProfileDao.getProfileByFirebaseUid(uid);
            UserProfile profileToSave;

            if (existingProfileOpt.isPresent()) {
                profileToSave = existingProfileOpt.get();
                // Update only allowed fields from request
                if (requestProfile.getDisplayName() != null) {
                    profileToSave.setDisplayName(requestProfile.getDisplayName());
                }
                if (requestProfile.getBio() != null) {
                    profileToSave.setBio(requestProfile.getBio());
                }
                profileToSave.setEmail(email); // Ensure email is up-to-date from token
            } else {
                // Create new profile
                profileToSave = new UserProfile(uid, email, 
                    requestProfile.getDisplayName() != null ? requestProfile.getDisplayName() : currentName);
                profileToSave.setBio(requestProfile.getBio());
            }
            
            userProfileDao.saveOrUpdateProfile(profileToSave);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(profileToSave));

        } catch (SQLException e) {
            System.err.println("Database error saving user profile for UID " + uid + ": " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Database error saving profile.\"}");
        } catch (IOException e) { // Catches Jackson parsing errors too
            System.err.println("Error processing request to save profile for UID " + uid + ": " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid request data.\"}");
        }
    }
}

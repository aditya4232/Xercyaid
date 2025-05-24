package com.example.servlet;

import com.example.dao.StoryDao;
import com.example.model.Story;
import com.example.model.UserProfile; // Assuming UserProfile is available via request attribute
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
import java.util.concurrent.TimeUnit;

// This servlet will be mapped via web.xml to /api/stories
public class StoryApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StoryDao storyDao;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        storyDao = new StoryDao();
        objectMapper = new ObjectMapper();
    }

    // GET /api/stories - Publicly accessible to fetch active stories
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Fetch active stories, e.g., limit 15
            List<Story> stories = storyDao.getActiveStories(15);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(stories));
        } catch (SQLException e) {
            System.err.println("Database error fetching active stories: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Database error retrieving stories.\"}");
        }
    }

    // POST /api/stories - Protected by FirebaseTokenFilter
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FirebaseToken firebaseUser = (FirebaseToken) request.getAttribute("firebaseUser");
        UserProfile userProfile = (UserProfile) request.getAttribute("userProfile"); // From FirebaseTokenFilter

        if (firebaseUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"User not authenticated for creating a story.\"}");
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
            @SuppressWarnings("unchecked")
            Map<String, String> reqBody = objectMapper.readValue(request.getReader(), Map.class);
            String textContent = reqBody.get("textContent");

            if (textContent == null || textContent.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"textContent is required for a story.\"}");
                return;
            }

            Story newStory = new Story(firebaseUser.getUid(), textContent.trim(), displayName);
            // Timestamp and expiresAt are set in the Story constructor by default
            // Or can be explicitly set here if needed:
            // newStory.setTimestamp(new Timestamp(System.currentTimeMillis()));
            // newStory.setExpiresAt(new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)));
            
            Story createdStory = storyDao.createStory(newStory);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(createdStory));

        } catch (SQLException e) {
            System.err.println("Database error creating story for UID " + firebaseUser.getUid() + ": " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Database error creating story.\"}");
        } catch (IOException e) { // Catches Jackson parsing errors too
            System.err.println("Error processing request to create story for UID " + firebaseUser.getUid() + ": " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid request data for story.\"}");
        }
    }
}

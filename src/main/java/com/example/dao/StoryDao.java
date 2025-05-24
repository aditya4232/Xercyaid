package com.example.dao;

import com.example.model.Story;
import com.example.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StoryDao {

    public Story createStory(Story story) throws SQLException {
        String sql = "INSERT INTO stories (id, firebase_uid, text_content, display_name, created_at, expires_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Ensure timestamps are set if not already
            if (story.getTimestamp() == null) {
                story.setTimestamp(new Timestamp(System.currentTimeMillis()));
            }
            if (story.getExpiresAt() == null) {
                story.setExpiresAt(new Timestamp(story.getTimestamp().getTime() + TimeUnit.HOURS.toMillis(24)));
            }

            pstmt.setString(1, story.getId());
            pstmt.setString(2, story.getFirebaseUid());
            pstmt.setString(3, story.getTextContent());
            pstmt.setString(4, story.getDisplayName());
            pstmt.setTimestamp(5, story.getTimestamp());
            pstmt.setTimestamp(6, story.getExpiresAt());

            pstmt.executeUpdate();
            return story;
        }
    }

    public List<Story> getActiveStories(int limit) throws SQLException {
        List<Story> stories = new ArrayList<>();
        // Fetch stories that have not expired yet, ordered by creation time (newest first)
        String sql = "SELECT id, firebase_uid, text_content, display_name, created_at, expires_at " +
                     "FROM stories " +
                     "WHERE expires_at > CURRENT_TIMESTAMP " +
                     "ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Story story = new Story(
                            rs.getString("id"),
                            rs.getString("firebase_uid"),
                            rs.getString("text_content"),
                            rs.getString("display_name"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("expires_at")
                    );
                    stories.add(story);
                }
            }
        }
        return stories;
    }
}

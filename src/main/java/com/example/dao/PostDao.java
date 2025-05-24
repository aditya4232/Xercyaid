package com.example.dao;

import com.example.model.Post;
import com.example.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostDao {

    public Post createPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (id, firebase_uid, text_content, display_name, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            if (post.getTimestamp() != null) { // Allow pre-set timestamp if available
                createdAt = post.getTimestamp();
            } else {
                post.setTimestamp(createdAt); // Set it in the object for return
            }

            pstmt.setString(1, post.getId());
            pstmt.setString(2, post.getFirebaseUid());
            pstmt.setString(3, post.getTextContent());
            pstmt.setString(4, post.getDisplayName());
            pstmt.setTimestamp(5, createdAt);
            
            pstmt.executeUpdate();
            return post; // Return the post with ID and timestamp potentially set
        }
    }

    public List<Post> getAllPosts(int limit, int offset) throws SQLException {
        List<Post> posts = new ArrayList<>();
        // ORDER BY created_at DESC for newest first
        String sql = "SELECT id, firebase_uid, text_content, display_name, created_at FROM posts ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getString("id"),
                            rs.getString("firebase_uid"),
                            rs.getString("text_content"),
                            rs.getString("display_name"),
                            rs.getTimestamp("created_at")
                    );
                    posts.add(post);
                }
            }
        }
        return posts;
    }

    public Optional<Post> getPostById(String id) throws SQLException {
        String sql = "SELECT id, firebase_uid, text_content, display_name, created_at FROM posts WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Post post = new Post(
                            rs.getString("id"),
                            rs.getString("firebase_uid"),
                            rs.getString("text_content"),
                            rs.getString("display_name"),
                            rs.getTimestamp("created_at")
                    );
                    return Optional.of(post);
                }
            }
        }
        return Optional.empty();
    }
}

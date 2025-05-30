package com.example.dao;

import com.example.model.UserProfile;
import com.example.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

public class UserProfileDao {

    public void saveOrUpdateProfile(UserProfile profile) throws SQLException {
        // Check if profile exists
        Optional<UserProfile> existingProfile = getProfileByFirebaseUid(profile.getFirebaseUid());

        String sql;
        if (existingProfile.isPresent()) {
            // Update existing profile
            sql = "UPDATE user_profiles SET display_name = ?, email = ?, bio = ?, updated_at = CURRENT_TIMESTAMP WHERE firebase_uid = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, profile.getDisplayName());
                pstmt.setString(2, profile.getEmail());
                pstmt.setString(3, profile.getBio());
                pstmt.setString(4, profile.getFirebaseUid());
                pstmt.executeUpdate();
            }
        } else {
            // Insert new profile
            sql = "INSERT INTO user_profiles (firebase_uid, display_name, email, bio, created_at, updated_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, profile.getFirebaseUid());
                pstmt.setString(2, profile.getDisplayName());
                pstmt.setString(3, profile.getEmail());
                pstmt.setString(4, profile.getBio());
                pstmt.executeUpdate();
            }
        }
    }

    public Optional<UserProfile> getProfileByFirebaseUid(String firebaseUid) throws SQLException {
        String sql = "SELECT firebase_uid, display_name, email, bio, created_at, updated_at FROM user_profiles WHERE firebase_uid = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firebaseUid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = new UserProfile();
                    profile.setFirebaseUid(rs.getString("firebase_uid"));
                    profile.setDisplayName(rs.getString("display_name"));
                    profile.setEmail(rs.getString("email"));
                    profile.setBio(rs.getString("bio"));
                    profile.setCreatedAt(rs.getTimestamp("created_at"));
                    profile.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return Optional.of(profile);
                }
            }
        }
        return Optional.empty();
    }
}

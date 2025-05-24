package com.example.model;

import java.util.Date;

public class UserProfile {
    private String firebaseUid; // Primary Key, from FirebaseToken.getUid()
    private String displayName;
    private String email;       // From FirebaseToken.getEmail()
    private String bio;
    private Date createdAt;
    private Date updatedAt;

    public UserProfile() {
    }

    public UserProfile(String firebaseUid, String email, String displayName) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = new Date(); // Set on initial creation via this constructor
        this.updatedAt = new Date(); // Set on initial creation
    }

    // Getters
    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
               "firebaseUid='" + firebaseUid + '\'' +
               ", displayName='" + displayName + '\'' +
               ", email='" + email + '\'' +
               ", bio='" + (bio != null ? bio.substring(0, Math.min(bio.length(), 20)) + "..." : "null") + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}

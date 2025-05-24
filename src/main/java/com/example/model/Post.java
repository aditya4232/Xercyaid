package com.example.model;

import java.sql.Timestamp;
import java.util.UUID;

public class Post {
    private String id;
    private String firebaseUid;
    private String textContent;
    private String displayName; // Denormalized from UserProfile for easier post display
    private Timestamp timestamp; // Creation timestamp

    // Default constructor for Jackson or other frameworks
    public Post() {
    }

    // Constructor for creating a new post
    public Post(String firebaseUid, String textContent, String displayName) {
        this.id = UUID.randomUUID().toString();
        this.firebaseUid = firebaseUid;
        this.textContent = textContent;
        this.displayName = displayName;
        // Timestamp will be set by the database (DEFAULT CURRENT_TIMESTAMP)
        // or can be set here if preferred: this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    // Constructor for retrieving a post from the database
    public Post(String id, String firebaseUid, String textContent, String displayName, Timestamp timestamp) {
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.textContent = textContent;
        this.displayName = displayName;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public String getTextContent() {
        return textContent;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Post{" +
               "id='" + id + '\'' +
               ", firebaseUid='" + firebaseUid + '\'' +
               ", displayName='" + displayName + '\'' +
               ", textContent='" + (textContent != null ? textContent.substring(0, Math.min(textContent.length(), 30)) + "..." : "null") + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}

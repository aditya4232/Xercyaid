package com.example.model;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Story {
    private String id;
    private String firebaseUid;
    private String textContent;
    private String displayName; // Denormalized from UserProfile
    private Timestamp timestamp;   // Creation timestamp
    private Timestamp expiresAt;   // Expiration timestamp (e.g., 24 hours after creation)

    // Default constructor
    public Story() {
    }

    // Constructor for creating a new story
    public Story(String firebaseUid, String textContent, String displayName) {
        this.id = UUID.randomUUID().toString();
        this.firebaseUid = firebaseUid;
        this.textContent = textContent;
        this.displayName = displayName;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        // Default expiration: 24 hours from now
        this.expiresAt = new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24));
    }

    // Constructor for retrieving from database
    public Story(String id, String firebaseUid, String textContent, String displayName, Timestamp timestamp, Timestamp expiresAt) {
        this.id = id;
        this.firebaseUid = firebaseUid;
        this.textContent = textContent;
        this.displayName = displayName;
        this.timestamp = timestamp;
        this.expiresAt = expiresAt;
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

    public Timestamp getExpiresAt() {
        return expiresAt;
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

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "Story{" +
               "id='" + id + '\'' +
               ", firebaseUid='" + firebaseUid + '\'' +
               ", displayName='" + displayName + '\'' +
               ", textContent='" + (textContent != null ? textContent.substring(0, Math.min(textContent.length(), 20)) + "..." : "null") + '\'' +
               ", timestamp=" + timestamp +
               ", expiresAt=" + expiresAt +
               '}';
    }
}

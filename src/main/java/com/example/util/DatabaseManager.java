package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // Using a file-based H2 database; data persists across restarts until `target` dir is cleaned.
    // AUTO_SERVER=TRUE allows multiple connections (e.g., app + H2 console)
    // DB_CLOSE_DELAY=-1 keeps the DB open until the VM exits, useful for web apps
    private static final String DB_URL = "jdbc:h2:./target/webappdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa"; // Default H2 user
    private static final String DB_PASSWORD = "";   // Default H2 password

    public static Connection getConnection() throws SQLException {
        try {
            // Ensure H2 driver is loaded. This is often not needed with modern JDBC drivers (JDBC 4.0+)
            // but can be good practice for older environments or specific configurations.
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initUserTable() {
        // SQL to create the users table if it doesn't exist
        // Using VARCHAR(36) for ID to store UUIDs as strings.
        // Username and Email are unique.
        // Password_hash stores the (currently placeholder) hashed password.
        // Registration_date defaults to the current timestamp on insert.
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                                    "id VARCHAR(36) PRIMARY KEY, " +
                                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                                    "email VARCHAR(255) UNIQUE NOT NULL, " +
                                    "password_hash VARCHAR(255) NOT NULL, " +
                                    "registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                    ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUserTableSQL);
            System.out.println("User table initialized successfully (or already exists).");
        } catch (SQLException e) {
            System.err.println("Error initializing user table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initUserProfilesTable() {
        String createUserProfilesTableSQL = "CREATE TABLE IF NOT EXISTS user_profiles (" +
                                            "firebase_uid VARCHAR(128) PRIMARY KEY, " + // Firebase UIDs can be up to 128 chars
                                            "display_name VARCHAR(255), " +
                                            "email VARCHAR(255) UNIQUE, " + // Should match Firebase user email
                                            "bio TEXT, " +
                                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                                            ");";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUserProfilesTableSQL);
            System.out.println("User profiles table initialized successfully (or already exists).");
        } catch (SQLException e) {
            System.err.println("Error initializing user profiles table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initPostsTable() {
        String createPostsTableSQL = "CREATE TABLE IF NOT EXISTS posts (" +
                                     "id VARCHAR(36) PRIMARY KEY, " +
                                     "firebase_uid VARCHAR(128) NOT NULL, " + // Foreign key to user_profiles if desired
                                     "text_content TEXT NOT NULL, " +
                                     "display_name VARCHAR(255), " + // Denormalized for easier display
                                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                     ");";
        // Optional: Add index on firebase_uid and created_at for faster querying
        // String createIndexPostsFirebaseUidSQL = "CREATE INDEX IF NOT EXISTS idx_posts_firebase_uid ON posts(firebase_uid);";
        // String createIndexPostsCreatedAtSQL = "CREATE INDEX IF NOT EXISTS idx_posts_created_at ON posts(created_at DESC);";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createPostsTableSQL);
            // stmt.execute(createIndexPostsFirebaseUidSQL);
            // stmt.execute(createIndexPostsCreatedAtSQL);
            System.out.println("Posts table initialized successfully (or already exists).");
        } catch (SQLException e) {
            System.err.println("Error initializing posts table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initStoriesTable() {
        String createStoriesTableSQL = "CREATE TABLE IF NOT EXISTS stories (" +
                                       "id VARCHAR(36) PRIMARY KEY, " +
                                       "firebase_uid VARCHAR(128) NOT NULL, " +
                                       "text_content TEXT, " + // Text content can be optional for some stories
                                       "display_name VARCHAR(255), " +
                                       "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                       "expires_at TIMESTAMP NOT NULL" +
                                       ");";
        // Optional: Index on expires_at and created_at for fetching active stories
        // String createIndexStoriesExpiresAtSQL = "CREATE INDEX IF NOT EXISTS idx_stories_expires_at ON stories(expires_at);";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createStoriesTableSQL);
            // stmt.execute(createIndexStoriesExpiresAtSQL);
            System.out.println("Stories table initialized successfully (or already exists).");
        } catch (SQLException e) {
            System.err.println("Error initializing stories table: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Main method for quick testing of connection and table creation (optional)
    public static void main(String[] args) {
        try {
            System.out.println("Attempting to connect to H2 database...");
            Connection conn = getConnection();
            System.out.println("Connection successful: " + conn.getMetaData().getURL());
            initUserTable();
            initUserProfilesTable();
            initPostsTable();
            initStoriesTable(); // Initialize the stories table
            conn.close();
            System.out.println("Connection closed.");
        } catch (SQLException e) {
            System.err.println("Database connection or initialization failed:");
            e.printStackTrace();
        }
    }
}

package com.example.listener;

import com.example.util.DatabaseManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.h2.tools.Server; // Import H2 Server for web console

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

@WebListener
public class AppContextListener implements ServletContextListener {

    private Server h2WebServer;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Application context is initializing...");

        // Initialize the user table
        DatabaseManager.initUserTable();
        DatabaseManager.initUserProfilesTable(); // Initialize UserProfiles table
        DatabaseManager.initPostsTable(); // Initialize Posts table
        DatabaseManager.initStoriesTable(); // Initialize Stories table

        // Initialize Firebase Admin SDK
        // =====================================================================================
        // IMPORTANT: Firebase Admin SDK Initialization
        // -------------------------------------------------------------------------------------
        // To enable Firebase Admin SDK features (like ID token verification on the backend),
        // you need to:
        // 1. Go to your Firebase project console -> Project settings -> Service accounts.
        // 2. Generate a new private key and download the JSON file.
        // 3. RENAME this file to "firebase-service-account-key.json".
        // 4. Place this "firebase-service-account-key.json" file in the
        //    "src/main/resources" directory of this project.
        //
        // The application will attempt to load this file from the classpath.
        // =====================================================================================
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account-key.json");

            if (serviceAccount == null) {
                System.err.println("*********************************************************************************");
                System.err.println("ERROR: firebase-service-account-key.json NOT FOUND in classpath (src/main/resources).");
                System.err.println("Firebase Admin SDK will not be initialized. Backend token verification will fail.");
                System.err.println("Please follow setup instructions in AppContextListener.java or README_BACKEND.md.");
                System.err.println("*********************************************************************************");
            } else {
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    // Optionally, specify your databaseURL if using Firebase Realtime Database
                    // .setDatabaseUrl("https://<YOUR_PROJECT_ID>.firebaseio.com")
                    .build();

                if (FirebaseApp.getApps().isEmpty()) { // Check if already initialized
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase Admin SDK initialized successfully.");
                } else {
                    System.out.println("Firebase Admin SDK already initialized.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing Firebase Admin SDK: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Ensure 'firebase-service-account-key.json' is valid and in 'src/main/resources'.");
        }


        // Start H2 web console
        // This allows accessing the H2 database via a browser at http://localhost:8082
        // Default user: sa, empty password. JDBC URL should match DatabaseManager.
        try {
            System.out.println("Starting H2 web console on port 8082...");
            h2WebServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082");
            h2WebServer.start();
            sce.getServletContext().setAttribute("h2WebServer", h2WebServer);
            System.out.println("H2 web console started successfully. URL: " + h2WebServer.getURL());
        } catch (SQLException e) {
            System.err.println("Failed to start H2 web console: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Application context initialization complete.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application context is being destroyed...");

        // Stop H2 web console
        if (h2WebServer != null) {
            System.out.println("Stopping H2 web console...");
            h2WebServer.stop();
            System.out.println("H2 web console stopped.");
        }

        System.out.println("Application context destruction complete.");
    }
}

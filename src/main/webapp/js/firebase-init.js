// =====================================================================================
// IMPORTANT: Firebase Configuration
// -------------------------------------------------------------------------------------
// This file contains the Firebase project configuration.
//
// TO USE THIS APPLICATION, YOU MUST:
// 1. Create a Firebase project at https://console.firebase.google.com/
// 2. In your Firebase project console, go to Project settings (gear icon).
// 3. Under the "General" tab, find the "Your apps" section.
// 4. Click on the "Web" icon (</>) to add a web app (or use an existing one).
// 5. Firebase will provide you with a `firebaseConfig` object.
// 6. COPY that entire `firebaseConfig` object and PASTE it below, replacing the
//    placeholder `firebaseConfig` object.
//
// Example of what to copy from Firebase:
// const firebaseConfig = {
//   apiKey: "AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXX",
//   authDomain: "your-project-id.firebaseapp.com",
//   projectId: "your-project-id",
//   storageBucket: "your-project-id.appspot.com",
//   messagingSenderId: "123456789012",
//   appId: "1:123456789012:web:abcdef1234567890abcdef"
// };
// =====================================================================================

const firebaseConfig = {
  apiKey: "YOUR_API_KEY_HERE",
  authDomain: "YOUR_PROJECT_ID.firebaseapp.com",
  projectId: "YOUR_PROJECT_ID",
  storageBucket: "YOUR_PROJECT_ID.appspot.com",
  messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
  appId: "YOUR_APP_ID"
};

// Initialize Firebase
let app;
let auth;

// Check if Firebase is already initialized to avoid re-initialization errors
if (!firebase.apps.length) {
  app = firebase.initializeApp(firebaseConfig);
} else {
  app = firebase.app(); // if already initialized, use that one
}
auth = firebase.auth(); // Get the auth instance


// Function to redirect to login if not authenticated
// Adjusted paths to be relative for simpler deployment, assuming all relevant HTML files are in the same directory.
function redirectToLoginIfNotAuth(currentPath) {
  if (!auth.currentUser && !currentPath.endsWith('/login_firebase.html')) {
    console.log('User not authenticated, redirecting to login.');
    window.location.href = 'login_firebase.html';
  }
}

// Function to redirect to app_main if authenticated and on login page
function redirectToAppIfAuth(currentPath) {
  if (auth.currentUser && currentPath.endsWith('/login_firebase.html')) {
    console.log('User authenticated, redirecting to app main.');
    window.location.href = 'app_main.html';
  }
}

// General utility to display messages
function displayMessage(elementId, message, isError = true) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = message;
        element.classList.remove('hidden');
        element.classList.toggle('text-red-500', isError); // Example error styling
        element.classList.toggle('text-green-500', !isError); // Example success styling
    }
}

// General utility to hide messages
function hideMessage(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.classList.add('hidden');
        element.textContent = '';
    }
}

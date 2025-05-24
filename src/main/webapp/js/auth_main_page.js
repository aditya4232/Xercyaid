document.addEventListener('DOMContentLoaded', () => {
    if (!firebase.apps.length) {
        console.error("Firebase not initialized. Ensure firebase-init.js is loaded and configured.");
        // Potentially display a more user-facing error on the page
        return;
    }
    const auth = firebase.auth();

    const currentUserEmailDiv = document.getElementById('currentUserEmail');
    const logoutButton = document.getElementById('logoutButton');

    // Check auth state
    auth.onAuthStateChanged(user => {
        if (user) {
            // User is signed in.
            console.log('User is logged in on app_main:', user);
            if (currentUserEmailDiv) {
                currentUserEmailDiv.textContent = `Welcome, ${user.email}`;
            }
        } else {
            // User is signed out.
            console.log('User is not logged in, redirecting to login_firebase.html from app_main.');
            window.location.href = 'login_firebase.html';
        }
    });

    // Logout button functionality
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            auth.signOut().then(() => {
                // Sign-out successful.
                // The onAuthStateChanged observer will handle the redirect.
                console.log('User signed out successfully.');
            }).catch((error) => {
                // An error happened.
                console.error('Sign out error:', error);
                // Optionally display this error to the user
                alert(`Logout failed: ${error.message}`);
            });
        });
    }
});

document.addEventListener('DOMContentLoaded', () => {
    if (!firebase.apps.length) {
        console.error("Firebase not initialized. Ensure firebase-init.js is loaded and configured.");
        displayMessage('errorMessage', 'Firebase configuration error. Please check console.');
        return;
    }
    const auth = firebase.auth();

    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const showRegisterBtn = document.getElementById('showRegister');
    const showLoginBtn = document.getElementById('showLogin');

    const loginEmailInput = document.getElementById('email');
    const loginPasswordInput = document.getElementById('password');
    const loginErrorMessageDiv = document.getElementById('errorMessage');

    const registerEmailInput = document.getElementById('registerEmail');
    const registerPasswordInput = document.getElementById('registerPassword');
    const registerErrorMessageDiv = document.getElementById('registerErrorMessage');

    // Redirect if user is already logged in
    auth.onAuthStateChanged(user => {
        if (user) {
            console.log('User is logged in, redirecting to app_main.html from login page.');
            window.location.href = 'app_main.html';
        }
    });

    // Switch to registration form
    if (showRegisterBtn) {
        showRegisterBtn.addEventListener('click', (e) => {
            e.preventDefault();
            loginForm.classList.add('hidden');
            registerForm.classList.remove('hidden');
            hideMessage(loginErrorMessageDiv.id);
        });
    }

    // Switch to login form
    if (showLoginBtn) {
        showLoginBtn.addEventListener('click', (e) => {
            e.preventDefault();
            registerForm.classList.add('hidden');
            loginForm.classList.remove('hidden');
            hideMessage(registerErrorMessageDiv.id);
        });
    }

    // Login form submission
    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            hideMessage(loginErrorMessageDiv.id);
            const email = loginEmailInput.value;
            const password = loginPasswordInput.value;

            auth.signInWithEmailAndPassword(email, password)
                .then((userCredential) => {
                    // Signed in 
                    console.log('User logged in:', userCredential.user);
                    window.location.href = 'app_main.html';
                })
                .catch((error) => {
                    console.error('Login error:', error);
                    displayMessage(loginErrorMessageDiv.id, `Error: ${error.message}`);
                });
        });
    }

    // Registration form submission
    if (registerForm) {
        registerForm.addEventListener('submit', (e) => {
            e.preventDefault();
            hideMessage(registerErrorMessageDiv.id);
            const email = registerEmailInput.value;
            const password = registerPasswordInput.value;

            if (password.length < 6) {
                displayMessage(registerErrorMessageDiv.id, 'Password should be at least 6 characters.');
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                .then((userCredential) => {
                    // Signed in 
                    console.log('User registered and logged in:', userCredential.user);
                    window.location.href = 'app_main.html'; 
                })
                .catch((error) => {
                    console.error('Registration error:', error);
                    displayMessage(registerErrorMessageDiv.id, `Error: ${error.message}`);
                });
        });
    }
});

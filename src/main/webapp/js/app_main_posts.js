document.addEventListener('DOMContentLoaded', () => {
    if (!firebase.apps.length) {
        console.error("Firebase not initialized for posts. Ensure firebase-init.js is loaded and configured.");
        return;
    }
    const auth = firebase.auth();

    const postTextContentInput = document.getElementById('postTextContent');
    const submitPostButton = document.getElementById('submitPostButton');
    const postFeedArea = document.getElementById('postFeedArea'); // Ensure this container exists in app_main.html
    const postFormErrorMessageDiv = document.getElementById('postErrorMessage'); // From app_main.html

    // Function to fetch and display posts
    async function fetchAndDisplayPosts() {
        if (!postFeedArea) {
            console.error('Post feed area not found in HTML.');
            return;
        }
        try {
            const response = await fetch('./api/posts?limit=20&offset=0'); // Adjust limit/offset as needed
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const posts = await response.json();

            // Clear existing posts (simple approach)
            // For infinite scroll, you'd append instead of clearing
            const feedContainer = postFeedArea.querySelector('.space-y-6'); // Assuming this is the direct child for posts
            if(feedContainer) {
                feedContainer.innerHTML = ''; // Clear previous posts
            } else { // Fallback if the structure is just postFeedArea
                 postFeedArea.innerHTML = '<h2 class="text-xl font-semibold mb-4 text-blue-700">Feed</h2>'; // Re-add title if needed
            }


            if (posts.length === 0) {
                const noPostsMessage = document.createElement('p');
                noPostsMessage.textContent = 'No posts yet. Be the first to share!';
                noPostsMessage.className = 'text-gray-500 text-center py-4';
                if(feedContainer) feedContainer.appendChild(noPostsMessage); else postFeedArea.appendChild(noPostsMessage);
                return;
            }

            posts.forEach(post => {
                const postElement = document.createElement('article');
                postElement.className = 'bg-white p-6 rounded-lg shadow-md';
                
                // Format timestamp (basic example, consider using a library for more complex needs)
                const postDate = new Date(post.timestamp).toLocaleString();

                postElement.innerHTML = `
                    <div class="flex items-center mb-4">
                        <!-- Placeholder for user avatar - can be added later -->
                        <!-- <img class="h-10 w-10 rounded-full mr-3" src="https://via.placeholder.com/40" alt="User avatar"> -->
                        <div>
                            <p class="font-semibold text-gray-800">${post.displayName || 'Anonymous'}</p>
                            <p class="text-xs text-gray-500">Posted on ${postDate}</p>
                        </div>
                    </div>
                    <p class="text-gray-700 mb-3 whitespace-pre-wrap">${post.textContent}</p>
                    <!-- Placeholder for image if your Post model supports it -->
                    <!-- <img class="rounded-md w-full max-h-96 object-cover" src="https://via.placeholder.com/600x400" alt="Post image"> -->
                    <div class="mt-4 flex justify-between items-center text-sm">
                        <button class="text-blue-500 hover:text-blue-700 font-semibold">Like</button>
                        <button class="text-gray-500 hover:text-gray-700 font-semibold">Comment</button>
                        <button class="text-gray-500 hover:text-gray-700 font-semibold">Share</button>
                    </div>
                `;
                if(feedContainer) feedContainer.appendChild(postElement); else postFeedArea.appendChild(postElement);
            });
        } catch (error) {
            console.error('Error fetching posts:', error);
            if(postFeedArea.querySelector('.space-y-6')) postFeedArea.querySelector('.space-y-6').innerHTML = '<p class="text-red-500 text-center">Could not load posts. Please try again later.</p>';
            else postFeedArea.innerHTML = '<h2 class="text-xl font-semibold mb-4 text-blue-700">Feed</h2> <p class="text-red-500 text-center">Could not load posts. Please try again later.</p>';
        }
    }

    // Create Post form submission
    if (submitPostButton && postTextContentInput) {
        submitPostButton.addEventListener('click', async (e) => {
            e.preventDefault();
            hideMessage(postFormErrorMessageDiv.id); // Hide previous errors

            const textContent = postTextContentInput.value.trim();
            if (!textContent) {
                displayMessage(postFormErrorMessageDiv.id, 'Post content cannot be empty.');
                return;
            }

            const currentUser = auth.currentUser;
            if (!currentUser) {
                displayMessage(postFormErrorMessageDiv.id, 'You must be logged in to post.');
                // Optionally redirect to login: window.location.href = 'login_firebase.html';
                return;
            }

            try {
                const idToken = await currentUser.getIdToken();
                const response = await fetch('./api/posts', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${idToken}`
                    },
                    body: JSON.stringify({ textContent: textContent })
                });

                if (response.status === 201) { // Created
                    postTextContentInput.value = ''; // Clear textarea
                    hideMessage(postFormErrorMessageDiv.id);
                    await fetchAndDisplayPosts(); // Refresh posts
                } else {
                    const errorData = await response.json();
                    console.error('Post creation error:', errorData);
                    displayMessage(postFormErrorMessageDiv.id, `Error: ${errorData.error || 'Could not create post.'}`);
                }
            } catch (error) {
                console.error('Error creating post:', error);
                displayMessage(postFormErrorMessageDiv.id, 'An unexpected error occurred. Please try again.');
            }
        });
    }

    // Initial fetch of posts when the page loads and user is authenticated
    auth.onAuthStateChanged(user => {
        if (user) {
            fetchAndDisplayPosts();
        } else {
            // If user is not logged in, no need to fetch posts for app_main.html
            // auth_main_page.js should handle redirection to login page.
            // Clear the feed area if it might contain stale data from a previous session (though usually not an issue with full page loads)
             if (postFeedArea) {
                const feedContainer = postFeedArea.querySelector('.space-y-6');
                if(feedContainer) feedContainer.innerHTML = '<p class="text-gray-500 text-center py-4">Please log in to see the feed.</p>';
                else postFeedArea.innerHTML = '<h2 class="text-xl font-semibold mb-4 text-blue-700">Feed</h2><p class="text-gray-500 text-center py-4">Please log in to see the feed.</p>';
            }
        }
    });
});

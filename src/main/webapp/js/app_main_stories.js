document.addEventListener('DOMContentLoaded', () => {
    if (!firebase.apps.length) {
        console.error("Firebase not initialized for stories. Ensure firebase-init.js is loaded and configured.");
        return;
    }
    const auth = firebase.auth();

    const storyTextContentInput = document.getElementById('storyTextContent');
    const submitStoryButton = document.getElementById('submitStoryButton');
    const storiesArea = document.getElementById('storiesArea'); // Container for story circles
    const storyFeedContainer = document.getElementById('storyFeedContainer'); // Specific div for story circles
    const storyErrorMessageDiv = document.getElementById('storyErrorMessage');

    // Modal elements
    const storyModal = document.getElementById('storyModal');
    const storyModalContent = document.getElementById('storyModalContent');
    const storyModalDisplayName = document.getElementById('storyModalDisplayName');
    const storyModalCloseButton = document.getElementById('storyModalCloseButton');

    // Function to show the story modal
    function showStoryModal(story) {
        if (storyModal && storyModalContent && storyModalDisplayName) {
            storyModalDisplayName.textContent = story.displayName || 'Anonymous';
            storyModalContent.textContent = story.textContent;
            storyModal.classList.remove('hidden');
        }
    }

    // Function to hide the story modal
    function hideStoryModal() {
        if (storyModal) {
            storyModal.classList.add('hidden');
            storyModalDisplayName.textContent = '';
            storyModalContent.textContent = '';
        }
    }

    if (storyModalCloseButton) {
        storyModalCloseButton.addEventListener('click', hideStoryModal);
    }
    // Optional: Close modal if clicked outside the content (if modal has an overlay)
    if (storyModal) {
        storyModal.addEventListener('click', (event) => {
            if (event.target === storyModal) { // Clicked on the overlay
                hideStoryModal();
            }
        });
    }


    // Function to fetch and display stories
    async function fetchAndDisplayStories() {
        if (!storyFeedContainer) {
            console.error('Story feed container (#storyFeedContainer) not found in HTML.');
            return;
        }
        try {
            const response = await fetch('./api/stories'); // GET request
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const stories = await response.json();

            storyFeedContainer.innerHTML = ''; // Clear existing stories

            if (stories.length === 0) {
                // Example: Add a "No stories" or "Add Story" placeholder if desired
                const addStoryPlaceholder = document.createElement('div');
                addStoryPlaceholder.className = 'flex-shrink-0 w-20 h-20 bg-gray-200 rounded-full flex items-center justify-center text-gray-500 text-sm cursor-pointer hover:bg-gray-300';
                addStoryPlaceholder.textContent = '+ Add Story';
                addStoryPlaceholder.addEventListener('click', () => {
                    // Potentially focus the story input or open a dedicated story creation UI
                    if(storyTextContentInput) storyTextContentInput.focus();
                });
                storyFeedContainer.appendChild(addStoryPlaceholder);
                return;
            }

            stories.forEach(story => {
                const storyCircle = document.createElement('div');
                storyCircle.className = 'flex-shrink-0 w-20 h-20 bg-gradient-to-r from-purple-400 via-pink-500 to-red-500 rounded-full flex items-center justify-center text-white font-bold cursor-pointer hover:opacity-80 transition-opacity story-circle';
                // Display first letter of display name or 'S'
                storyCircle.textContent = story.displayName ? story.displayName.charAt(0).toUpperCase() : 'S';
                storyCircle.title = `View story from ${story.displayName || 'Anonymous'}`;
                
                storyCircle.addEventListener('click', () => {
                    showStoryModal(story);
                });
                storyFeedContainer.appendChild(storyCircle);
            });
        } catch (error) {
            console.error('Error fetching stories:', error);
            storyFeedContainer.innerHTML = '<p class="text-red-500 text-center">Could not load stories.</p>';
        }
    }

    // Create Story button submission
    if (submitStoryButton && storyTextContentInput) {
        submitStoryButton.addEventListener('click', async () => {
            if(storyErrorMessageDiv) hideMessage(storyErrorMessageDiv.id);

            const textContent = storyTextContentInput.value.trim();
            if (!textContent) {
                if(storyErrorMessageDiv) displayMessage(storyErrorMessageDiv.id, 'Story content cannot be empty.');
                return;
            }

            const currentUser = auth.currentUser;
            if (!currentUser) {
                if(storyErrorMessageDiv) displayMessage(storyErrorMessageDiv.id, 'You must be logged in to create a story.');
                return;
            }

            try {
                const idToken = await currentUser.getIdToken();
                const response = await fetch('./api/stories', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${idToken}`
                    },
                    body: JSON.stringify({ textContent: textContent })
                });

                if (response.status === 201) { // Created
                    storyTextContentInput.value = ''; // Clear input
                    if(storyErrorMessageDiv) hideMessage(storyErrorMessageDiv.id);
                    await fetchAndDisplayStories(); // Refresh stories
                } else {
                    const errorData = await response.json();
                    console.error('Story creation error:', errorData);
                    if(storyErrorMessageDiv) displayMessage(storyErrorMessageDiv.id, `Error: ${errorData.error || 'Could not create story.'}`);
                }
            } catch (error) {
                console.error('Error creating story:', error);
                 if(storyErrorMessageDiv) displayMessage(storyErrorMessageDiv.id, 'An unexpected error occurred. Please try again.');
            }
        });
    }

    // Initial fetch of stories when the page loads and user is authenticated
    auth.onAuthStateChanged(user => {
        if (user) {
            fetchAndDisplayStories();
        } else {
            // If user is not logged in, clear stories area or show a message
            if (storyFeedContainer) {
                storyFeedContainer.innerHTML = '<p class="text-gray-500 text-center">Log in to view or create stories.</p>';
            }
        }
    });
});

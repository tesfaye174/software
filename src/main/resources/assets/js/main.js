// Utility functions
function getJSON(url, callback) {
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Request error: ' + response.status);
            }
            return response.json();
        })
        .then(data => callback(null, data))
        .catch(error => callback(error, null));
}

function postJSON(url, data, callback) {
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw err; });
            }
            return response.json();
        })
        .then(data => callback(null, data))
        .catch(error => callback(error, null));
}

// User management
const UserManager = {
    currentUser: null,
    
    init: function() {
        const savedUser = localStorage.getItem('currentUser');
        if (savedUser) {
            this.currentUser = JSON.parse(savedUser);
            this.updateUI();
        }
    },
    
    register: function(username, password, email, callback) {
        const userData = { username, password, email };
        postJSON('/api/users/register', userData, (error, data) => {
            if (error) {
                callback(error, null);
                return;
            }
            callback(null, data);
        });
    },
    
    login: function(username, password, callback) {
        const loginData = { username, password };
        postJSON('/api/users/login', loginData, (error, data) => {
            if (error) {
                callback(error, null);
                return;
            }
            this.currentUser = data;
            localStorage.setItem('currentUser', JSON.stringify(data));
            this.updateUI();
            callback(null, data);
        });
    },
    
    logout: function() {
        this.currentUser = null;
        localStorage.removeItem('currentUser');
        this.updateUI();
        window.location.href = '/';
    },
    
    updateUI: function() {
        const loginBtn = document.getElementById('loginBtn');
        const registerBtn = document.getElementById('registerBtn');
        const logoutBtn = document.getElementById('logoutBtn');
        const userInfo = document.getElementById('userInfo');
        
        if (!loginBtn || !registerBtn || !logoutBtn || !userInfo) {
            return;
        }
        
        if (this.currentUser) {
            loginBtn.style.display = 'none';
            registerBtn.style.display = 'none';
            logoutBtn.style.display = 'block';
            userInfo.style.display = 'block';
            userInfo.textContent = `Welcome, ${this.currentUser.username}!`;
        } else {
            loginBtn.style.display = 'block';
            registerBtn.style.display = 'block';
            logoutBtn.style.display = 'none';
            userInfo.style.display = 'none';
        }
    }
};

// Story management
const StoryManager = {
    currentStory: null,
    storyHistory: [],
    
    loadStory: function(storyId, callback) {
        getJSON(`/api/stories/${storyId}`, (error, story) => {
            if (error) {
                callback(error, null);
                return;
            }
            this.currentStory = story;
            this.updateStoryUI();
            callback(null, story);
        });
    },
    
    makeChoice: function(choiceIndex) {
        if (!this.currentStory || !this.currentStory.nextStoryIds || choiceIndex >= this.currentStory.nextStoryIds.length) {
            console.error('Invalid choice');
            return;
        }
        
        this.storyHistory.push(this.currentStory.id);
        const nextStoryId = this.currentStory.nextStoryIds[choiceIndex];
        this.loadStory(nextStoryId, (error, story) => {
            if (error) {
                console.error('Error loading story:', error);
            }
        });
    },
    
    goBack: function() {
        if (this.storyHistory.length === 0) {
            console.error('No previous story');
            return;
        }
        
        const previousStoryId = this.storyHistory.pop();
        this.loadStory(previousStoryId, (error, story) => {
            if (error) {
                console.error('Error loading previous story:', error);
            }
        });
    },
    
    updateStoryUI: function() {
        const storyContainer = document.getElementById('storyContainer');
        const choicesContainer = document.getElementById('choicesContainer');
        
        if (!storyContainer || !choicesContainer) {
            return;
        }
        
        if (!this.currentStory) {
            storyContainer.innerHTML = '<p>No story loaded</p>';
            choicesContainer.innerHTML = '';
            return;
        }
        
        storyContainer.innerHTML = `
            <h2>${this.currentStory.title}</h2>
            <div class="story-content">${this.currentStory.content}</div>
        `;
        
        choicesContainer.innerHTML = '';
        
        if (this.currentStory.isEnding) {
            const restartButton = document.createElement('button');
            restartButton.className = 'choice-button';
            restartButton.textContent = 'Restart';
            restartButton.addEventListener('click', () => {
                this.storyHistory = [];
                this.loadStory(1, (error, story) => {
                    if (error) {
                        console.error('Error loading initial story:', error);
                    }
                });
            });
            choicesContainer.appendChild(restartButton);
        } else if (this.currentStory.choices && this.currentStory.choices.length > 0) {
            this.currentStory.choices.forEach((choice, index) => {
                const choiceButton = document.createElement('button');
                choiceButton.className = 'choice-button';
                choiceButton.textContent = choice;
                choiceButton.addEventListener('click', () => this.makeChoice(index));
                choicesContainer.appendChild(choiceButton);
            });
        }
        
        if (this.storyHistory.length > 0) {
            const backButton = document.createElement('button');
            backButton.className = 'back-button';
            backButton.textContent = 'Go Back';
            backButton.addEventListener('click', () => this.goBack());
            choicesContainer.appendChild(backButton);
        }
    }
};

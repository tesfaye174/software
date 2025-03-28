package tesfaye.venieri.software;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    // Singleton instance
    private static UserManager instance;

    // Current logged-in user
    private User currentUser;

    // List of stories
    private List<Story> stories;

    // Private constructor for singleton pattern
    private UserManager() {
        this.stories = new ArrayList<>();
    }

    // Singleton getInstance method
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    // User class to represent user details
    public static class User {
        private String email;
        private String username;
        private LocalDate joinedDate;

        public User(String email) {
            this.email = email;
            this.username = email.split("@")[0];
            this.joinedDate = LocalDate.now();
        }

        // Getters
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public LocalDate getJoinedDate() { return joinedDate; }
    }

    // Story class to represent user stories
    public static class Story {
        private long id;
        private String title;
        private String content;
        private String author;

        public Story(String title, String content, String author) {
            this.id = System.currentTimeMillis();
            this.title = title;
            this.content = content;
            this.author = author;
        }

        // Getters
        public long getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public String getAuthor() { return author; }
    }

    // Login method
    public boolean login(String email, String password) {
        // In a real application, you'd validate credentials against a database
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            this.currentUser = new User(email);
            return true;
        }
        return false;
    }

    // Logout method
    public void logout() {
        this.currentUser = null;
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return this.currentUser;
    }

    // Create a new story
    public Story createStory(String title, String content) {
        if (isLoggedIn()) {
            Story newStory = new Story(title, content, currentUser.getUsername());
            stories.add(newStory);
            return newStory;
        }
        return null;
    }

    // Get stories by current user
    public List<Story> getUserStories() {
        if (isLoggedIn()) {
            List<Story> userStories = new ArrayList<>();
            for (Story story : stories) {
                if (story.getAuthor().equals(currentUser.getUsername())) {
                    userStories.add(story);
                }
            }
            return userStories;
        }
        return new ArrayList<>();
    }

    // Example usage method
    public void demonstrateUsage() {
        // Login
        boolean loginSuccess = login("user@example.com", "password");

        if (loginSuccess) {
            // Create a story
            Story story = createStory("My First Adventure", "A long and exciting story...");

            // Get user's stories
            List<Story> myStories = getUserStories();

            // Logout
            logout();
        }
    }
}
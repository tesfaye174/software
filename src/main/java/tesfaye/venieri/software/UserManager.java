package tesfaye.venieri.software;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.User;

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

    // Login method
    public boolean login(String email, String password) {
        // In a real application, you'd validate credentials against a database
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            this.currentUser = new User(email, password, email);
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
            Story newStory = new Story(title, content, false);
            stories.add(newStory);
            return newStory;
        }
        return null;
    }

    // Get stories by current user
    public List<Story> getUserStories() {
        if (isLoggedIn()) {
            // Restituisce tutte le storie poiché la classe Story non ha più il campo author
            return new ArrayList<>(stories);
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
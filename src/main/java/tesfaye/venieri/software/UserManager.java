package tesfaye.venieri.software;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Repository.StoryRepository;
import tesfaye.venieri.software.Repository.UserRepository;

@Component
public class UserManager {
    // Current logged-in user
    private User currentUser;
    
    // Repositories
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    
    @Autowired
    public UserManager(UserRepository userRepository, StoryRepository storyRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.currentUser = null;
    }
    
    // For backward compatibility with Main.java
    private static UserManager instance;
    
    // Singleton getInstance method for backward compatibility
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            // This will only be used when called from Main.java, not from Spring context
            instance = new UserManager(null, null);
        }
        return instance;
    }

    // Login method
    public boolean login(String email, String password) {
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            if (userRepository != null) {
                // Use repository to find user
                User user = userRepository.findByUsername(email).orElse(null);
                if (user != null && user.getPassword().equals(password)) {
                    this.currentUser = user;
                    return true;
                }
                return false;
            } else {
                // Fallback for Main.java when repository is null
                this.currentUser = new User(email, password, email);
                return true;
            }
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
            if (storyRepository != null) {
                // Use repository to save story
                return storyRepository.save(newStory);
            } else {
                // Fallback for Main.java when repository is null
                return newStory;
            }
        }
        return null;
    }

    // Get stories by current user
    public List<Story> getUserStories() {
        if (isLoggedIn()) {
            if (storyRepository != null) {
                // Use repository to get all stories
                return storyRepository.findAll();
            } else {
                // Fallback for Main.java when repository is null
                return new ArrayList<>();
            }
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
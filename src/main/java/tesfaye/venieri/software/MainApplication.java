package tesfaye.venieri.software;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;

@SpringBootApplication
public class MainApplication {
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    @Autowired
    private UserManager userManager;
    
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner runDemo() {
        return args -> {
            logger.info("Starting demo application...");
            
            try {
                // Initialize test user if not exists
                String testEmail = "user@example.com";
                String testPassword = "Test@123";
                String testUsername = "testUser";
                
                try {
                    userManager.registerUser(testUsername, testEmail, testPassword);
                    logger.info("Test user created successfully");
                } catch (IllegalArgumentException e) {
                    logger.warn("Test user already exists: {}", e.getMessage());
                }
                
                // Attempt login
                logger.info("Attempting login with test credentials");
                String sessionId = userManager.login(testEmail, testPassword);
                
                // Get current user
                User currentUser = userManager.getCurrentUser(sessionId);
                logger.info("Logged in as: {}", currentUser.getUsername());
                
                try {
                    // Create a story
                    Story story = new Story();
                    story.setTitle("Adventure");
                    story.setDescription("A thrilling tale of coding and debugging...");
                    story.setAuthor(currentUser);
                    story.setIsPublic(false);
                    
                    Story savedStory = userManager.createStory(story);
                    logger.info("Story created: {}", savedStory.getTitle());
                    
                    // Get user's stories
                    List<Story> stories = userManager.getUserStories(sessionId);
                    logger.info("Total stories by user: {}", stories.size());
                    
                } catch (Exception e) {
                    logger.error("Error while managing stories: {}", e.getMessage());
                }
                
                // Logout
                userManager.logout(sessionId);
                logger.info("User logged out successfully");
                
            } catch (Exception e) {
                logger.error("Demo application error: {}", e.getMessage());
            }
        };
    }
}
}
package tesfaye.venieri.software;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.time.LocalDateTime;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;

@SpringBootApplication
public class MainApplication {
    private static final String DEMO_USER_EMAIL = "user@example.com";
    private static final String DEMO_USER_PASSWORD = "Test@123";
    private static final String DEMO_USER_NAME = "testUser";

    @Value("${app.demo.enabled:false}")
    private boolean demoEnabled;
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    @Autowired
    private UserManager userManager;
    
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner runDemo() {
        return args -> {
            if (!demoEnabled) {
                logger.info("Demo mode is disabled");
                return;
            }
            logger.info("Starting demo application...");
            
            try {
                // Initialize test user if not exists
                String testEmail = DEMO_USER_EMAIL;
                String testPassword = DEMO_USER_PASSWORD;
                String testUsername = DEMO_USER_NAME;
                
                try {
                    userManager.registerUser(testUsername, testEmail, testPassword);
                    logger.info("Test user created successfully");
                } catch (IllegalArgumentException e) {
                    logger.warn("Test user already exists: {}", e.getMessage());
                }
                
                // Attempt login
                logger.info("Attempting login with test credentials");
                String sessionId = userManager.login(testEmail, testPassword);
                
                // Get current user after registration/login
                User currentUser = userManager.getCurrentUser(sessionId);
                logger.info("Logged in as: {}", currentUser.getUsername());
                
                try {
                    // Create a story using UserManager's method
                    Story story = userManager.createStory(
                        "Adventure", 
                        "A thrilling tale of coding and debugging...", 
                        false,
                        sessionId
                    );
                    
                    logger.info("Story created: {}", story.getTitle());
                    
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
                logger.error("Demo application error: {}", e.getMessage(), e);
                if (e instanceof IllegalArgumentException) {
                    logger.warn("Invalid demo configuration detected");
                }
            }
        };
    }
}
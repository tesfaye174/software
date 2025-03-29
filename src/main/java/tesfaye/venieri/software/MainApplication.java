package tesfaye.venieri.software;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Repository.UserRepository;
import tesfaye.venieri.software.Repository.StoryRepository;

@SpringBootApplication
public class MainApplication {

    @Autowired
    private UserManager userManager;
    
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner runDemo() {
        return args -> {
            System.out.println("Avvio dell'applicazione demo...");
            
            // Login
            boolean loginSuccess = userManager.login("user@example.com", "password");
            
            if (loginSuccess) {
                // Get the current logged-in user
                User currentUser = userManager.getCurrentUser();
                System.out.println("Logged in as: " + currentUser.getUsername());
                
                // Create a story
                Story story = userManager.createStory("Adventure", "A thrilling tale...");
                System.out.println("Story created: " + story.getTitle());
                
                // Get user's stories
                List<Story> stories = userManager.getUserStories();
                System.out.println("Total stories by user: " + stories.size());
                
                // Logout
                userManager.logout();
                System.out.println("User logged out");
            } else {
                System.out.println("Login failed");
            }
        };
    }
}
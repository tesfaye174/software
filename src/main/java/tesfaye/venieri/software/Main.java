package tesfaye.venieri.software;

import java.util.List;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;

public class Main {
    public static void main(String[] args) {
        // Get the UserManager instance
        UserManager userManager = UserManager.getInstance();

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
    }
}
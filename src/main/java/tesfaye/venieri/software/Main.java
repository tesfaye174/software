package tesfaye.venieri.software;

import java.util.List;

// This class should be created in the same package as UserManager
// Typically placed in the same source folder,
// for example: src/main/java/tesfaye/venieri/software/Main.java
public class Main {
    public static void main(String[] args) {
        // Get the UserManager instance
        UserManager userManager = UserManager.getInstance();

        // Login
        boolean loginSuccess = userManager.login("user@example.com", "password");

        if (loginSuccess) {
            // Get the current logged-in user
            UserManager.User currentUser = userManager.getCurrentUser();
            System.out.println("Logged in as: " + currentUser.getUsername());

            // Create a story
            UserManager.Story story = userManager.createStory("Adventure", "A thrilling tale...");
            System.out.println("Story created: " + story.getTitle());

            // Get user's stories
            List<UserManager.Story> stories = userManager.getUserStories();
            System.out.println("Total stories by user: " + stories.size());

            // Logout
            userManager.logout();
            System.out.println("User logged out");
        } else {
            System.out.println("Login failed");
        }
    }
}
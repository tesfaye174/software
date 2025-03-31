package tesfaye.venieri.software.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.Scene;
import tesfaye.venieri.software.Model.Choice;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import tesfaye.venieri.software.Exception.GameSessionException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class GameSessionService extends BaseService {
    private final Map<Long, GameState> activeSessions = new ConcurrentHashMap<>();
    
    @Autowired
    private StoryService storyService;
    
    @Autowired
    private ChoiceService choiceService;
    
    public void startNewSession(User user, Story story) {
        try {
            logOperationStart("startNewSession", "Starting new session for user: " + user.getUsername());
            
            if (activeSessions.containsKey(user.getId())) {
                throw new GameSessionException("User already has an active session");
            }
            
            GameState state = new GameState();
            state.setCurrentScene(story.getStartingScene());
            state.setStory(story);
            state.setUser(user);
            state.setProgress(new GameProgress());
            
            activeSessions.put(user.getId(), state);
            logOperationComplete("startNewSession", "Session started successfully");
        } catch (Exception e) {
            handleException(e, "Error starting session");
            throw e;
        }
    }
    
    public GameProgress makeChoice(Long userId, Long choiceId) {
        try {
            logOperationStart("makeChoice", "Processing choice for user: " + userId);
            
            GameState state = activeSessions.get(userId);
            if (state == null) {
                throw new GameSessionException("No active session found for user");
            }
            
            Choice choice = choiceService.findById(choiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Choice not found with ID: " + choiceId));
            
            if (!state.getCurrentScene().getChoices().contains(choice)) {
                throw new GameSessionException("Invalid choice for current scene");
            }
            
            // Update game state
            state.getProgress().addChoice(choice);
            state.setCurrentScene(choice.getNextScene());
            
            // If the scene is final, end the session
            if (state.getCurrentScene().getIsFinal()) {
                endSession(userId);
            }
            
            logOperationComplete("makeChoice", "Choice processed successfully");
            return state.getProgress();
        } catch (Exception e) {
            handleException(e, "Error processing choice");
            throw e;
        }
    }
    
    public void endSession(Long userId) {
        try {
            logOperationStart("endSession", "Ending session for user: " + userId);
            GameState state = activeSessions.remove(userId);
            if (state != null) {
                // Here you can add logic to save final progress
                logOperationComplete("endSession", "Session ended successfully");
            }
        } catch (Exception e) {
            handleException(e, "Error ending session");
            throw e;
        }
    }
    
    public GameState getCurrentState(Long userId) {
        GameState state = activeSessions.get(userId);
        if (state == null) {
            throw new GameSessionException("No active session found for user");
        }
        return state;
    }
    
    // Inner class for managing game state
    public static class GameState {
        private Scene currentScene;
        private Story story;
        private User user;
        private GameProgress progress;
        
        // Getters and Setters
        public Scene getCurrentScene() { return currentScene; }
        public void setCurrentScene(Scene currentScene) { this.currentScene = currentScene; }
        
        public Story getStory() { return story; }
        public void setStory(Story story) { this.story = story; }
        
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        
        public GameProgress getProgress() { return progress; }
        public void setProgress(GameProgress progress) { this.progress = progress; }
    }
    
    // Inner class for managing game progress
    public static class GameProgress {
        private List<Choice> choices = new ArrayList<>();
        private Map<String, Object> gameState = new ConcurrentHashMap<>();
        
        public void addChoice(Choice choice) {
            choices.add(choice);
        }
        
        public List<Choice> getChoices() {
            return choices;
        }
        
        public void setGameState(String key, Object value) {
            gameState.put(key, value);
        }
        
        public Object getGameState(String key) {
            return gameState.get(key);
        }
    }
}
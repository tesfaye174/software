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
            logOperationStart("startNewSession", "Avvio nuova sessione per utente: " + user.getUsername());
            
            if (activeSessions.containsKey(user.getId())) {
                throw new GameSessionException("L'utente ha già una sessione attiva");
            }
            
            GameState state = new GameState();
            state.setCurrentScene(story.getStartingScene());
            state.setStory(story);
            state.setUser(user);
            state.setProgress(new GameProgress());
            
            activeSessions.put(user.getId(), state);
            logOperationComplete("startNewSession", "Sessione avviata con successo");
        } catch (Exception e) {
            handleException(e, "Errore durante l'avvio della sessione");
            throw e;
        }
    }
    
    public GameProgress makeChoice(Long userId, Long choiceId) {
        try {
            logOperationStart("makeChoice", "Elaborazione scelta per utente: " + userId);
            
            GameState state = activeSessions.get(userId);
            if (state == null) {
                throw new GameSessionException("Nessuna sessione attiva trovata per l'utente");
            }
            
            Choice choice = choiceService.findById(choiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Scelta non trovata con ID: " + choiceId));
            
            if (!state.getCurrentScene().getChoices().contains(choice)) {
                throw new GameSessionException("Scelta non valida per la scena corrente");
            }
            
            // Aggiorna lo stato del gioco
            state.getProgress().addChoice(choice);
            state.setCurrentScene(choice.getDestinationScene());
            
            // Se la scena è finale, termina la sessione
            if (state.getCurrentScene().isFinal()) {
                endSession(userId);
            }
            
            logOperationComplete("makeChoice", "Scelta elaborata con successo");
            return state.getProgress();
        } catch (Exception e) {
            handleException(e, "Errore durante l'elaborazione della scelta");
            throw e;
        }
    }
    
    public void endSession(Long userId) {
        try {
            logOperationStart("endSession", "Terminazione sessione per utente: " + userId);
            GameState state = activeSessions.remove(userId);
            if (state != null) {
                // Qui puoi aggiungere la logica per salvare il progresso finale
                logOperationComplete("endSession", "Sessione terminata con successo");
            }
        } catch (Exception e) {
            handleException(e, "Errore durante la terminazione della sessione");
            throw e;
        }
    }
    
    public GameState getCurrentState(Long userId) {
        GameState state = activeSessions.get(userId);
        if (state == null) {
            throw new GameSessionException("Nessuna sessione attiva trovata per l'utente");
        }
        return state;
    }
    
    // Classe interna per gestire lo stato del gioco
    public static class GameState {
        private Scene currentScene;
        private Story story;
        private User user;
        private GameProgress progress;
        
        // Getters e Setters
        public Scene getCurrentScene() { return currentScene; }
        public void setCurrentScene(Scene currentScene) { this.currentScene = currentScene; }
        
        public Story getStory() { return story; }
        public void setStory(Story story) { this.story = story; }
        
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        
        public GameProgress getProgress() { return progress; }
        public void setProgress(GameProgress progress) { this.progress = progress; }
    }
    
    // Classe interna per gestire il progresso del gioco
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
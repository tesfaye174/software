@Service
public class GameSessionService {
    private final Map<Long, GameState> activeSessions = new ConcurrentHashMap<>();
    
    public void startNewSession(User user, Story story) {
        GameState state = new GameState();
        state.setCurrentScene(story.getStartingScene());
        activeSessions.put(user.getId(), state);
    }
    
    public GameProgress makeChoice(Long userId, Long choiceId) {
        GameState state = activeSessions.get(userId);
        // Implement choice logic and story progression
        return state.getProgress();
    }
}
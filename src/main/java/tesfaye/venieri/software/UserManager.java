package tesfaye.venieri.software;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Repository.UserRepository;
import tesfaye.venieri.software.Repository.StoryRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Service
@Transactional
public class UserManager {
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    
    public UserManager(UserRepository userRepository, StoryRepository storyRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.sessionRegistry = new SessionRegistryImpl();
    }
    
    /**
     * Effettua il login di un utente
     * @param email Email dell'utente
     * @param password Password dell'utente
     * @return true se il login è avvenuto con successo, false altrimenti
     * @throws IllegalArgumentException se email o password sono null o vuoti
     */
    public String login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be empty");
        }
        
        Optional<User> userOpt = Optional.ofNullable(findUserByEmail(email));
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        String sessionId = generateSessionId();
        sessionRegistry.registerNewSession(sessionId, user);
        return sessionId;
    }
    
    private String generateSessionId() {
        return java.util.UUID.randomUUID().toString();
    }
    
    public User getCurrentUser(String sessionId) {
        Object principal = sessionRegistry.getSessionInformation(sessionId).getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new IllegalStateException("No user logged in for this session");
    }
    
    /**
     * Restituisce l'utente attualmente loggato
     * @return L'utente corrente o null se nessun utente è loggato
     */
    public void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters long and contain at least one digit, " +
                "one lowercase letter, one uppercase letter, one special character, and no whitespace");
        }
    }
    
    @Transactional
    public User registerUser(String username, String email, String password) {
        validatePassword(password);
        
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already taken");
        }
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        
        return userRepository.save(newUser);
    }
    
    /**
     * Crea una nuova storia per l'utente corrente
     * @param title Titolo della storia
     * @param content Contenuto della storia
     * @param isEnding Indica se la storia è un finale o ha continuazioni
     * @return La storia creata
     * @throws IllegalStateException se nessun utente è loggato
     * @throws IllegalArgumentException se titolo o contenuto sono null o vuoti
     */
    public Story createStory(String title, String content, boolean isEnding) {
        if (currentUser == null) {
            throw new IllegalStateException("Nessun utente loggato");
        }
        
        // Validazione input
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere vuoto");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Il contenuto non può essere vuoto");
        }
        
        // Creiamo una nuova storia e la associamo all'utente corrente
        Story story = new Story(title, content, isEnding, currentUser);
        // Salviamo la storia nel database
        storyRepository.save(story);
        logger.info("Creazione di una nuova storia iniziata per l'utente: {}", currentUser.getUsername());
        
        return story;
    }
    
    /**
     * Crea una nuova storia per l'utente corrente (non finale)
     * @param title Titolo della storia
     * @param content Contenuto della storia
     * @return La storia creata
     * @throws IllegalStateException se nessun utente è loggato
     */
    public Story createStory(String title, String content) {
        return createStory(title, content, false);
    }
    
    /**
     * Restituisce tutte le storie dell'utente corrente
     * @return Lista delle storie dell'utente
     * @throws IllegalStateException se nessun utente è loggato
     */
    public List<Story> getUserStories() {
        if (currentUser == null) {
            throw new IllegalStateException("Nessun utente loggato");
        }
        
        // Recuperiamo le storie dal database filtrate per l'utente corrente
        return storyRepository.findByUserId(currentUser.getId());
    }
    
    /**
     * Cerca storie per titolo
     * @param title Titolo o parte di titolo da cercare
     * @return Lista delle storie che contengono il titolo specificato
     */
    public List<Story> searchStoriesByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo di ricerca non può essere vuoto");
        }
        
        return storyRepository.findByTitleContaining(title);
    }
    
    /**
     * Effettua il logout dell'utente corrente
     */
    public void logout(String sessionId) {
        sessionRegistry.removeSessionInformation(sessionId);
    }
    
    /**
     * Metodo di supporto per trovare un utente tramite email
     * @param email Email dell'utente da cercare
     * @return L'utente trovato o null
     */
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Transactional
    public User updateUserProfile(String sessionId, String username, String email) {
        User currentUser = getCurrentUser(sessionId);
        
        if (username != null && !username.trim().isEmpty()) {
            User existingUser = userRepository.findByUsername(username);
            if (existingUser != null && !existingUser.getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Username already taken");
            }
            currentUser.setUsername(username);
        }
        
        if (email != null && !email.trim().isEmpty()) {
            User existingUser = userRepository.findByEmail(email);
            if (existingUser != null && !existingUser.getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Email already registered");
            }
            currentUser.setEmail(email);
        }
        
        return userRepository.save(currentUser);
    }
    
    @Transactional
    public void changePassword(String sessionId, String oldPassword, String newPassword) {
        User currentUser = getCurrentUser(sessionId);
        
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        validatePassword(newPassword);
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        logger.info("Storia creata con successo: {}", story.getTitle());
    }
}
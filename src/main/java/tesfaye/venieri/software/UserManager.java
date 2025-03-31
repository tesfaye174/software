package tesfaye.venieri.software;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Repository.UserRepository;
import tesfaye.venieri.software.Repository.StoryRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    
    @Autowired
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
        
        User user = findUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
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
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        
        var sessionInfo = sessionRegistry.getSessionInformation(sessionId);
        if (sessionInfo == null || sessionInfo.isExpired()) {
            throw new IllegalStateException("Invalid or expired session");
        }
        
        Object principal = sessionInfo.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new IllegalStateException("No user logged in for this session");
    }

    public Story createStory(String title, String content, boolean isEnding, String sessionId) {
        User currentUser = getCurrentUser(sessionId);
        
        // Validazione input
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere vuoto");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Il contenuto non può essere vuoto");
        }
        
        Story story = new Story();
        story.setTitle(title);
        story.setContent(content);
        story.setPremium(isEnding);
        story.setAuthor(currentUser);
        
        Story savedStory = storyRepository.save(story);
        logger.info("Storia creata: {} (autore: {})", title, currentUser.getUsername());
        
        return savedStory;
    }
    
    /**
     * Crea una nuova storia per l'utente corrente (non finale)
     * @param title Titolo della storia
     * @param content Contenuto della storia
     * @return La storia creata
     * @throws IllegalStateException se nessun utente è loggato
     */
    public Story createStory(String title, String content, String sessionId) {
        return createStory(title, content, false, sessionId);
    }

    /**
     * Restituisce tutte le storie dell'utente corrente
     * @return Lista delle storie dell'utente
     * @throws IllegalStateException se nessun utente è loggato
     */
    public List<Story> getUserStories(String sessionId) {
        User currentUser = getCurrentUser(sessionId);
        if (currentUser == null) {
            throw new IllegalStateException("Nessun utente loggato");
        }
        
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
        return userRepository.findByEmail(email).orElse(null);
    }
    
    @Transactional
    public User updateUserProfile(String sessionId, String username, String email) {
        User currentUser = getCurrentUser(sessionId);
        
        if (username != null && !username.trim().isEmpty()) {
            User existingUser = userRepository.findByUsername(username).orElse(null);
            if (existingUser != null && !existingUser.getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Username already taken");
            }
            currentUser.setUsername(username);
        }
        
        if (email != null && !email.trim().isEmpty()) {
            User existingUser = userRepository.findByEmail(email).orElse(null);
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
        logger.info("Password cambiata con successo per l'utente: {}", currentUser.getUsername());
    }

    /**
     * Registra un nuovo utente nel sistema
     * @param username Nome utente
     * @param email Email dell'utente
     * @param password Password dell'utente
     * @return L'utente creato
     * @throws IllegalArgumentException se i parametri non sono validi o se l'email/username è già in uso
     */
    @Transactional
    public User registerUser(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Verifica se l'email è già registrata
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Verifica se lo username è già in uso
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Validazione password
        validatePassword(password);

        // Crea il nuovo utente
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPremium(false);

        User savedUser = userRepository.save(user);
        logger.info("Utente registrato con successo: {}", username);
        return savedUser;
    }

    private void validatePassword(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one digit, one uppercase letter, one lowercase letter, and one special character");
        }
    }
}
package tesfaye.venieri.software;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Repository.UserRepository;
import tesfaye.venieri.software.Repository.StoryRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserManager {
    
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    
    public UserManager(UserRepository userRepository, StoryRepository storyRepository) {
        this.userRepository = userRepository;
        this.storyRepository = storyRepository;
    }
    
    private User currentUser = null;
    
    /**
     * Effettua il login di un utente
     * @param email Email dell'utente
     * @param password Password dell'utente
     * @return true se il login è avvenuto con successo, false altrimenti
     * @throws IllegalArgumentException se email o password sono null o vuoti
     */
    public boolean login(String email, String password) {
        // Validazione input
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email e password non possono essere vuoti");
        }
        
        // In un'applicazione reale, dovremmo cercare l'utente nel database
        // e verificare la password in modo sicuro usando BCrypt:
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Optional<User> userOpt = Optional.ofNullable(findUserByEmail(email));
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Verifica della password (in un'app reale dovrebbe essere criptata con BCrypt)
            if (encoder.matches(password, user.getPassword())) {
                this.currentUser = user;
                return true;
            }
        } else {
            // Crea un utente di test per demo
            User newUser = new User("testUser", encoder.encode(password), email);
            // Salviamo l'utente nel database
            userRepository.save(newUser);
            this.currentUser = newUser;
            return true;
        }
        return false;
    }
    
    /**
     * Restituisce l'utente attualmente loggato
     * @return L'utente corrente o null se nessun utente è loggato
     */
    public User getCurrentUser() {
        return currentUser;
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
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Metodo di supporto per trovare un utente tramite email
     * @param email Email dell'utente da cercare
     * @return L'utente trovato o null
     */
    private User findUserByEmail(String email) {
        // Cerchiamo l'utente nel database
        return userRepository.findByEmail(email);
    }
}
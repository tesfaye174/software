package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Repository.UserRepository;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import java.util.List;
import java.util.Collections;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    @Transactional
    public User save(User user) {
        try {
            logOperationStart("saveUser", "Salvataggio utente: " + user.getUsername());
            validatePassword(user.getPassword());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            logOperationComplete("saveUser", "Utente salvato con successo: " + savedUser.getUsername());
            return savedUser;
        } catch (Exception e) {
            handleException(e, "Errore durante il salvataggio dell'utente: " + user.getUsername());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        try {
            logOperationStart("existsByUsername", "Verifica esistenza utente: " + username);
            boolean exists = userRepository.findByUsername(username).isPresent();
            logOperationComplete("existsByUsername", "Risultato verifica: " + exists);
            return exists;
        } catch (Exception e) {
            handleException(e, "Errore durante la verifica dell'esistenza dell'utente: " + username);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        try {
            logOperationStart("existsByEmail", "Verifica esistenza email: " + email);
            boolean exists = userRepository.findByEmail(email).isPresent();
            logOperationComplete("existsByEmail", "Risultato verifica: " + exists);
            return exists;
        } catch (Exception e) {
            handleException(e, "Errore durante la verifica dell'esistenza dell'email: " + email);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        try {
            logOperationStart("findByUsername", "Ricerca utente: " + username);
            Optional<User> user = userRepository.findByUsername(username);
            logOperationComplete("findByUsername", user.isPresent() ? "Utente trovato" : "Utente non trovato");
            return user;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca dell'utente: " + username);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public User update(User user) {
        try {
            logOperationStart("updateUser", "Aggiornamento utente: " + user.getUsername());
            if (!userRepository.existsById(user.getId())) {
                throw new ResourceNotFoundException("Utente non trovato con ID: " + user.getId());
            }
            User updatedUser = userRepository.save(user);
            logOperationComplete("updateUser", "Utente aggiornato con successo: " + updatedUser.getUsername());
            return updatedUser;
        } catch (Exception e) {
            handleException(e, "Errore durante l'aggiornamento dell'utente: " + user.getUsername());
            return null;
        }
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        try {
            logOperationStart("deleteUser", "Eliminazione utente con ID: " + userId);
            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException("Utente non trovato con ID: " + userId);
            }
            userRepository.deleteById(userId);
            logOperationComplete("deleteUser", "Utente eliminato con successo");
        } catch (Exception e) {
            handleException(e, "Errore durante l'eliminazione dell'utente con ID: " + userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        try {
            logOperationStart("findById", "Ricerca utente con ID: " + id);
            Optional<User> user = userRepository.findById(id);
            logOperationComplete("findById", user.isPresent() ? "Utente trovato" : "Utente non trovato");
            return user;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca dell'utente con ID: " + id);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        try {
            logOperationStart("findAll", "Recupero di tutti gli utenti");
            List<User> users = userRepository.findAll();
            logOperationComplete("findAll", "Recuperati " + users.size() + " utenti");
            return users;
        } catch (Exception e) {
            handleException(e, "Errore durante il recupero di tutti gli utenti");
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        try {
            logOperationStart("findByEmail", "Ricerca utente per email: " + email);
            Optional<User> user = userRepository.findByEmail(email);
            logOperationComplete("findByEmail", user.isPresent() ? "Utente trovato" : "Utente non trovato");
            return user;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca dell'utente per email: " + email);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            logOperationStart("changePassword", "Cambio password utente con ID: " + userId);
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new IllegalArgumentException("Password corrente non corretta");
            }

            validatePassword(newPassword);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            logOperationComplete("changePassword", "Password cambiata con successo");
        } catch (Exception e) {
            handleException(e, "Errore durante il cambio password per l'utente con ID: " + userId);
            throw e;
        }
    }

    private void validatePassword(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("La password deve essere di almeno 8 caratteri e contenere almeno una cifra, una lettera maiuscola, una lettera minuscola e un carattere speciale");
        }
    }
}
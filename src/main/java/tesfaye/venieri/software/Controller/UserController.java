package tesfaye.venieri.software.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import it.storieinterattive.model.Utente;
import it.storieinterattive.repository.UtenteRepository;

import java.util.List;

/**
 * Controller for managing user operations
 * Extends the generic ModelRepositoryController for standard CRUD operations
 */
@RestController
@RequestMapping("/api/users")
public class UserController extends ModelRepositoryController<Utente, UtenteRepository> {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UtenteRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Utente> getAll() {
        return repository.findAll();
    }

    @Override
    public ResponseEntity<Utente> getById(@PathVariable Long id) {
        Utente utente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(utente);
    }

    @Override
    public Utente create(@RequestBody Utente utente) {
        // Encode password before saving
        if (!utente.getPassword().startsWith("$2a$")) {
            utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        }
        return repository.save(utente);
    }

    @Override
    public ResponseEntity<Utente> update(@PathVariable Long id, @RequestBody Utente utenteDetails) {
        Utente utente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        utente.setUsername(utenteDetails.getUsername());
        
        // Only update password if it's provided and not already encoded
        if (utenteDetails.getPassword() != null && !utenteDetails.getPassword().isEmpty() 
                && !utenteDetails.getPassword().startsWith("$2a$")) {
            utente.setPassword(passwordEncoder.encode(utenteDetails.getPassword()));
        }
        
        utente.setPremium(utenteDetails.isPremium());

        Utente updatedUtente = repository.save(utente);
        return ResponseEntity.ok(updatedUtente);
    }

    /**
     * Find user by username
     * 
     * @param username The username to search for
     * @return The user with the given username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Utente> getByUsername(@PathVariable String username) {
        Utente utente = repository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return ResponseEntity.ok(utente);
    }

    /**
     * Upgrade a user to premium status
     * 
     * @param id The user ID
     * @return The updated user
     */
    @PutMapping("/{id}/upgrade-to-premium")
    public ResponseEntity<Utente> upgradeToPremium(@PathVariable Long id) {
        Utente utente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        utente.setPremium(true);
        Utente updatedUtente = repository.save(utente);
        
        return ResponseEntity.ok(updatedUtente);
    }
}
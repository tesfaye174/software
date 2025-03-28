package tesfaye.venieri.software;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Verifica se username o email sono già in uso
        if (userRepository.existsByUsername(user.getUsername())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Username già in uso");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Email già in uso");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // In un'applicazione reale, dovresti criptare la password prima di salvarla
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        
        // Non restituire la password nella risposta
        savedUser.setPassword(null);
        
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // In un'applicazione reale, dovresti verificare la password criptata
            // if (passwordEncoder.matches(password, user.getPassword())) {
            if (password.equals(user.getPassword())) {
                // Non restituire la password nella risposta
                user.setPassword(null);
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("error", "Username o password non validi");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(null); // Non restituire la password
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
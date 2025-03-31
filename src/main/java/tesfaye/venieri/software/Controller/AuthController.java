package tesfaye.venieri.software.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Service.UserService;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import tesfaye.venieri.software.Exception.UnauthorizedException;

import jakarta.validation.Valid;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Visualizzazione form di registrazione");
        try {
            User newUser = new User();
            newUser.setPremium(false); // Imposta lo stato premium di default
            model.addAttribute("user", newUser);
            return "auth/register";
        } catch (Exception e) {
            logger.error("Errore durante la visualizzazione del form di registrazione: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la visualizzazione del form");
        }
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                             BindingResult result, Model model) {
        logger.info("Tentativo di registrazione utente: {}", user.getUsername());
        try {
            if (result.hasErrors()) {
                logger.warn("Errori di validazione durante la registrazione: {}", result.getAllErrors());
                return "auth/register";
            }

            if (userService.existsByUsername(user.getUsername())) {
                logger.warn("Username già in uso: {}", user.getUsername());
                model.addAttribute("usernameError", "Username già in uso");
                return "auth/register";
            }

            if (userService.existsByEmail(user.getEmail())) {
                logger.warn("Email già registrata: {}", user.getEmail());
                model.addAttribute("emailError", "Email già registrata");
                return "auth/register";
            }

            userService.save(user);
            logger.info("Utente registrato con successo: {}", user.getUsername());
            return "redirect:/login?registered";
        } catch (Exception e) {
            logger.error("Errore durante la registrazione: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la registrazione");
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        logger.info("Visualizzazione form di login");
        return "auth/login";
    }

    @GetMapping("/premium")
    public String showPremiumPage(Model model) {
        logger.info("Visualizzazione pagina premium");
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));
            
            model.addAttribute("user", user);
            return "auth/premium";
        } catch (ResourceNotFoundException e) {
            logger.error("Utente non trovato durante l'accesso alla pagina premium");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato");
        } catch (Exception e) {
            logger.error("Errore durante l'accesso alla pagina premium: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante l'accesso alla pagina premium");
        }
    }

    @PostMapping("/premium/process")
    public String processPremiumPayment() {
        logger.info("Elaborazione pagamento premium");
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));
            
            // Simulazione del pagamento
            // In un'implementazione reale, qui ci sarebbe l'integrazione con un servizio di pagamento esterno
            
            user.setPremium(true);
            userService.save(user);
            logger.info("Pagamento premium elaborato con successo per l'utente: {}", username);
            
            return "redirect:/home?premiumSuccess";
        } catch (ResourceNotFoundException e) {
            logger.error("Utente non trovato durante l'elaborazione del pagamento premium");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato");
        } catch (Exception e) {
            logger.error("Errore durante l'elaborazione del pagamento premium: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante l'elaborazione del pagamento");
        }
    }
}

package tesfaye.venieri.software.Controller;

import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;  // Updated to jakarta.validation

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        User newUser = new User();
        newUser.setIsPremium(false); // Set default premium status
        model.addAttribute("user", newUser);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("usernameError", "Username already in use");
            return "register";
        }

        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email already registered");
            return "register";
        }

        userService.save(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/premium")
    public String showPremiumPage() {
        return "premium";
    }

    @PostMapping("/premium/process")
    public String processPremiumPayment() {
        // Simulazione del pagamento
        // In un'implementazione reale, qui ci sarebbe l'integrazione con un servizio di pagamento esterno
        
        // Per ora, aggiorniamo direttamente lo stato dell'utente
        User currentUser = userService.getCurrentUser();
        currentUser.setIsPremium(true);
        userService.save(currentUser);
        
        return "redirect:/home?premiumSuccess";
    }
}

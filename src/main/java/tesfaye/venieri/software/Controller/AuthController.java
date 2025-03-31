package tesfaye.venieri.software.Controller;

import .model.Utente;
import tesfaye.venieri.software.Service.UtenteService;  // Fixed import path
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

    private final UtenteService utenteService;

    @Autowired
    public AuthController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("utente", new Utente());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("utente") Utente utente, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (utenteService.existsByUsername(utente.getUsername())) {
            model.addAttribute("usernameError", "Username già in uso");
            return "register";
        }

        utenteService.save(utente);
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
        // In un caso reale, questo avverrebbe dopo la conferma del pagamento
        // TODO: Implementare l'integrazione con un servizio di pagamento esterno
        
        return "redirect:/home?premiumSuccess";
    }
}

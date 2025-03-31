package tesfaye.venieri.software.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Service.UserService;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import tesfaye.venieri.software.Exception.UnauthorizedException;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller per la gestione degli utenti.
 * Gestisce le operazioni CRUD e le funzionalità relative agli utenti.
 */
@Controller
@RequestMapping("/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    /**
     * Mostra la lista di tutti gli utenti
     * @param model Il modello per la vista
     * @return La vista della lista utenti
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        try {
            logger.info("Recupero lista utenti");
            List<User> users = userService.findAll();
            model.addAttribute("users", users);
            return "user/list";
        } catch (Exception e) {
            logger.error("Errore nel recupero degli utenti", e);
            model.addAttribute("error", "Errore nel recupero degli utenti");
            return "error";
        }
    }

    /**
     * Mostra il form di creazione utente
     * @param model Il modello per la vista
     * @return La vista del form di creazione
     */
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        logger.info("Visualizzazione form creazione utente");
        model.addAttribute("user", new User());
        return "user/form";
    }

    /**
     * Crea un nuovo utente
     * @param user L'utente da creare
     * @param result Il risultato della validazione
     * @param redirectAttributes Attributi per il redirect
     * @return Redirect alla lista utenti
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(@Valid @ModelAttribute User user,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                logger.warn("Errore di validazione nella creazione utente");
                return "user/form";
            }
            
            if (userService.existsByEmail(user.getEmail())) {
                result.rejectValue("email", "error.email", "Email già in uso");
                return "user/form";
            }
            
            if (userService.existsByUsername(user.getUsername())) {
                result.rejectValue("username", "error.username", "Username già in uso");
                return "user/form";
            }

            userService.save(user);
            logger.info("Utente creato con successo: {}", user.getUsername());
            redirectAttributes.addFlashAttribute("success", "Utente creato con successo");
            return "redirect:/users";
        } catch (Exception e) {
            logger.error("Errore nella creazione dell'utente", e);
            redirectAttributes.addFlashAttribute("error", "Errore nella creazione dell'utente");
            return "redirect:/users";
        }
    }

    /**
     * Mostra il form di modifica utente
     * @param id L'ID dell'utente da modificare
     * @param model Il modello per la vista
     * @return La vista del form di modifica
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            logger.info("Visualizzazione form modifica utente: {}", id);
            User user = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));
            model.addAttribute("user", user);
            return "user/form";
        } catch (ResourceNotFoundException e) {
            logger.error("Utente non trovato: {}", id);
            model.addAttribute("error", "Utente non trovato");
            return "error";
        }
    }

    /**
     * Aggiorna un utente esistente
     * @param id L'ID dell'utente da aggiornare
     * @param user I dati aggiornati dell'utente
     * @param result Il risultato della validazione
     * @param redirectAttributes Attributi per il redirect
     * @return Redirect alla lista utenti
     */
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Long id,
                           @Valid @ModelAttribute User user,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                logger.warn("Errore di validazione nell'aggiornamento utente");
                return "user/form";
            }

            User existingUser = userService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

            if (!existingUser.getEmail().equals(user.getEmail()) && 
                userService.existsByEmail(user.getEmail())) {
                result.rejectValue("email", "error.email", "Email già in uso");
                return "user/form";
            }

            user.setId(id); // Imposta l'ID per l'aggiornamento
            userService.save(user);
            logger.info("Utente aggiornato con successo: {}", user.getUsername());
            redirectAttributes.addFlashAttribute("success", "Utente aggiornato con successo");
            return "redirect:/users";
        } catch (ResourceNotFoundException e) {
            logger.error("Utente non trovato per l'aggiornamento: {}", id);
            redirectAttributes.addFlashAttribute("error", "Utente non trovato");
            return "redirect:/users";
        }
    }

    /**
     * Elimina un utente
     * @param id L'ID dell'utente da eliminare
     * @param redirectAttributes Attributi per il redirect
     * @return Redirect alla lista utenti
     */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Eliminazione utente: {}", id);
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Utente eliminato con successo");
            return "redirect:/users";
        } catch (ResourceNotFoundException e) {
            logger.error("Utente non trovato per l'eliminazione: {}", id);
            redirectAttributes.addFlashAttribute("error", "Utente non trovato");
            return "redirect:/users";
        }
    }

    /**
     * Mostra il profilo dell'utente corrente
     * @param model Il modello per la vista
     * @return La vista del profilo
     */
    @GetMapping("/profile")
    public String showProfile(Model model) {
        try {
            logger.info("Visualizzazione profilo utente corrente");
            User currentUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Utente non autenticato"));
            model.addAttribute("user", currentUser);
            return "user/profile";
        } catch (UnauthorizedException e) {
            logger.error("Utente non autenticato");
            return "redirect:/login";
        }
    }

    /**
     * Aggiorna il profilo dell'utente corrente
     * @param user I dati aggiornati dell'utente
     * @param result Il risultato della validazione
     * @param redirectAttributes Attributi per il redirect
     * @return Redirect al profilo
     */
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute User user,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                logger.warn("Errore di validazione nell'aggiornamento profilo");
                return "user/profile";
            }

            User currentUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Utente non autenticato"));
            
            user.setId(currentUser.getId()); // Mantieni l'ID esistente
            userService.save(user);
            
            logger.info("Profilo utente aggiornato con successo");
            redirectAttributes.addFlashAttribute("success", "Profilo aggiornato con successo");
            return "redirect:/users/profile";
        } catch (UnauthorizedException e) {
            logger.error("Utente non autenticato");
            return "redirect:/login";
        }
    }
}
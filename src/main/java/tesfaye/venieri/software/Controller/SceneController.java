package tesfaye.venieri.software.Controller;

import tesfaye.venieri.software.Model.*;
import tesfaye.venieri.software.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import tesfaye.venieri.software.Exception.UnauthorizedException;

@Controller
@RequestMapping("/scenes")
public class SceneController {
    private static final Logger logger = LoggerFactory.getLogger(SceneController.class);
    private final SceneService sceneService;
    private final StoryService storyService;
    private final ChoiceService choiceService;
    private final RiddleService riddleService;
    private final ItemService itemService;

    @Autowired
    public SceneController(SceneService sceneService, StoryService storyService,
                          ChoiceService choiceService, RiddleService riddleService,
                          ItemService itemService) {
        this.sceneService = sceneService;
        this.storyService = storyService;
        this.choiceService = choiceService;
        this.riddleService = riddleService;
        this.itemService = itemService;
    }

    @GetMapping("/create/{storyId}")
    public String showCreationForm(@PathVariable Long storyId, Model model) {
        logger.info("Visualizzazione form di creazione scena per la storia ID: {}", storyId);
        try {
            Story story = storyService.findById(storyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata"));
            
            verifyAuthor(story);
            
            model.addAttribute("story", story);
            model.addAttribute("scene", new Scene());
            return "scenes/create";
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata: {}", storyId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Storia non trovata");
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato a creare scene per questa storia");
        } catch (Exception e) {
            logger.error("Errore durante la visualizzazione del form: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la visualizzazione del form");
        }
    }

    @PostMapping("/create/{storyId}")
    public String createScene(@PathVariable Long storyId, @Valid @ModelAttribute("scene") Scene scene,
                            BindingResult result, Model model) {
        logger.info("Creazione nuova scena per la storia ID: {}", storyId);
        try {
            Story story = storyService.findById(storyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata"));
            
            verifyAuthor(story);
            
            if (result.hasErrors()) {
                model.addAttribute("story", story);
                return "scenes/create";
            }
            
            scene.setStory(story);
            Scene savedScene = sceneService.save(scene);
            return "redirect:/scenes/edit/" + savedScene.getId();
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata: {}", storyId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Storia non trovata");
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato a creare scene per questa storia");
        } catch (Exception e) {
            logger.error("Errore durante la creazione della scena: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la creazione della scena");
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Visualizzazione form di modifica scena ID: {}", id);
        try {
            Scene scene = sceneService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Scena non trovata"));
            
            verifyAuthor(scene.getStory());
            
            model.addAttribute("scene", scene);
            model.addAttribute("choices", choiceService.findByScene(scene));
            model.addAttribute("riddles", riddleService.findByScene(scene));
            model.addAttribute("items", itemService.findByScene(scene));
            return "scenes/edit";
        } catch (ResourceNotFoundException e) {
            logger.error("Scena non trovata: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scena non trovata");
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato a modificare questa scena");
        } catch (Exception e) {
            logger.error("Errore durante la visualizzazione del form: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la visualizzazione del form");
        }
    }

    @PostMapping("/edit/{id}")
    public String editScene(@PathVariable Long id, @Valid @ModelAttribute("scene") Scene scene,
                          BindingResult result, Model model) {
        logger.info("Modifica scena ID: {}", id);
        try {
            Scene existingScene = sceneService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Scena non trovata"));
            
            verifyAuthor(existingScene.getStory());
            
            if (result.hasErrors()) {
                model.addAttribute("choices", choiceService.findByScene(existingScene));
                model.addAttribute("riddles", riddleService.findByScene(existingScene));
                model.addAttribute("items", itemService.findByScene(existingScene));
                return "scenes/edit";
            }
            
            existingScene.setText(scene.getText());
            existingScene.setFinal(scene.isFinal());
            
            sceneService.save(existingScene);
            return "redirect:/scenes/edit/" + id;
        } catch (ResourceNotFoundException e) {
            logger.error("Scena non trovata: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scena non trovata");
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato a modificare questa scena");
        } catch (Exception e) {
            logger.error("Errore durante la modifica della scena: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la modifica della scena");
        }
    }

    private void verifyAuthor(Story story) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (!story.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("Non sei autorizzato a modificare questa storia");
        }
    }
}

package tesfaye.venieri.software.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import tesfaye.venieri.software.Exception.UnauthorizedException;
import tesfaye.venieri.software.Model.Scene;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Repository.SceneRepository;
import tesfaye.venieri.software.Repository.StoryRepository;
import tesfaye.venieri.software.Repository.UserRepository;
import tesfaye.venieri.software.Service.SceneService;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller per la gestione delle scene.
 * Gestisce le operazioni CRUD e le funzionalità relative alle scene.
 */
@RestController
@RequestMapping("/api/scenes")
public class SceneController {

    private static final Logger logger = LoggerFactory.getLogger(SceneController.class);
    
    private final SceneRepository sceneRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final SceneService sceneService;

    @Autowired
    public SceneController(SceneRepository sceneRepository,
                          StoryRepository storyRepository,
                          UserRepository userRepository,
                          SceneService sceneService) {
        this.sceneRepository = sceneRepository;
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.sceneService = sceneService;
    }

    /**
     * Recupera tutte le scene di una storia
     * @param storyId ID della storia
     * @return Lista delle scene della storia
     */
    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Scene>> getScenesByStory(@PathVariable Long storyId) {
        try {
            logger.info("Recupero scene della storia: {}", storyId);
            Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata con ID: " + storyId));
            
            List<Scene> scenes = sceneRepository.findByStoryId(story.getId());
            return ResponseEntity.ok(scenes);
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata: {}", storyId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Errore nel recupero delle scene", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recupera una scena specifica
     * @param id ID della scena
     * @return La scena trovata
     */
    @GetMapping("/{id}")
    public ResponseEntity<Scene> getSceneById(@PathVariable Long id) {
        try {
            logger.info("Recupero scena con ID: {}", id);
            Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scena non trovata con ID: " + id));
            return ResponseEntity.ok(scene);
        } catch (ResourceNotFoundException e) {
            logger.error("Scena non trovata: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Errore nel recupero della scena", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea una nuova scena
     * @param scene La scena da creare
     * @return La scena creata
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Scene> createScene(@Valid @RequestBody Scene scene) {
        try {
            logger.info("Creazione nuova scena");
            User currentUser = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Utente non autenticato"));

            // Verifica che l'utente sia l'autore della storia
            Story story = storyRepository.findById(scene.getStory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata"));

            if (!story.getAuthor().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Non sei autorizzato a creare scene per questa storia");
            }

            Scene savedScene = sceneRepository.save(scene);
            logger.info("Scena creata con successo: {}", savedScene.getId());
            return ResponseEntity.ok(savedScene);
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Errore nella creazione della scena", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Aggiorna una scena esistente
     * @param id ID della scena da aggiornare
     * @param sceneDetails Dettagli aggiornati della scena
     * @return La scena aggiornata
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Scene> updateScene(@PathVariable Long id, 
                                           @Valid @RequestBody Scene sceneDetails) {
        try {
            logger.info("Aggiornamento scena con ID: {}", id);
            Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scena non trovata con ID: " + id));

            // Verifica che l'utente sia l'autore della storia
            User currentUser = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Utente non autenticato"));

            if (!scene.getStory().getAuthor().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Non sei autorizzato a modificare questa scena");
            }

            scene.setTitle(sceneDetails.getTitle());
            scene.setContent(sceneDetails.getContent());
            scene.setIsFinal(sceneDetails.getIsFinal());

            Scene updatedScene = sceneRepository.save(scene);
            logger.info("Scena aggiornata con successo: {}", id);
            return ResponseEntity.ok(updatedScene);
        } catch (ResourceNotFoundException e) {
            logger.error("Scena non trovata per l'aggiornamento: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato per l'aggiornamento: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Errore nell'aggiornamento della scena", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina una scena
     * @param id ID della scena da eliminare
     * @return Risposta di conferma
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteScene(@PathVariable Long id) {
        try {
            logger.info("Eliminazione scena con ID: {}", id);
            Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scena non trovata con ID: " + id));

            // Verifica che l'utente sia l'autore della storia
            User currentUser = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Utente non autenticato"));

            if (!scene.getStory().getAuthor().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Non sei autorizzato a eliminare questa scena");
            }

            sceneRepository.delete(scene);
            logger.info("Scena eliminata con successo: {}", id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            logger.error("Scena non trovata per l'eliminazione: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato per l'eliminazione: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Errore nell'eliminazione della scena", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recupera le scene finali di una storia
     * @param storyId ID della storia
     * @return Lista delle scene finali
     */
    @GetMapping("/story/{storyId}/final")
    public ResponseEntity<List<Scene>> getFinalScenes(@PathVariable Long storyId) {
        try {
            logger.info("Recupero scene finali della storia: {}", storyId);
            Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata con ID: " + storyId));
            
            List<Scene> finalScenes = sceneRepository.findByStoryIdAndIsFinalTrue(story.getId());
            return ResponseEntity.ok(finalScenes);
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata: {}", storyId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Errore nel recupero delle scene finali", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

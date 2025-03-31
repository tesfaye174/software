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
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Repository.StoryRepository;
import tesfaye.venieri.software.Repository.UserRepository;
import tesfaye.venieri.software.Service.StoryService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller per la gestione delle storie.
 * Gestisce le operazioni CRUD e le funzionalità relative alle storie.
 */
@RestController
@RequestMapping("/api/stories")
public class StoryController {

    private static final Logger logger = LoggerFactory.getLogger(StoryController.class);
    
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryService storyService;

    @Autowired
    public StoryController(StoryRepository storyRepository, 
                          UserRepository userRepository,
                          StoryService storyService) {
        this.storyRepository = storyRepository;
        this.userRepository = userRepository;
        this.storyService = storyService;
    }

    /**
     * Recupera tutte le storie
     * @return Lista di tutte le storie
     */
    @GetMapping
    public ResponseEntity<List<Story>> getAllStories() {
        try {
            logger.info("Recupero lista storie");
            List<Story> stories = storyRepository.findAll();
            return ResponseEntity.ok(stories);
        } catch (Exception e) {
            logger.error("Errore nel recupero delle storie", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Recupera una storia specifica
     * @param id ID della storia
     * @return La storia trovata
     */
    @GetMapping("/{id}")
    public ResponseEntity<Story> getStoryById(@PathVariable Long id) {
        try {
            logger.info("Recupero storia con ID: {}", id);
            Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata con ID: " + id));
            return ResponseEntity.ok(story);
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Errore nel recupero della storia", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crea una nuova storia
     * @param story La storia da creare
     * @return La storia creata
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Story> createStory(@Valid @RequestBody Story story) {
        try {
            logger.info("Creazione nuova storia");
            User currentUser = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Utente non autenticato"));

            story.setAuthor(currentUser);
            story.setCreationDate(LocalDateTime.now());
            
            Story savedStory = storyRepository.save(story);
            logger.info("Storia creata con successo: {}", savedStory.getId());
            return ResponseEntity.ok(savedStory);
        } catch (UnauthorizedException e) {
            logger.error("Utente non autenticato");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Errore nella creazione della storia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Aggiorna una storia esistente
     * @param id ID della storia da aggiornare
     * @param storyDetails Dettagli aggiornati della storia
     * @return La storia aggiornata
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Story> updateStory(@PathVariable Long id, 
                                           @Valid @RequestBody Story storyDetails) {
        try {
            logger.info("Aggiornamento storia con ID: {}", id);
            Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata con ID: " + id));

            // Verifica che l'utente corrente sia l'autore
            User currentUser = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Utente non autenticato"));

            if (!story.getAuthor().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Non sei autorizzato a modificare questa storia");
            }

            story.setTitle(storyDetails.getTitle());
            story.setDescription(storyDetails.getDescription());

            Story updatedStory = storyRepository.save(story);
            logger.info("Storia aggiornata con successo: {}", id);
            return ResponseEntity.ok(updatedStory);
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata per l'aggiornamento: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato per l'aggiornamento: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Errore nell'aggiornamento della storia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina una storia
     * @param id ID della storia da eliminare
     * @return Risposta di conferma
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        try {
            logger.info("Eliminazione storia con ID: {}", id);
            Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata con ID: " + id));

            // Verifica che l'utente corrente sia l'autore
            User currentUser = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UnauthorizedException("Utente non autenticato"));

            if (!story.getAuthor().getId().equals(currentUser.getId())) {
                throw new UnauthorizedException("Non sei autorizzato a eliminare questa storia");
            }

            storyRepository.delete(story);
            logger.info("Storia eliminata con successo: {}", id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata per l'eliminazione: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            logger.error("Utente non autorizzato per l'eliminazione: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.error("Errore nell'eliminazione della storia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recupera tutte le storie di un autore specifico
     * @param authorId ID dell'autore
     * @return Lista delle storie dell'autore
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Story>> getStoriesByAuthor(@PathVariable Long authorId) {
        try {
            logger.info("Recupero storie dell'autore: {}", authorId);
            User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Autore non trovato con ID: " + authorId));
            
            List<Story> stories = storyRepository.findByAuthorId(author.getId());
            return ResponseEntity.ok(stories);
        } catch (ResourceNotFoundException e) {
            logger.error("Autore non trovato: {}", authorId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Errore nel recupero delle storie dell'autore", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Recupera le storie pubbliche
     * @return Lista delle storie pubbliche
     */
    @GetMapping("/public")
    public ResponseEntity<List<Story>> getPublicStories() {
        try {
            logger.info("Recupero storie pubbliche");
            List<Story> stories = storyService.findPublicStories();
            return ResponseEntity.ok(stories);
        } catch (Exception e) {
            logger.error("Errore nel recupero delle storie pubbliche", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
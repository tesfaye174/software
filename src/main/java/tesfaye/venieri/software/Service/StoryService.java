package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.StorySearchCriteria;
import tesfaye.venieri.software.Repository.StoryRepository;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servizio per la gestione delle storie.
 * Fornisce metodi per le operazioni CRUD e la logica di business.
 */
@Service
public class StoryService extends BaseService {

    private final StoryRepository storyRepository;

    @Autowired
    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    /**
     * Recupera tutte le storie
     * @return Lista di tutte le storie
     */
    @Transactional(readOnly = true)
    public List<Story> findAll() {
        try {
            logOperationStart("findAll", "Recupero di tutte le storie");
            List<Story> stories = storyRepository.findAll();
            logOperationComplete("findAll", "Recuperate " + stories.size() + " storie");
            return stories;
        } catch (Exception e) {
            handleException(e, "Errore durante il recupero di tutte le storie");
            return List.of();
        }
    }

    /**
     * Recupera una storia per ID
     * @param id ID della storia
     * @return Optional contenente la storia se trovata
     */
    @Transactional(readOnly = true)
    public Optional<Story> findById(Long id) {
        try {
            logOperationStart("findById", "Ricerca storia con ID: " + id);
            Optional<Story> story = storyRepository.findById(id);
            logOperationComplete("findById", story.isPresent() ? "Storia trovata" : "Storia non trovata");
            return story;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca della storia con ID: " + id);
            return Optional.empty();
        }
    }

    /**
     * Recupera le storie di un autore specifico
     * @param authorId ID dell'autore
     * @return Lista delle storie dell'autore
     */
    @Transactional(readOnly = true)
    public List<Story> findByAuthorId(Long authorId) {
        try {
            logOperationStart("findByAuthorId", "Ricerca storie per autore ID: " + authorId);
            List<Story> stories = storyRepository.findByAuthorId(authorId);
            logOperationComplete("findByAuthorId", "Trovate " + stories.size() + " storie");
            return stories;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca delle storie per l'autore ID: " + authorId);
            return List.of();
        }
    }

    /**
     * Recupera le storie pubbliche
     * @return Lista delle storie pubbliche
     */
    @Transactional(readOnly = true)
    public List<Story> findPublicStories() {
        try {
            logOperationStart("findPublicStories", "Ricerca storie pubbliche");
            List<Story> stories = storyRepository.findByIsPublicTrue();
            logOperationComplete("findPublicStories", "Trovate " + stories.size() + " storie pubbliche");
            return stories;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca delle storie pubbliche");
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<Story> findByUserId(Long userId) {
        try {
            logOperationStart("findByUserId", "Ricerca storie per utente ID: " + userId);
            List<Story> stories = storyRepository.findByUserId(userId);
            logOperationComplete("findByUserId", "Trovate " + stories.size() + " storie");
            return stories;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca delle storie per l'utente ID: " + userId);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<Story> searchByTitle(String title) {
        try {
            logOperationStart("searchByTitle", "Ricerca storie per titolo: " + title);
            List<Story> stories = storyRepository.findByTitleContaining(title);
            logOperationComplete("searchByTitle", "Trovate " + stories.size() + " storie");
            return stories;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca delle storie per titolo: " + title);
            return List.of();
        }
    }

    /**
     * Salva una storia
     * @param story La storia da salvare
     * @return La storia salvata
     */
    @Transactional
    public Story save(Story story) {
        try {
            logOperationStart("save", "Salvataggio storia: " + story.getTitle());
            Story savedStory = storyRepository.save(story);
            logOperationComplete("save", "Storia salvata con successo: " + savedStory.getId());
            return savedStory;
        } catch (Exception e) {
            handleException(e, "Errore durante il salvataggio della storia: " + story.getTitle());
            return null;
        }
    }

    /**
     * Elimina una storia
     * @param story La storia da eliminare
     */
    @Transactional
    public void delete(Story story) {
        try {
            logOperationStart("delete", "Eliminazione storia: " + story.getId());
            storyRepository.delete(story);
            logOperationComplete("delete", "Storia eliminata con successo");
        } catch (Exception e) {
            handleException(e, "Errore durante l'eliminazione della storia: " + story.getId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            logOperationStart("deleteById", "Eliminazione storia con ID: " + id);
            if (!storyRepository.existsById(id)) {
                throw new ResourceNotFoundException("Storia non trovata con ID: " + id);
            }
            storyRepository.deleteById(id);
            logOperationComplete("deleteById", "Storia eliminata con successo");
        } catch (Exception e) {
            handleException(e, "Errore durante l'eliminazione della storia con ID: " + id);
        }
    }

    @Transactional(readOnly = true)
    public List<Story> searchStories(StorySearchCriteria criteria) {
        try {
            logOperationStart("searchStories", "Ricerca storie con criteri");
            List<Story> stories = storyRepository.findByTitleContainingAndAuthorId(
                criteria.getTitle(), 
                criteria.getAuthorId()
            );
            logOperationComplete("searchStories", "Trovate " + stories.size() + " storie");
            return stories;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca delle storie con criteri");
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<Story> findPopularStories(int limit) {
        try {
            logOperationStart("findPopularStories", "Ricerca storie popolari con limite: " + limit);
            List<Story> stories = storyRepository.findTopByOrderByViewsDesc(limit);
            logOperationComplete("findPopularStories", "Trovate " + stories.size() + " storie popolari");
            return stories;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca delle storie popolari");
            return List.of();
        }
    }
}
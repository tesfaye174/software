package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tesfaye.venieri.software.Model.Scene;

import java.util.List;

/**
 * Repository per la gestione delle scene.
 * Fornisce metodi per l'accesso ai dati delle scene nel database.
 */
@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {
    
    /**
     * Recupera tutte le scene di una storia specifica
     * @param storyId ID della storia
     * @return Lista delle scene della storia
     */
    List<Scene> findByStoryId(Long storyId);
    
    /**
     * Recupera tutte le scene finali di una storia
     * @param storyId ID della storia
     * @return Lista delle scene finali
     */
    List<Scene> findByStoryIdAndIsFinalTrue(Long storyId);
} 
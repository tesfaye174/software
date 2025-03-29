package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tesfaye.venieri.software.Model.Story;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    // Metodi di ricerca per titolo
    List<Story> findByTitleContainingIgnoreCase(String title);
    
    // Metodi di ricerca per contenuto
    List<Story> findByContentContainingIgnoreCase(String content);
    
    // Metodi di ricerca per stato di fine
    List<Story> findByIsEnding(boolean isEnding);
    
    // Metodi di ricerca combinati
    List<Story> findByTitleContainingIgnoreCaseAndIsEnding(String title, boolean isEnding);
    List<Story> findByContentContainingIgnoreCaseAndIsEnding(String content, boolean isEnding);
    
    // Metodo per trovare storie che non hanno scelte (storie incomplete)
    @Query("SELECT s FROM Story s WHERE s.choices.size = 0")
    List<Story> findStoriesWithoutChoices();
    
    // Metodo per trovare storie con un numero minimo di scelte
    @Query("SELECT s FROM Story s WHERE s.choices.size >= :minChoices")
    List<Story> findStoriesWithMinChoices(@Param("minChoices") int minChoices);
    
    // Metodo per trovare storie che sono collegate ad una storia specifica tramite scelte
    @Query("SELECT DISTINCT c.nextStory FROM Choice c WHERE c.currentStory.id = :storyId AND c.nextStory IS NOT NULL")
    List<Story> findConnectedStories(@Param("storyId") Long storyId);
}

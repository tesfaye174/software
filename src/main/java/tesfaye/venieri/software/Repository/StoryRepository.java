package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tesfaye.venieri.software.Model.Story;

import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    // Metodo per trovare le storie di un utente specifico
    List<Story> findByUserId(Long userId);
    
    // Metodo per trovare le storie per titolo (utile per ricerche)
    List<Story> findByTitleContaining(String title);
    
    // Metodo per trovare le storie per titolo e autore
    List<Story> findByTitleContainingAndAuthorId(String title, Long authorId);
    
    // Metodo per trovare le storie più popolari
    List<Story> findTopByOrderByViewsDesc(int limit);
    
    // Metodo per trovare le storie di un autore specifico
    List<Story> findByAuthorId(Long authorId);
}
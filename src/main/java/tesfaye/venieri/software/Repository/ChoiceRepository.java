package tesfaye.venieri.software.Repository;

import tesfaye.venieri.software.Model.Choice;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione delle scelte nel gioco.
 * Fornisce metodi per accedere e manipolare i dati delle scelte disponibili.
 */
@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    
    /**
     * Trova tutte le scelte disponibili per una storia specifica
     * @param currentStory La storia di cui si vogliono trovare le scelte
     * @return Lista delle scelte disponibili
     */
    List<Choice> findByCurrentStory(Story currentStory);

    /**
     * Trova tutte le scelte che portano a una storia specifica
     * @param nextStory La storia di destinazione
     * @return Lista delle scelte che portano alla storia
     */
    List<Choice> findByNextStory(Story nextStory);

    /**
     * Trova tutte le scelte disponibili in una scena specifica
     * @param scene La scena di cui si vogliono trovare le scelte
     * @return Lista delle scelte disponibili nella scena
     */
    List<Choice> findByScene(Scene scene);

    /**
     * Trova tutte le scelte che richiedono un oggetto specifico
     * @param itemId L'ID dell'oggetto richiesto
     * @return Lista delle scelte che richiedono l'oggetto
     */
    @Query("SELECT c FROM Choice c WHERE c.requiresItem = true AND c.requiredItem.id = :itemId")
    List<Choice> findByRequiredItem(@Param("itemId") Long itemId);

    /**
     * Trova tutte le scelte che danno un oggetto specifico
     * @param itemId L'ID dell'oggetto dato
     * @return Lista delle scelte che danno l'oggetto
     */
    @Query("SELECT c FROM Choice c WHERE c.givesItem = true AND c.itemToGive.id = :itemId")
    List<Choice> findByItemToGive(@Param("itemId") Long itemId);

    /**
     * Trova tutte le scelte che richiedono la soluzione di un indovinello specifico
     * @param riddleId L'ID dell'indovinello richiesto
     * @return Lista delle scelte che richiedono la soluzione dell'indovinello
     */
    @Query("SELECT c FROM Choice c WHERE c.requiredRiddle.id = :riddleId")
    List<Choice> findByRequiredRiddle(@Param("riddleId") Long riddleId);

    /**
     * Trova tutte le scelte che portano a una scena specifica
     * @param sceneId L'ID della scena di destinazione
     * @return Lista delle scelte che portano alla scena
     */
    @Query("SELECT c FROM Choice c WHERE c.nextScene.id = :sceneId")
    List<Choice> findByNextScene(@Param("sceneId") Long sceneId);

    /**
     * Verifica se una scelta è disponibile in una scena specifica
     * @param choiceId L'ID della scelta
     * @param sceneId L'ID della scena
     * @return true se la scelta è disponibile nella scena, false altrimenti
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Choice c WHERE c.id = :choiceId AND c.scene.id = :sceneId")
    boolean isChoiceAvailableInScene(@Param("choiceId") Long choiceId, @Param("sceneId") Long sceneId);
}
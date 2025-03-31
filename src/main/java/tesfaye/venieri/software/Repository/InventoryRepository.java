package tesfaye.venieri.software.Repository;

import tesfaye.venieri.software.Model.Inventory;
import tesfaye.venieri.software.Model.Game;
import tesfaye.venieri.software.Model.CollectedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione degli inventari del gioco.
 * Fornisce metodi per accedere e manipolare i dati degli inventari.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    /**
     * Trova l'inventario associato a un gioco specifico
     * @param game Il gioco di cui si vuole trovare l'inventario
     * @return L'inventario del gioco, se presente
     */
    Optional<Inventory> findByGame(Game game);

    /**
     * Trova tutti gli inventari che contengono un oggetto specifico
     * @param itemId L'ID dell'oggetto da cercare
     * @return Lista di inventari che contengono l'oggetto
     */
    @Query("SELECT i FROM Inventory i JOIN i.collectedItems ci WHERE ci.item.id = :itemId")
    List<Inventory> findByCollectedItemId(@Param("itemId") Long itemId);

    /**
     * Verifica se un oggetto è presente in un inventario specifico
     * @param inventoryId L'ID dell'inventario
     * @param itemId L'ID dell'oggetto da cercare
     * @return true se l'oggetto è presente nell'inventario, false altrimenti
     */
    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM Inventory i JOIN i.collectedItems ci WHERE i.id = :inventoryId AND ci.item.id = :itemId")
    boolean hasItem(@Param("inventoryId") Long inventoryId, @Param("itemId") Long itemId);

    /**
     * Trova tutti gli oggetti raccolti in un inventario specifico
     * @param inventoryId L'ID dell'inventario
     * @return Lista di oggetti raccolti
     */
    @Query("SELECT ci FROM CollectedItem ci WHERE ci.inventory.id = :inventoryId")
    List<CollectedItem> findCollectedItemsByInventoryId(@Param("inventoryId") Long inventoryId);
}

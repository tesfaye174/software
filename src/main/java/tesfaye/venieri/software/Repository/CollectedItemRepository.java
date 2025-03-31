package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tesfaye.venieri.software.Model.CollectedItem;
import tesfaye.venieri.software.Model.Inventory;
import tesfaye.venieri.software.Model.Item;

import java.util.List;

@Repository
public interface CollectedItemRepository extends JpaRepository<CollectedItem, Long> {
    boolean existsByInventoryAndItem(Inventory inventory, Item item);
    List<CollectedItem> findByInventory(Inventory inventory);
} 
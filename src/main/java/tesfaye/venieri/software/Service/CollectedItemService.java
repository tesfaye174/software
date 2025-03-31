package tesfaye.venieri.software.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tesfaye.venieri.software.Model.*;
import tesfaye.venieri.software.Repository.CollectedItemRepository;

import java.util.List;

@Service
public class CollectedItemService {
    
    private final CollectedItemRepository collectedItemRepository;

    @Autowired
    public CollectedItemService(CollectedItemRepository collectedItemRepository) {
        this.collectedItemRepository = collectedItemRepository;
    }

    public boolean existsByInventoryAndItem(Inventory inventory, Item item) {
        return collectedItemRepository.existsByInventoryAndItem(inventory, item);
    }

    public void addItemToInventory(Inventory inventory, Item item) {
        CollectedItem collectedItem = new CollectedItem();
        collectedItem.setInventory(inventory);
        collectedItem.setItem(item);
        collectedItemRepository.save(collectedItem);
    }

    public List<CollectedItem> findByInventory(Inventory inventory) {
        return collectedItemRepository.findByInventory(inventory);
    }
} 
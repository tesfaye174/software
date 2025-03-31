package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Inventory;
import tesfaye.venieri.software.Model.Game;
import tesfaye.venieri.software.Repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> findById(Long id) {
        return inventoryRepository.findById(id);
    }

    public Optional<Inventory> findByGame(Game game) {
        return inventoryRepository.findByGame(game);
    }

    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Inventory createEmptyInventory(Game game) {
        Inventory inventory = new Inventory();
        inventory.setGame(game);
        return inventoryRepository.save(inventory);
    }

    public void delete(Inventory inventory) {
        inventoryRepository.delete(inventory);
    }

    public void deleteById(Long id) {
        inventoryRepository.deleteById(id);
    }
}

package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Inventory;
import tesfaye.venieri.software.Model.Game;
import tesfaye.venieri.software.Repository.InventoryRepository;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
public class InventoryService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Inventory> findAll() {
        try {
            logOperationStart("findAll", "Recupero di tutti gli inventari");
            List<Inventory> inventories = inventoryRepository.findAll();
            logOperationComplete("findAll", "Recuperati " + inventories.size() + " inventari");
            return inventories;
        } catch (Exception e) {
            handleException(e, "Errore durante il recupero di tutti gli inventari");
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Inventory> findById(Long id) {
        try {
            logOperationStart("findById", "Ricerca inventario con ID: " + id);
            Optional<Inventory> inventory = inventoryRepository.findById(id);
            logOperationComplete("findById", inventory.isPresent() ? "Inventario trovato" : "Inventario non trovato");
            return inventory;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca dell'inventario con ID: " + id);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Inventory> findByGame(Game game) {
        try {
            logOperationStart("findByGame", "Ricerca inventario per gioco con ID: " + game.getId());
            Optional<Inventory> inventory = inventoryRepository.findByGame(game);
            logOperationComplete("findByGame", inventory.isPresent() ? "Inventario trovato" : "Inventario non trovato");
            return inventory;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca dell'inventario per il gioco con ID: " + game.getId());
            return Optional.empty();
        }
    }

    @Transactional
    public Inventory save(Inventory inventory) {
        try {
            logOperationStart("save", "Salvataggio inventario");
            validateInventory(inventory);
            Inventory savedInventory = inventoryRepository.save(inventory);
            logOperationComplete("save", "Inventario salvato con successo con ID: " + savedInventory.getId());
            return savedInventory;
        } catch (Exception e) {
            handleException(e, "Errore durante il salvataggio dell'inventario");
            return null;
        }
    }

    @Transactional
    public Inventory createEmptyInventory(Game game) {
        try {
            logOperationStart("createEmptyInventory", "Creazione inventario vuoto per il gioco con ID: " + game.getId());
            Inventory inventory = new Inventory();
            inventory.setGame(game);
            inventory.setCollectedItems(Collections.emptySet());
            Inventory savedInventory = inventoryRepository.save(inventory);
            logOperationComplete("createEmptyInventory", "Inventario vuoto creato con successo con ID: " + savedInventory.getId());
            return savedInventory;
        } catch (Exception e) {
            handleException(e, "Errore durante la creazione dell'inventario vuoto per il gioco con ID: " + game.getId());
            return null;
        }
    }

    @Transactional
    public void delete(Inventory inventory) {
        try {
            logOperationStart("delete", "Eliminazione inventario con ID: " + inventory.getId());
            if (!inventoryRepository.existsById(inventory.getId())) {
                throw new ResourceNotFoundException("Inventario non trovato con ID: " + inventory.getId());
            }
            inventoryRepository.delete(inventory);
            logOperationComplete("delete", "Inventario eliminato con successo");
        } catch (Exception e) {
            handleException(e, "Errore durante l'eliminazione dell'inventario con ID: " + inventory.getId());
        }
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            logOperationStart("deleteById", "Eliminazione inventario con ID: " + id);
            if (!inventoryRepository.existsById(id)) {
                throw new ResourceNotFoundException("Inventario non trovato con ID: " + id);
            }
            inventoryRepository.deleteById(id);
            logOperationComplete("deleteById", "Inventario eliminato con successo");
        } catch (Exception e) {
            handleException(e, "Errore durante l'eliminazione dell'inventario con ID: " + id);
        }
    }

    private void validateInventory(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("L'inventario non può essere null");
        }
        if (inventory.getGame() == null) {
            throw new IllegalArgumentException("Il gioco associato all'inventario non può essere null");
        }
        if (inventory.getCollectedItems() == null) {
            inventory.setCollectedItems(Collections.emptySet());
        }
    }
}
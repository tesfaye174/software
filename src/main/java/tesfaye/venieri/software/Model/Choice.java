package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Rappresenta una scelta disponibile in una scena del gioco.
 * Ogni scelta può portare a una nuova scena e può richiedere condizioni specifiche per essere disponibile.
 */
@Entity
@Table(name = "choices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"scene", "destinationScene", "requiredItem", "requiredRiddle", "itemToGive"})
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il testo della scelta non può essere vuoto")
    @Size(min = 3, max = 200, message = "Il testo deve essere tra 3 e 200 caratteri")
    @Column(length = 200, nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id", nullable = false)
    @NotNull(message = "La scena è obbligatoria")
    private Scene scene;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_scene_id", nullable = false)
    @NotNull(message = "La scena di destinazione è obbligatoria")
    private Scene destinationScene;

    @Column(name = "requires_item", nullable = false)
    private boolean requiresItem = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "required_item_id")
    private Item requiredItem;

    @Column(name = "requires_riddle_solution", nullable = false)
    private boolean requiresRiddleSolution = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "required_riddle_id")
    private Riddle requiredRiddle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_scene_id", nullable = false)
    @NotNull(message = "La scena successiva è obbligatoria")
    private Scene nextScene;

    @Column(name = "gives_item", nullable = false)
    private boolean givesItem = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_to_give_id")
    private Item itemToGive;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_required_id")
    private Item itemRequired;

    /**
     * Costruttore semplificato per creare una scelta base
     * @param text Il testo della scelta
     * @param scene La scena corrente
     * @param destinationScene La scena di destinazione
     */
    public Choice(String text, Scene scene, Scene destinationScene) {
        this.text = text;
        this.scene = scene;
        this.destinationScene = destinationScene;
    }

    /**
     * Verifica se questa scelta è disponibile in base alle condizioni richieste
     * @param inventory L'inventario del giocatore
     * @return true se la scelta è disponibile, false altrimenti
     * @throws IllegalArgumentException se l'inventario è null
     */
    public boolean isAvailable(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("L'inventario non può essere null");
        }
        
        if (requiresItem && requiredItem != null) {
            return inventory.getCollectedItems().stream()
                    .anyMatch(collectedItem -> collectedItem.getItem().getId().equals(requiredItem.getId()));
        }
        
        if (requiresRiddleSolution && requiredRiddle != null) {
            return requiredRiddle.isSolved();
        }
        
        return true;
    }

    /**
     * Applica gli effetti della scelta all'inventario del giocatore
     * @param inventory L'inventario del giocatore
     */
    public void applyChoice(Inventory inventory) {
        if (givesItem && itemToGive != null) {
            CollectedItem collectedItem = new CollectedItem();
            collectedItem.setItem(itemToGive);
            collectedItem.setInventory(inventory);
            inventory.getCollectedItems().add(collectedItem);
        }
    }

    /**
     * Imposta questa scelta come corretta
     */
    public void markAsCorrect() {
        this.isCorrect = true;
    }

    /**
     * Verifica se questa scelta è corretta
     * @return true se la scelta è corretta, false altrimenti
     */
    public boolean isCorrectChoice() {
        return isCorrect;
    }

    public Scene getNextScene() {
        return nextScene;
    }

    public void setNextScene(Scene nextScene) {
        this.nextScene = nextScene;
    }

    public boolean getGivesItem() {
        return givesItem;
    }

    public void setGivesItem(boolean givesItem) {
        this.givesItem = givesItem;
    }

    public Item getItemToGive() {
        return itemToGive;
    }

    public void setItemToGive(Item itemToGive) {
        this.itemToGive = itemToGive;
    }

    public Item getItemRequired() {
        return itemRequired;
    }

    public void setItemRequired(Item itemRequired) {
        this.itemRequired = itemRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Choice)) return false;
        Choice choice = (Choice) o;
        return getId() != null && getId().equals(choice.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // Constructors, getters and setters are handled by Lombok
}

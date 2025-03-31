package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Rappresenta una scelta disponibile in una scena del gioco.
 * Ogni scelta può portare a una nuova scena e può richiedere un oggetto specifico.
 */
@Entity
@Table(name = "choices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"scene", "destinationScene", "requiredItem"})
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il testo della scelta non può essere vuoto")
    @Size(min = 3, max = 200, message = "Il testo deve essere tra 3 e 200 caratteri")
    @Column(length = 200, nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scene_id", nullable = false)
    private Scene scene;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_scene_id", nullable = false)
    private Scene destinationScene;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "required_item_id")
    private Item requiredItem;

    /**
     * Verifica se questa scelta è disponibile in base agli oggetti posseduti
     * @param inventory L'inventario del giocatore
     * @return true se la scelta è disponibile, false altrimenti
     */
    public Choice(String text, Scene scene, Scene destinationScene) {
        this.text = text;
        this.scene = scene;
        this.destinationScene = destinationScene;
    }

    public boolean isAvailable(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }
        
        if (requiredItem == null) {
            return true;
        }
        
        return inventory.getCollectedItems().stream()
                .anyMatch(collectedItem -> collectedItem.getItem().getId().equals(requiredItem.getId()));
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

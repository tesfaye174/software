package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Rappresenta una scena nel gioco.
 * Ogni scena contiene testo descrittivo, scelte possibili, enigmi e oggetti.
 */
@Entity
@Table(name = "scenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"story", "choices", "riddles", "items"})
public class Scene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il testo della scena non può essere vuoto")
    @Size(min = 10, max = 5000, message = "Il testo deve essere tra 10 e 5000 caratteri")
    @Column(length = 5000, nullable = false)
    private String text;

    @Column(name = "is_final", nullable = false)
    private boolean isFinal = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Choice> choices = new HashSet<>();

    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Riddle> riddles = new HashSet<>();

    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Item> items = new HashSet<>();

    /**
     * Restituisce le scelte disponibili per il giocatore in base al suo inventario
     * @param inventory L'inventario del giocatore
     * @return Set di scelte disponibili
     */
    public Set<Choice> getAvailableChoices(Inventory inventory) {
        Set<Choice> availableChoices = new HashSet<>();
        for (Choice choice : choices) {
            if (choice.isAvailable(inventory)) {
                availableChoices.add(choice);
            }
        }
        return availableChoices;
    }

    /**
     * Aggiunge una scelta alla scena
     * @param choice La scelta da aggiungere
     */
    public void addChoice(Choice choice) {
        choices.add(choice);
        choice.setScene(this);
    }

    /**
     * Aggiunge un oggetto alla scena
     * @param item L'oggetto da aggiungere
     */
    public void addItem(Item item) {
        items.add(item);
        item.setScene(this);
    }

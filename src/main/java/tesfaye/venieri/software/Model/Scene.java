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

    @NotBlank(message = "Il titolo della scena è obbligatorio")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Il contenuto della scena è obbligatorio")
    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    @NotNull(message = "La storia è obbligatoria")
    private Story story;

    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Choice> choices = new HashSet<>();

    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Riddle> riddles = new HashSet<>();

    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Item> items = new HashSet<>();

    @Column(name = "is_final", nullable = false)
    private boolean isFinal = false;

    @ManyToMany
    @JoinTable(
        name = "scene_connections",
        joinColumns = @JoinColumn(name = "scene_id"),
        inverseJoinColumns = @JoinColumn(name = "previous_scene_id")
    )
    private Set<Scene> previousScenes = new HashSet<>();

    @ManyToMany(mappedBy = "previousScenes")
    private Set<Scene> nextScenes = new HashSet<>();

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
     * Aggiunge un enigma alla scena
     * @param riddle L'enigma da aggiungere
     */
    public void addRiddle(Riddle riddle) {
        riddles.add(riddle);
        riddle.setScene(this);
    }

    /**
     * Aggiunge un oggetto alla scena
     * @param item L'oggetto da aggiungere
     */
    public void addItem(Item item) {
        items.add(item);
        item.setScene(this);
    }

    /**
     * Rimuove una scelta dalla scena
     * @param choice La scelta da rimuovere
     */
    public void removeChoice(Choice choice) {
        choices.remove(choice);
        choice.setScene(null);
    }

    /**
     * Rimuove un enigma dalla scena
     * @param riddle L'enigma da rimuovere
     */
    public void removeRiddle(Riddle riddle) {
        riddles.remove(riddle);
        riddle.setScene(null);
    }

    /**
     * Rimuove un oggetto dalla scena
     * @param item L'oggetto da rimuovere
     */
    public void removeItem(Item item) {
        items.remove(item);
        item.setScene(null);
    }

    public Set<Scene> getPreviousScenes() {
        return previousScenes;
    }

    public void addPreviousScene(Scene scene) {
        previousScenes.add(scene);
        scene.getNextScenes().add(this);
    }

    public void removePreviousScene(Scene scene) {
        previousScenes.remove(scene);
        scene.getNextScenes().remove(this);
    }

    public Set<Scene> getNextScenes() {
        return nextScenes;
    }

    public void addNextScene(Scene scene) {
        nextScenes.add(scene);
        scene.getPreviousScenes().add(this);
    }

    public void removeNextScene(Scene scene) {
        nextScenes.remove(scene);
        scene.getPreviousScenes().remove(this);
    }

    // Miglioramento della documentazione
    // Classe che rappresenta una scena nel gioco
    
    // Metodo per restituire le scelte disponibili per il giocatore in base al suo inventario
    // Metodo per aggiungere una scelta alla scena
    // Metodo per aggiungere un enigma alla scena
    // Metodo per aggiungere un oggetto alla scena
    // Metodo per rimuovere una scelta dalla scena
    // Metodo per rimuovere un enigma dalla scena
    // Metodo per rimuovere un oggetto dalla scena
}

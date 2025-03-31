package tesfaye.venieri.software.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una scena all'interno di una storia.
 * Una scena contiene il testo narrativo e le scelte disponibili.
 */
@Entity
@Table(name = "scenes")
public class Scene {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Il titolo è obbligatorio")
    private String title;
    
    @NotBlank(message = "Il contenuto è obbligatorio")
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    @NotNull(message = "La storia è obbligatoria")
    private Story story;
    
    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices = new ArrayList<>();
    
    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riddle> riddles = new ArrayList<>();
    
    @OneToMany(mappedBy = "scene", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();
    
    @Column(name = "is_final", nullable = false)
    private boolean isFinal = false;
    
    @ManyToMany
    @JoinTable(
        name = "scene_previous_scenes",
        joinColumns = @JoinColumn(name = "scene_id"),
        inverseJoinColumns = @JoinColumn(name = "previous_scene_id")
    )
    private List<Scene> previousScenes = new ArrayList<>();
    
    @ManyToMany(mappedBy = "previousScenes")
    private List<Scene> nextScenes = new ArrayList<>();

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public List<Riddle> getRiddles() {
        return riddles;
    }

    public void setRiddles(List<Riddle> riddles) {
        this.riddles = riddles;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public List<Scene> getPreviousScenes() {
        return previousScenes;
    }

    public void setPreviousScenes(List<Scene> previousScenes) {
        this.previousScenes = previousScenes;
    }

    public List<Scene> getNextScenes() {
        return nextScenes;
    }

    public void setNextScenes(List<Scene> nextScenes) {
        this.nextScenes = nextScenes;
    }

    // Metodi di utilità per la gestione delle relazioni
    public void addPreviousScene(Scene scene) {
        if (!previousScenes.contains(scene)) {
            previousScenes.add(scene);
            scene.addNextScene(this);
        }
    }

    public void removePreviousScene(Scene scene) {
        if (previousScenes.remove(scene)) {
            scene.removeNextScene(this);
        }
    }

    public void addNextScene(Scene scene) {
        if (!nextScenes.contains(scene)) {
            nextScenes.add(scene);
            scene.addPreviousScene(this);
        }
    }

    public void removeNextScene(Scene scene) {
        if (nextScenes.remove(scene)) {
            scene.removePreviousScene(this);
        }
    }
}

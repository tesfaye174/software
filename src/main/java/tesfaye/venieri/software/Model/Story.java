package tesfaye.venieri.software.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Rappresenta una storia interattiva.
 * Una storia contiene scene, scelte e oggetti che il giocatore può raccogliere.
 */
@Entity
@Table(name = "stories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(min = 5, max = 100, message = "Il titolo deve essere tra 5 e 100 caratteri")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Il contenuto è obbligatorio")
    @Lob
    @Column(nullable = false)
    private String content;
    
    @NotBlank(message = "La descrizione è obbligatoria")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @Column(name = "is_premium", nullable = false)
    private boolean isPremium = false;
    
    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;
    
    @Column(name = "views", nullable = false)
    private int views = 0;
    
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Scene> scenes = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull(message = "L'autore è obbligatorio")
    private User author;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }

    public void incrementViews() {
        this.views++;
    }

    public Scene getStartingScene() {
        return scenes.stream()
            .filter(scene -> !scene.getIsFinal() && scene.getChoices().isEmpty())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Nessuna scena iniziale trovata per la storia: " + title));
    }

    /**
     * Recupera la prima scena della storia
     * @return la prima scena della storia
     */
    public Scene getFirstScene() {
        return scenes.stream()
            .filter(scene -> scene.getPreviousScenes().isEmpty())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("La storia non ha una scena iniziale"));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void addScene(Scene scene) {
        if (!scenes.contains(scene)) {
            scenes.add(scene);
            scene.setStory(this);
        }
    }

    public void removeScene(Scene scene) {
        if (scenes.remove(scene)) {
            scene.setStory(null);
        }
    }
}
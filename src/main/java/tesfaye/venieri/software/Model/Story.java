package tesfaye.venieri.software.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    @Column(length = 1000)
    private String description;
    
    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;
    
    @Column(name = "is_premium", nullable = false)
    private boolean isPremium = false;
    
    @Column(name = "views", nullable = false)
    private int views = 0;
    
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Scene> scenes = new HashSet<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
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
            .filter(scene -> !scene.isFinal() && scene.getChoices().isEmpty())
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
}
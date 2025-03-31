package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il nome dell'oggetto è obbligatorio")
    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "is_collectible", nullable = false)
    private boolean isCollectible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id", nullable = false)
    @NotNull(message = "La scena è obbligatoria")
    private Scene scene;

    @Column(name = "is_collected", nullable = false)
    private boolean isCollected = false;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CollectedItem> collectedItems = new HashSet<>();

    @OneToMany(mappedBy = "requiredItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Choice> conditionedChoices = new HashSet<>();

    public void collect() {
        if (isCollectible) {
            this.isCollected = true;
        }
    }
}
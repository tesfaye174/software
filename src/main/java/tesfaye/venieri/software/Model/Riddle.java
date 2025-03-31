package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "riddles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Riddle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 5, max = 500)
    @Column(length = 500)
    private String text;

    @Enumerated(EnumType.STRING)
    private RiddleType type;

    @NotBlank
    @Size(min = 1, max = 100)
    private String solution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id", nullable = false)
    private Scene scene;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_scene_id", nullable = false)
    private Scene destinationScene;

    public enum RiddleType {
        TEXT,
        NUMERIC
    }
}

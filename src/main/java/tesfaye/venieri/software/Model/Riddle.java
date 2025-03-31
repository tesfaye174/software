package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "La domanda dell'enigma è obbligatoria")
    @Size(min = 5, max = 500)
    @Column(length = 500, nullable = false)
    private String question;

    @NotBlank(message = "La risposta dell'enigma è obbligatoria")
    @Size(min = 1, max = 100)
    @Column(nullable = false)
    private String answer;

    @Column(name = "is_solved", nullable = false)
    private boolean isSolved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id", nullable = false)
    @NotNull(message = "La scena è obbligatoria")
    private Scene scene;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_scene_id", nullable = false)
    @NotNull(message = "La scena di destinazione è obbligatoria")
    private Scene nextScene;

    public boolean checkAnswer(String userAnswer) {
        return answer.equalsIgnoreCase(userAnswer.trim());
    }

    public void solve() {
        this.isSolved = true;
    }

    public enum RiddleType {
        TEXT,
        NUMERIC
    }
}

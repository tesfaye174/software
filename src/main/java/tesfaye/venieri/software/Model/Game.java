package tesfaye.venieri.software.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_scene_id", nullable = false)
    private Scene currentScene;

    @Column(name = "start_date")
    private LocalDateTime startDate = LocalDateTime.now();

    @Column(name = "last_save_date")
    private LocalDateTime lastSaveDate = LocalDateTime.now();

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Inventory inventory;

    @Column(name = "completed")
    private boolean completed = false;

    // Miglioramento della documentazione
    // Classe che rappresenta un gioco
    /**
     * Metodo per inizializzare un nuovo gioco
     * @param story La storia da giocare
     * @param user Il giocatore
     * @param startingScene La scena iniziale
     */
    public Game(Story story, User user, Scene startingScene) {
        this.story = story;
        this.user = user;
        this.currentScene = startingScene;
        this.inventory = new Inventory();
        this.inventory.setGame(this);
    }

    // Metodo per aggiornare la scena corrente e salvare il progresso
    /**
     * Salva il progresso del gioco
     */
    public void saveProgress() {
        this.lastSaveDate = LocalDateTime.now();
    }

    // Metodo per verificare se il giocatore può accedere a una determinata scelta
    /**
     * Verifica se il giocatore può accedere a una determinata scelta
     * @param choice La scelta da verificare
     * @return true se il giocatore ha gli oggetti necessari per la scelta
     */
    public boolean canMakeChoice(Choice choice) {
        return choice.isAvailable(this.inventory);
    }
}
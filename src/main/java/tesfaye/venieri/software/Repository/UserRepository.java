package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tesfaye.venieri.software.Model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository per la gestione degli utenti del sistema.
 * Fornisce metodi per accedere e manipolare i dati degli utenti.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Trova un utente tramite email
     * @param email L'email dell'utente da cercare
     * @return L'utente trovato, se presente
     */
    Optional<User> findByEmail(String email);

    /**
     * Trova un utente tramite username
     * @param username Lo username dell'utente da cercare
     * @return L'utente trovato, se presente
     */
    Optional<User> findByUsername(String username);

    /**
     * Verifica se esiste un utente con una determinata email
     * @param email L'email da verificare
     * @return true se esiste un utente con quell'email, false altrimenti
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se esiste un utente con un determinato username
     * @param username Lo username da verificare
     * @return true se esiste un utente con quello username, false altrimenti
     */
    boolean existsByUsername(String username);

    /**
     * Trova tutti gli utenti che hanno un ruolo specifico
     * @param role Il ruolo da cercare
     * @return Lista degli utenti con il ruolo specificato
     */
    List<User> findByRole(String role);

    /**
     * Trova gli utenti attivi o inattivi
     * @param active Lo stato di attivazione da cercare
     * @return Lista degli utenti con lo stato specificato
     */
    List<User> findByActive(boolean active);

    /**
     * Trova gli utenti che hanno completato almeno un gioco
     * @return Lista degli utenti che hanno completato almeno un gioco
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.games g WHERE g.status = 'COMPLETED'")
    List<User> findUsersWithCompletedGames();

    /**
     * Trova gli utenti che hanno creato almeno una storia
     * @return Lista degli utenti che hanno creato almeno una storia
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.stories s")
    List<User> findUsersWithStories();

    /**
     * Trova gli utenti che hanno un punteggio totale superiore a un valore specifico
     * @param minScore Il punteggio minimo
     * @return Lista degli utenti con punteggio superiore al minimo
     */
    @Query("SELECT u FROM User u WHERE u.totalScore > :minScore")
    List<User> findUsersWithScoreAbove(@Param("minScore") int minScore);

    /**
     * Trova gli utenti che hanno giocato una storia specifica
     * @param storyId L'ID della storia
     * @return Lista degli utenti che hanno giocato la storia
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.games g WHERE g.story.id = :storyId")
    List<User> findUsersWhoPlayedStory(@Param("storyId") Long storyId);
}
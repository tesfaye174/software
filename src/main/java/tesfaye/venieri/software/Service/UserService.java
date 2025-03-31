package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);
    User update(User user);
    void delete(Long userId);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findAll();
    Optional<User> findByEmail(String email);
    void changePassword(Long userId, String oldPassword, String newPassword);
    /**
     * Verifica se esiste già un utente con l'email specificata
     * @param email l'email da verificare
     * @return true se esiste un utente con l'email specificata, false altrimenti
     */
    boolean existsByEmail(String email);
}
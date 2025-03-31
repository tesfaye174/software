package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.model.User;
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
}
package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tesfaye.venieri.software.Model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
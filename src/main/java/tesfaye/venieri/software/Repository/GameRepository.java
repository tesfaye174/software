package tesfaye.venieri.software.Repository;

import tesfaye.venieri.software.Model.Game;
import tesfaye.venieri.software.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByUser(User user);
} 
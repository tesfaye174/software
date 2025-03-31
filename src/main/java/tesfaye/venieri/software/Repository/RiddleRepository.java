package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tesfaye.venieri.software.Model.Riddle;
import tesfaye.venieri.software.Model.Scene;

import java.util.List;

@Repository
public interface RiddleRepository extends JpaRepository<Riddle, Long> {
    List<Riddle> findByScene(Scene scene);
} 
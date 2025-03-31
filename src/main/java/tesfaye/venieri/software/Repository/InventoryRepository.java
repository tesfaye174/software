package tesfaye.venieri.software.Repository;

import tesfaye.venieri.software.Model.Inventory;
import tesfaye.venieri.software.Model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByGame(Game game);
}

package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tesfaye.venieri.software.Model.Item;
import tesfaye.venieri.software.Model.Scene;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByScene(Scene scene);
} 
package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Item;
import tesfaye.venieri.software.Model.Scene;
import java.util.List;

public interface ItemService {
    List<Item> findByScene(Scene scene);
} 
package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Riddle;
import tesfaye.venieri.software.Model.Scene;
import java.util.List;

public interface RiddleService {
    List<Riddle> findByScene(Scene scene);
} 
package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Scene;
import java.util.Optional;

public interface SceneService {
    Optional<Scene> findById(Long id);
    Scene save(Scene scene);
} 
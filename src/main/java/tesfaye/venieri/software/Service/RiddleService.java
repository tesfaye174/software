package tesfaye.venieri.software.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tesfaye.venieri.software.Model.Riddle;
import tesfaye.venieri.software.Model.Scene;
import tesfaye.venieri.software.Repository.RiddleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RiddleService {
    
    private final RiddleRepository riddleRepository;

    @Autowired
    public RiddleService(RiddleRepository riddleRepository) {
        this.riddleRepository = riddleRepository;
    }

    public Optional<Riddle> findById(Long id) {
        return riddleRepository.findById(id);
    }

    public boolean verifySolution(Long riddleId, String answer) {
        Optional<Riddle> riddleOpt = findById(riddleId);
        if (riddleOpt.isPresent()) {
            Riddle riddle = riddleOpt.get();
            return riddle.getSolution().equalsIgnoreCase(answer);
        }
        return false;
    }

    public List<Riddle> findByScene(Scene scene) {
        return riddleRepository.findByScene(scene);
    }
} 
package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Choice;
import java.util.List;
import java.util.Optional;

public interface ChoiceService {
    Choice save(Choice choice);
    Choice update(Choice choice);
    void delete(Long choiceId);
    Optional<Choice> findById(Long id);
    List<Choice> findBySceneId(Long sceneId);
    List<Choice> findAll();
}

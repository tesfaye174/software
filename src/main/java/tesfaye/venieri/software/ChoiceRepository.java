package tesfaye.venieri.software;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findByCurrentStoryId(Long storyId);
}
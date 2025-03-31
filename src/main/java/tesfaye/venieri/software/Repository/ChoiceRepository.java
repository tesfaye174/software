package tesfaye.venieri.software.Repository;

import tesfaye.venieri.software.Model.Choice;
import tesfaye.venieri.software.Model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findByCurrentStory(Story currentStory);
    List<Choice> findByNextStory(Story nextStory);
}
package tesfaye.venieri.software.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tesfaye.venieri.software.Model.Choice;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findByCurrentStoryId(Long storyId);
}
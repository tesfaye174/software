package tesfaye.venieri.software;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final ChoiceRepository choiceRepository;
    
    @Autowired
    public StoryService(StoryRepository storyRepository, ChoiceRepository choiceRepository) {
        this.storyRepository = storyRepository;
        this.choiceRepository = choiceRepository;
    }
    
    public List<StoryDTO> getAllStories() {
        return storyRepository.findAll().stream()
                .map(StoryDTO::new)
                .collect(Collectors.toList());
    }
    
    public StoryDTO getStoryById(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story", "id", id));
        
        StoryDTO storyDTO = new StoryDTO(story);
        
        // Aggiungi le scelte alla storia
        List<ChoiceDTO> choiceDTOs = story.getChoices().stream()
                .map(ChoiceDTO::new)
                .collect(Collectors.toList());
        
        storyDTO.setChoices(choiceDTOs);
        
        return storyDTO;
    }
    
    public StoryDTO createStory(StoryDTO storyDTO) {
        Story story = new Story();
        story.setTitle(storyDTO.getTitle());
        story.setContent(storyDTO.getContent());
        story.setEnding(storyDTO.isEnding());
        
        Story savedStory = storyRepository.save(story);
        
        return new StoryDTO(savedStory);
    }
    
    public StoryDTO updateStory(Long id, StoryDTO storyDTO) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story", "id", id));
        
        story.setTitle(storyDTO.getTitle());
        story.setContent(storyDTO.getContent());
        story.setEnding(storyDTO.isEnding());
        
        Story updatedStory = storyRepository.save(story);
        
        return new StoryDTO(updatedStory);
    }
    
    public void deleteStory(Long id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story", "id", id));
        
        storyRepository.delete(story);
    }
    
    public StoryDTO addChoiceToStory(Long storyId, ChoiceDTO choiceDTO) {
        Story currentStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story", "id", storyId));
        
        Story nextStory = null;
        if (choiceDTO.getNextStoryId() != null) {
            nextStory = storyRepository.findById(choiceDTO.getNextStoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Story", "id", choiceDTO.getNextStoryId()));
        }
        
        Choice choice = new Choice();
        choice.setText(choiceDTO.getText());
        choice.setCurrentStory(currentStory);
        choice.setNextStory(nextStory);
        
        choiceRepository.save(choice);
        
        return getStoryById(storyId);
    }
    
    public void removeChoiceFromStory(Long storyId, Long choiceId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story", "id", storyId));
        
        Choice choice = choiceRepository.findById(choiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Choice", "id", choiceId));
        
        if (!choice.getCurrentStory().getId().equals(storyId)) {
            throw new IllegalArgumentException("La scelta non appartiene alla storia specificata");
        }
        
        choiceRepository.delete(choice);
    }
}
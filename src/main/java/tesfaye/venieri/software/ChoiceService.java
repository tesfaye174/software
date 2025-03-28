package tesfaye.venieri.software;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChoiceService {
    private final ChoiceRepository choiceRepository;
    private final StoryRepository storyRepository;
    
    @Autowired
    public ChoiceService(ChoiceRepository choiceRepository, StoryRepository storyRepository) {
        this.choiceRepository = choiceRepository;
        this.storyRepository = storyRepository;
    }
    
    public ChoiceDTO addChoiceToStory(Long storyId, ChoiceDTO choiceDTO) {
        // Verifica che la storia esista
        Story currentStory = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story", "id", storyId));
        
        // Verifica che la storia di destinazione esista (se specificata)
        Story nextStory = null;
        if (choiceDTO.getNextStoryId() != null) {
            nextStory = storyRepository.findById(choiceDTO.getNextStoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Story", "id", choiceDTO.getNextStoryId()));
        }
        
        // Crea la nuova scelta
        Choice choice = new Choice();
        choice.setText(choiceDTO.getText());
        choice.setCurrentStory(currentStory);
        choice.setNextStory(nextStory);
        
        // Salva la scelta
        Choice savedChoice = choiceRepository.save(choice);
        
        return new ChoiceDTO(savedChoice);
    }
    
    public List<ChoiceDTO> getChoicesForStory(Long storyId) {
        // Verifica che la storia esista
        if (!storyRepository.existsById(storyId)) {
            throw new ResourceNotFoundException("Story", "id", storyId);
        }
        
        // Trova tutte le scelte per la storia
        List<Choice> choices = choiceRepository.findByCurrentStoryId(storyId);
        
        // Converti le scelte in DTO
        return choices.stream()
                .map(ChoiceDTO::new)
                .collect(Collectors.toList());
    }
    
    public ChoiceDTO updateChoice(Long storyId, Long choiceId, ChoiceDTO choiceDTO) {
        // Verifica che la storia esista
        if (!storyRepository.existsById(storyId)) {
            throw new ResourceNotFoundException("Story", "id", storyId);
        }
        
        // Trova la scelta
        Choice choice = choiceRepository.findById(choiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Choice", "id", choiceId));
        
        // Verifica che la scelta appartenga alla storia specificata
        if (!choice.getCurrentStory().getId().equals(storyId)) {
            throw new ResourceNotFoundException("Choice with id " + choiceId + " not found in story with id " + storyId);
        }
        
        // Verifica che la storia di destinazione esista (se specificata)
        Story nextStory = null;
        if (choiceDTO.getNextStoryId() != null) {
            nextStory = storyRepository.findById(choiceDTO.getNextStoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Story", "id", choiceDTO.getNextStoryId()));
        }
        
        // Aggiorna la scelta
        choice.setText(choiceDTO.getText());
        choice.setNextStory(nextStory);
        
        // Salva la scelta aggiornata
        Choice updatedChoice = choiceRepository.save(choice);
        
        return new ChoiceDTO(updatedChoice);
    }
    
    public void deleteChoice(Long storyId, Long choiceId) {
        // Verifica che la storia esista
        if (!storyRepository.existsById(storyId)) {
            throw new ResourceNotFoundException("Story", "id", storyId);
        }
        
        // Trova la scelta
        Choice choice = choiceRepository.findById(choiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Choice", "id", choiceId));
        
        // Verifica che la scelta appartenga alla storia specificata
        if (!choice.getCurrentStory().getId().equals(storyId)) {
            throw new ResourceNotFoundException("Choice with id " + choiceId + " not found in story with id " + storyId);
        }
        
        // Elimina la scelta
        choiceRepository.delete(choice);
    }
}
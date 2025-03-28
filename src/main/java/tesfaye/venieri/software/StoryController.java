package tesfaye.venieri.software;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
    private final StoryRepository storyRepository;
    private final ChoiceRepository choiceRepository;

    @Autowired
    public StoryController(StoryRepository storyRepository, ChoiceRepository choiceRepository) {
        this.storyRepository = storyRepository;
        this.choiceRepository = choiceRepository;
    }

    @GetMapping
    public ResponseEntity<List<Story>> getAllStories() {
        List<Story> stories = storyRepository.findAll();
        return new ResponseEntity<>(stories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoryById(@PathVariable Long id) {
        return storyRepository.findById(id)
                .map(story -> new ResponseEntity<>(story, HttpStatus.OK))
                .orElse(new ResponseEntity<>(createErrorResponse("Story not found"), HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Story> createStory(@RequestBody Story newStory) {
        Story savedStory = storyRepository.save(newStory);
        return new ResponseEntity<>(savedStory, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStory(@PathVariable Long id, @RequestBody Story updatedStory) {
        return storyRepository.findById(id)
                .map(story -> {
                    story.setTitle(updatedStory.getTitle());
                    story.setContent(updatedStory.getContent());
                    story.setEnding(updatedStory.isEnding());
                    Story savedStory = storyRepository.save(story);
                    return new ResponseEntity<>(savedStory, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(createErrorResponse("Story not found"), HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStory(@PathVariable Long id) {
        return storyRepository.findById(id)
                .map(story -> {
                    storyRepository.delete(story);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(createErrorResponse("Story not found"), HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/{storyId}/choices")
    public ResponseEntity<?> addChoiceToStory(@PathVariable Long storyId, @RequestBody Choice choice) {
        return storyRepository.findById(storyId)
                .map(story -> {
                    choice.setCurrentStory(story);
                    Choice savedChoice = choiceRepository.save(choice);
                    return new ResponseEntity<>(savedChoice, HttpStatus.CREATED);
                })
                .orElse(new ResponseEntity<>(createErrorResponse("Story not found"), HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/{storyId}/choices")
    public ResponseEntity<?> getChoicesForStory(@PathVariable Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            return new ResponseEntity<>(createErrorResponse("Story not found"), HttpStatus.NOT_FOUND);
        }
        
        List<Choice> choices = choiceRepository.findByCurrentStoryId(storyId);
        return new ResponseEntity<>(choices, HttpStatus.OK);
    }
    
    @PutMapping("/{storyId}/choices/{choiceId}")
    public ResponseEntity<?> updateChoice(@PathVariable Long storyId, @PathVariable Long choiceId, @RequestBody Choice updatedChoice) {
        if (!storyRepository.existsById(storyId)) {
            return new ResponseEntity<>(createErrorResponse("Current story not found"), HttpStatus.NOT_FOUND);
        }
        
        return choiceRepository.findById(choiceId)
                .map(choice -> {
                    choice.setText(updatedChoice.getText());
                    
                    if (updatedChoice.getNextStory() != null && updatedChoice.getNextStory().getId() != null) {
                        return storyRepository.findById(updatedChoice.getNextStory().getId())
                                .map(nextStory -> {
                                    choice.setNextStory(nextStory);
                                    Choice savedChoice = choiceRepository.save(choice);
                                    return new ResponseEntity<>(savedChoice, HttpStatus.OK);
                                })
                                .orElse(new ResponseEntity<>(createErrorResponse("Next story not found"), HttpStatus.NOT_FOUND));
                    }
                    
                    Choice savedChoice = choiceRepository.save(choice);
                    return new ResponseEntity<>(savedChoice, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(createErrorResponse("Choice not found"), HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{storyId}/choices/{choiceId}")
    public ResponseEntity<?> deleteChoice(@PathVariable Long storyId, @PathVariable Long choiceId) {
        if (!storyRepository.existsById(storyId)) {
            return new ResponseEntity<>(createErrorResponse("Story not found"), HttpStatus.NOT_FOUND);
        }

        return choiceRepository.findById(choiceId)
                .map(choice -> {
                    choiceRepository.delete(choice);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(createErrorResponse("Choice not found"), HttpStatus.NOT_FOUND));
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}

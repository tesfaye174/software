package tesfaye.venieri.software;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stories")
@Validated
public class StoryController {
    private final StoryService storyService;
    private final ChoiceService choiceService;

    @Autowired
    public StoryController(StoryService storyService, ChoiceService choiceService) {
        this.storyService = storyService;
        this.choiceService = choiceService;
    }

    @GetMapping
    public ResponseEntity<List<StoryDTO>> getAllStories() {
        List<StoryDTO> stories = storyService.getAllStories();
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStoryById(@PathVariable Long id) {
        try {
            StoryDTO story = storyService.getStoryById(id);
            return ResponseEntity.ok(story);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<StoryDTO> createStory(@RequestBody @Validated StoryDTO storyDTO) {
        StoryDTO savedStory = storyService.createStory(storyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStory);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStory(@PathVariable Long id, @RequestBody @Validated StoryDTO storyDTO) {
        try {
            StoryDTO updatedStory = storyService.updateStory(id, storyDTO);
            return ResponseEntity.ok(updatedStory);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStory(@PathVariable Long id) {
        try {
            storyService.deleteStory(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/{storyId}/choices")
    public ResponseEntity<?> addChoiceToStory(@PathVariable Long storyId, @RequestBody @Validated ChoiceDTO choiceDTO) {
        try {
            ChoiceDTO savedChoice = choiceService.addChoiceToStory(storyId, choiceDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedChoice);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/{storyId}/choices")
    public ResponseEntity<?> getChoicesForStory(@PathVariable Long storyId) {
        try {
            List<ChoiceDTO> choices = choiceService.getChoicesForStory(storyId);
            return ResponseEntity.ok(choices);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{storyId}/choices/{choiceId}")
    public ResponseEntity<?> updateChoice(@PathVariable Long storyId, 
                                        @PathVariable Long choiceId, 
                                        @RequestBody @Validated ChoiceDTO choiceDTO) {
        try {
            ChoiceDTO updatedChoice = choiceService.updateChoice(storyId, choiceId, choiceDTO);
            return ResponseEntity.ok(updatedChoice);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{storyId}/choices/{choiceId}")
    public ResponseEntity<?> deleteChoice(@PathVariable Long storyId, @PathVariable Long choiceId) {
        try {
            choiceService.deleteChoice(storyId, choiceId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}

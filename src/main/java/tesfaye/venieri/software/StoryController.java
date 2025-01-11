package tesfaye.venieri.software;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StoryController {
    private final StoryRepository storyRepository;

    public StoryController(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @GetMapping("/stories")
    public List<Story> getAllStories() {
        return storyRepository.findAll();
    }

    @GetMapping("/story/{id}")
    public Story getStoryById(@PathVariable Long id) {
        return storyRepository.findById(id).orElseThrow(()->new RuntimeException("Story not found"));
    }

    @PostMapping("/stories")
    public void newStory(@RequestBody Story newStory) {
        storyRepository.save(newStory);
    }
}

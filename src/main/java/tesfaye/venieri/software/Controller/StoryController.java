package tesfaye.venieri.software.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Repository.StoryRepository;
import tesfaye.venieri.software.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing story operations
 * Extends the generic ModelRepositoryController for standard CRUD operations
 */
@RestController
@RequestMapping("/api/stories")
public class StoryController extends ModelRepositoryController<Story, StoryRepository> {

    private final UserRepository userRepository;

    @Autowired
    public StoryController(StoryRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Story> getAll() {
        return repository.findAll();
    }

    @Override
    public ResponseEntity<Story> getById(@PathVariable Long id) {
        Story story = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));
        return ResponseEntity.ok(story);
    }

    @Override
    public Story create(@RequestBody Story story) {
        // Set creation date if not already set
        if (story.getCreationDate() == null) {
            story.setCreationDate(LocalDateTime.now());
        }
        return repository.save(story);
    }

    @Override
    public ResponseEntity<Story> update(@PathVariable Long id, @RequestBody Story storyDetails) {
        Story story = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + id));

        story.setTitle(storyDetails.getTitle());
        story.setDescription(storyDetails.getDescription());
        // Don't update creation date
        // Don't update author

        Story updatedStory = repository.save(story);
        return ResponseEntity.ok(updatedStory);
    }

    /**
     * Get all stories by a specific author
     * 
     * @param authorId The author's ID
     * @return List of stories by the author
     */
    @GetMapping("/author/{authorId}")
    public List<Story> getByAuthor(@PathVariable Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        return repository.findByAuthorId(author.getId());
    }

    /**
     * Create a new story with the specified author
     * 
     * @param authorId The author's ID
     * @param storia The story to create
     * @return The created story
     */
    @PostMapping("/author/{authorId}")
    public Story createWithAuthor(@PathVariable Long authorId, @RequestBody Story story) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + authorId));
        
        story.setAuthor(author);
        story.setCreationDate(LocalDateTime.now());
        
        return repository.save(story);
    }
}
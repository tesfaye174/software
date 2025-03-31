package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoryService {

    private final StoryRepository storyRepository;

    @Autowired
    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public List<Story> findAll() {
        return storyRepository.findAll();
    }

    public Optional<Story> findById(Long id) {
        return storyRepository.findById(id);
    }

    public List<Story> findByUserId(Long userId) {
        return storyRepository.findByUserId(userId);
    }

    public List<Story> searchByTitle(String title) {
        return storyRepository.findByTitleContaining(title);
    }

    public Story save(Story story) {
        return storyRepository.save(story);
    }

    public void delete(Story story) {
        storyRepository.delete(story);
    }

    public void deleteById(Long id) {
        storyRepository.deleteById(id);
    }
}
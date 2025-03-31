package tesfaye.venieri.software.Controller;

import tesfaye.venieri.software.Model.*;
import tesfaye.venieri.software.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/scenes")
public class SceneController {

    private final SceneService sceneService;
    private final StoryService storyService;
    private final ChoiceService choiceService;
    private final RiddleService riddleService;
    private final ItemService itemService;

    @Autowired
    public SceneController(SceneService sceneService, StoryService storyService,
                          ChoiceService choiceService, RiddleService riddleService,
                          ItemService itemService) {
        this.sceneService = sceneService;
        this.storyService = storyService;
        this.choiceService = choiceService;
        this.riddleService = riddleService;
        this.itemService = itemService;
    }

    @GetMapping("/create/{storyId}")
    public String showCreationForm(@PathVariable Long storyId, Model model) {
        Optional<Story> storyOpt = storyService.findById(storyId);
        if (storyOpt.isPresent()) {
            Story story = storyOpt.get();
            
            // Verify that current user is the author of the story
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!story.getAuthor().getUsername().equals(username)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to create scenes for this story");
            }
            
            model.addAttribute("story", story);
            model.addAttribute("scene", new Scene());
            return "scenes/create";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Story not found");
    }

    @PostMapping("/create/{storyId}")
    public String createScene(@PathVariable Long storyId, @Valid @ModelAttribute("scene") Scene scene, 
                            BindingResult result, Model model) {
        Optional<Story> storyOpt = storyService.findById(storyId);
        if (!storyOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Story not found");
        }

        Story story = storyOpt.get();
        
        // Verify that current user is the author of the story
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (!story.getAuthor().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to create scenes for this story");
        }

        if (result.hasErrors()) {
            model.addAttribute("story", story);
            return "scenes/create";
        }
        
        scene.setStory(story);
        Scene savedScene = sceneService.save(scene);
        return "redirect:/scenes/edit/" + savedScene.getId();
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Scene> sceneOpt = sceneService.findById(id);
        if (!sceneOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scene not found");
        }

        Scene scene = sceneOpt.get();
        
        // Verify that current user is the author of the story
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (!scene.getStory().getAuthor().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this scene");
        }
        
        model.addAttribute("scene", scene);
        model.addAttribute("choices", choiceService.findByScene(scene));
        model.addAttribute("riddles", riddleService.findByScene(scene));
        model.addAttribute("items", itemService.findByScene(scene));
        return "scenes/edit";
    }

    @PostMapping("/edit/{id}")
    public String editScene(@PathVariable Long id, @Valid @ModelAttribute("scene") Scene scene, 
                          BindingResult result, Model model) {
        Optional<Scene> existingSceneOpt = sceneService.findById(id);
        if (!existingSceneOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scene not found");
        }

        Scene existingScene = existingSceneOpt.get();
        
        // Verify that current user is the author of the story
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        if (!existingScene.getStory().getAuthor().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this scene");
        }

        if (result.hasErrors()) {
            model.addAttribute("choices", choiceService.findByScene(existingScene));
            model.addAttribute("riddles", riddleService.findByScene(existingScene));
            model.addAttribute("items", itemService.findByScene(existingScene));
            return "scenes/edit";
        }
        
        // Update only modifiable fields
        existingScene.setText(scene.getText());
        existingScene.setFinal(scene.isFinal());
        
        sceneService.save(existingScene);
        return "redirect:/scenes/edit/" + id;
    }
}

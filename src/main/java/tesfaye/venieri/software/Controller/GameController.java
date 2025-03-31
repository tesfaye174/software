package tesfaye.venieri.software.Controller;

import tesfaye.venieri.software.model.*;
import tesfaye.venieri.software.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final StoryService storyService;
    private final ScenarioService scenarioService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final CollectedItemService collectedItemService;
    private final ItemService itemService;
    private final ChoiceService choiceService;
    private final RiddleService riddleService;

    @Autowired
    public GameController(GameService gameService, StoryService storyService,
                         ScenarioServiceImpl scenarioService, UserService userService,
                         CollectedItemService collectedItemService,
                         ItemService itemService, ChoiceService choiceService,
                         RiddleService riddleService) {
        this.gameService = gameService;
        this.storyService = storyService;
        this.scenarioService = scenarioService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.collectedItemService = collectedItemService;
        this.itemService = itemService;
        this.choiceService = choiceService;
        this.riddleService = riddleService;
    }

    @GetMapping("/start/{storyId}")
    public String startGame(@PathVariable Long storyId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Story story = storyService.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        Game game = gameService.startNewGame(user, story);
        return "redirect:/games/play/" + game.getId();
    }

    @GetMapping("/play/{gameId}")
    public String playGame(@PathVariable Long gameId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (!game.getPlayer().getUsername().equals(username)) {
            throw new IllegalStateException("You are not authorized to access this game");
        }

        Scenario currentScenario = game.getCurrentScenario();
        List<Choice> choices = choiceService.findByScenario(currentScenario);
        Optional<Riddle> riddle = riddleService.findByScenario(currentScenario);
        List<Item> items = itemService.findByScenario(currentScenario);
        List<CollectedItem> inventory = collectedItemService.findByGame(game);

        model.addAttribute("game", game);
        model.addAttribute("scenario", currentScenario);
        model.addAttribute("choices", choices);
        model.addAttribute("riddle", riddle.orElse(null));
        model.addAttribute("items", items);
        model.addAttribute("inventory", inventory);

        return "games/play";
    }

    @PostMapping("/choice/{gameId}/{choiceId}")
    public String makeChoice(@PathVariable Long gameId, @PathVariable Long choiceId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (!game.getPlayer().getUsername().equals(username)) {
            throw new IllegalStateException("You are not authorized to make this choice");
        }

        Choice choice = choiceService.findById(choiceId)
                .orElseThrow(() -> new IllegalArgumentException("Choice not found"));

        gameService.makeChoice(game, choice);
        return "redirect:/games/play/" + gameId;
    }

    @PostMapping("/riddle/{gameId}")
    public String solveRiddle(@PathVariable Long gameId, @RequestParam String answer, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (!game.getPlayer().getUsername().equals(username)) {
            throw new IllegalStateException("You are not authorized to solve this riddle");
        }

        Scenario currentScenario = game.getCurrentScenario();
        Riddle riddle = riddleService.findByScenario(currentScenario)
                .orElseThrow(() -> new IllegalStateException("No riddle found in current scenario"));

        boolean solved = gameService.solveRiddle(game, riddle, answer);
        if (!solved) {
            model.addAttribute("riddleError", "Incorrect answer. Try again!");
        }

        return "redirect:/games/play/" + gameId;
    }

    @PostMapping("/collect/{gameId}/{itemId}")
    public String collectItem(@PathVariable Long gameId, @PathVariable Long itemId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (!game.getPlayer().getUsername().equals(username)) {
            throw new IllegalStateException("You are not authorized to collect this item");
        }

        Item item = itemService.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        gameService.collectItem(game, item);
        return "redirect:/games/play/" + gameId;
    }

    @GetMapping("/inventory/{gameId}")
    public String viewInventory(@PathVariable Long gameId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Game game = gameService.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (!game.getPlayer().getUsername().equals(username)) {
            throw new IllegalStateException("You are not authorized to view this inventory");
        }

        List<CollectedItem> inventory = collectedItemService.findByGame(game);
        model.addAttribute("inventory", inventory);
        model.addAttribute("game", game);

        return "games/inventory";
    }
}

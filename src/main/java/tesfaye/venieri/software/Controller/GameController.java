package tesfaye.venieri.software.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tesfaye.venieri.software.Model.*;
import tesfaye.venieri.software.Service.*;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import tesfaye.venieri.software.Exception.GameException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

// Fix package imports at top
import tesfaye.venieri.software.Model.*;
import tesfaye.venieri.software.Service.*;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    
    private final GameService gameService;
    private final StoryService storyService;
    private final SceneService sceneService;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final CollectedItemService collectedItemService;
    private final ItemService itemService;
    private final ChoiceService choiceService;
    private final RiddleService riddleService;

    @Autowired
    public GameController(GameService gameService, StoryService storyService,
                         SceneService sceneService, UserService userService,
                         InventoryService inventoryService, CollectedItemService collectedItemService,
                         ItemService itemService, ChoiceService choiceService,
                         RiddleService riddleService) {
        this.gameService = gameService;
        this.storyService = storyService;
        this.sceneService = sceneService;
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.collectedItemService = collectedItemService;
        this.itemService = itemService;
        this.choiceService = choiceService;
        this.riddleService = riddleService;
    }

    @PostMapping("/start/{storyId}")
    public ResponseEntity<?> startGame(@PathVariable Long storyId) {
        try {
            Optional<Story> storyOpt = storyService.findById(storyId);
            if (storyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = userOpt.get();
            if (!user.isPremium()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Premium subscription required");
            }

            Story story = storyOpt.get();
            Scene firstScene = story.getStartingScene();
            if (firstScene == null) {
                return ResponseEntity.badRequest().body("Story has no starting scene");
            }

            Game game = gameService.startNewGame(user, story, firstScene);
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error starting game: " + e.getMessage());
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGame(@PathVariable Long gameId) {
        try {
            Optional<Game> gameOpt = gameService.findById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = gameOpt.get();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!game.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(game);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving game: " + e.getMessage());
        }
    }

    @PostMapping("/{gameId}/choice/{choiceId}")
    public ResponseEntity<?> makeChoice(@PathVariable Long gameId, @PathVariable Long choiceId) {
        try {
            Optional<Game> gameOpt = gameService.findById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = gameOpt.get();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!game.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Game updatedGame = gameService.makeChoice(gameId, choiceId);
            return ResponseEntity.ok(updatedGame);
        } catch (GameException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error making choice: " + e.getMessage());
        }
    }

    @PostMapping("/{gameId}/riddle/{riddleId}")
    public ResponseEntity<?> solveRiddle(@PathVariable Long gameId, @PathVariable Long riddleId,
                                       @RequestParam String answer) {
        try {
            Optional<Game> gameOpt = gameService.findById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = gameOpt.get();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!game.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Game updatedGame = gameService.solveRiddle(gameId, riddleId, answer);
            return ResponseEntity.ok(updatedGame);
        } catch (GameException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error solving riddle: " + e.getMessage());
        }
    }

    @PostMapping("/{gameId}/collect/{itemId}")
    public ResponseEntity<?> collectItem(@PathVariable Long gameId, @PathVariable Long itemId) {
        try {
            Optional<Game> gameOpt = gameService.findById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = gameOpt.get();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!game.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Optional<Item> itemOpt = itemService.findById(itemId);
            if (itemOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Item item = itemOpt.get();
            if (!item.getScene().getId().equals(game.getCurrentScene().getId())) {
                return ResponseEntity.badRequest().body("Item not available in current scene");
            }

            collectedItemService.addItemToInventory(game.getInventory(), item);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error collecting item: " + e.getMessage());
        }
    }

    @PostMapping("/{gameId}/save")
    public ResponseEntity<?> saveGame(@PathVariable Long gameId) {
        try {
            Optional<Game> gameOpt = gameService.findById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = gameOpt.get();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!game.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            gameService.saveGameState(gameId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error saving game: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listGames() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = userOpt.get();
            List<Game> games = gameService.findByUser(user);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error listing games: " + e.getMessage());
        }
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<?> deleteGame(@PathVariable Long gameId) {
        try {
            Optional<Game> gameOpt = gameService.findById(gameId);
            if (gameOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Game game = gameOpt.get();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!game.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            gameService.deleteById(gameId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting game: " + e.getMessage());
        }
    }
}

package tesfaye.venieri.software.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import tesfaye.venieri.software.Exception.GameSessionException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

// Fix package imports at top
import tesfaye.venieri.software.Model.*;
import tesfaye.venieri.software.Service.*;

@Controller
@RequestMapping("/games")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    
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
                         ScenarioService scenarioService, UserService userService,
                         InventoryService inventoryService,
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
        logger.info("Avvio nuovo gioco per la storia ID: {}", storyId);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            Game game = gameService.startNewGame(storyId, username);
            model.addAttribute("game", game);
            model.addAttribute("currentScene", game.getCurrentScene());
            model.addAttribute("inventory", game.getInventory());
            return "games/play";
        } catch (ResourceNotFoundException e) {
            logger.error("Storia non trovata: {}", storyId);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Storia non trovata");
        } catch (Exception e) {
            logger.error("Errore durante l'avvio del gioco: {}", e.getMessage());
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante l'avvio del gioco");
        }
    }

    @GetMapping("/play/{gameId}")
    public String playGame(@PathVariable Long gameId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<Game> game = gameService.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        if (!game.getPlayer().getUsername().equals(username)) {
            throw new IllegalStateException("You are not authorized to access this game");
        }

        Scenario currentScenario = game.getCurrentScenario();
        List<Choice> choices = choiceService.findByScenario(currentScenario);
        
        // Add error handling for riddle
        Riddle riddle = riddleService.findByScenario(currentScenario)
                .orElse(null);

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
    public String makeChoice(@PathVariable Long gameId, @PathVariable Long choiceId, Model model) {
        logger.info("Elaborazione scelta ID: {} per il gioco ID: {}", choiceId, gameId);
        try {
            Game game = gameService.findById(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException("Gioco non trovato"));
            
            Choice choice = choiceService.findById(choiceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Scelta non trovata"));
            
            if (!choice.isAvailable(game.getInventory())) {
                logger.warn("Scelta non disponibile per il gioco ID: {}", gameId);
                throw new GameSessionException("Questa scelta non è disponibile");
            }
            
            choice.applyChoice(game.getInventory());
            game.setCurrentScenario(choice.getDestinationScenario());
            gameService.saveGame(game);
            
            model.addAttribute("game", game);
            model.addAttribute("currentScene", game.getCurrentScenario());
            model.addAttribute("inventory", game.getInventory());
            return "redirect:/games/play/" + gameId;
        } catch (ResourceNotFoundException e) {
            logger.error("Risorsa non trovata: {}", e.getMessage());
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, e.getMessage());
        } catch (GameSessionException e) {
            logger.error("Errore nella sessione di gioco: {}", e.getMessage());
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Errore durante l'elaborazione della scelta: {}", e.getMessage());
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante l'elaborazione della scelta");
        }
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
        logger.info("Visualizzazione inventario per il gioco ID: {}", gameId);
        try {
            Game game = gameService.findById(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException("Gioco non trovato"));
            
            model.addAttribute("game", game);
            model.addAttribute("inventory", game.getInventory());
            return "games/inventory";
        } catch (ResourceNotFoundException e) {
            logger.error("Gioco non trovato: {}", gameId);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Gioco non trovato");
        } catch (Exception e) {
            logger.error("Errore durante la visualizzazione dell'inventario: {}", e.getMessage());
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la visualizzazione dell'inventario");
        }
    }

    @PostMapping("/{gameId}/end")
    public String endGame(@PathVariable Long gameId) {
        logger.info("Fine del gioco ID: {}", gameId);
        try {
            gameService.endGame(gameId);
            return "redirect:/stories";
        } catch (ResourceNotFoundException e) {
            logger.error("Gioco non trovato: {}", gameId);
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Gioco non trovato");
        } catch (Exception e) {
            logger.error("Errore durante la fine del gioco: {}", e.getMessage());
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Errore durante la fine del gioco");
        }
    }
}

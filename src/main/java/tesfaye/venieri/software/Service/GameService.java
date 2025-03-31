package tesfaye.venieri.software.Service;

import tesfaye.venieri.software.Model.Game;
import tesfaye.venieri.software.Model.User;
import tesfaye.venieri.software.Model.Story;
import tesfaye.venieri.software.Model.Scene;
import tesfaye.venieri.software.Model.CollectedItem;
import tesfaye.venieri.software.Repository.GameRepository;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;
import tesfaye.venieri.software.Exception.GameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

@Service
public class GameService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private final GameRepository gameRepository;
    private final InventoryService inventoryService;
    private final StoryService storyService;
    private final UserService userService;

    @Autowired
    public GameService(GameRepository gameRepository, 
                      InventoryService inventoryService,
                      StoryService storyService,
                      UserService userService) {
        this.gameRepository = gameRepository;
        this.inventoryService = inventoryService;
        this.storyService = storyService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<Game> findAll() {
        try {
            logOperationStart("findAll", "Recupero di tutti i giochi");
            List<Game> games = gameRepository.findAll();
            logOperationComplete("findAll", "Recuperati " + games.size() + " giochi");
            return games;
        } catch (Exception e) {
            handleException(e, "Errore durante il recupero di tutti i giochi");
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Game> findById(Long id) {
        try {
            logOperationStart("findById", "Ricerca gioco con ID: " + id);
            Optional<Game> game = gameRepository.findById(id);
            logOperationComplete("findById", game.isPresent() ? "Gioco trovato" : "Gioco non trovato");
            return game;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca del gioco con ID: " + id);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public List<Game> findByUser(User user) {
        try {
            logOperationStart("findByUser", "Ricerca giochi per utente: " + user.getUsername());
            List<Game> games = gameRepository.findByUser(user);
            logOperationComplete("findByUser", "Trovati " + games.size() + " giochi per l'utente");
            return games;
        } catch (Exception e) {
            handleException(e, "Errore durante la ricerca dei giochi per l'utente: " + user.getUsername());
            return Collections.emptyList();
        }
    }

    @Transactional
    public Game startNewGame(Long storyId, Long userId) {
        try {
            logOperationStart("startNewGame", "Avvio nuovo gioco per la storia: " + storyId + " e utente: " + userId);
            
            // Verifica esistenza storia e utente
            Story story = storyService.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Storia non trovata con ID: " + storyId));
            
            User user = userService.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

            // Crea nuovo gioco
            Game game = new Game();
            game.setUser(user);
            game.setStory(story);
            game.setCurrentScene(story.getFirstScene());
            game.setStartTime(LocalDateTime.now());
            game.setStatus(Game.GameStatus.IN_PROGRESS);

            // Crea inventario vuoto
            game.setInventory(inventoryService.createEmptyInventory(game));

            // Salva il gioco
            Game savedGame = gameRepository.save(game);
            logOperationComplete("startNewGame", "Nuovo gioco creato con successo con ID: " + savedGame.getId());
            return savedGame;
        } catch (Exception e) {
            handleException(e, "Errore durante l'avvio del nuovo gioco");
            throw new GameException("Impossibile avviare il nuovo gioco: " + e.getMessage());
        }
    }

    @Transactional
    public Game makeChoice(Long gameId, Long choiceId) {
        try {
            logOperationStart("makeChoice", "Esecuzione scelta " + choiceId + " per il gioco: " + gameId);
            
            Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Gioco non trovato con ID: " + gameId));

            if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
                throw new GameException("Il gioco non è in corso");
            }

            // Verifica che la scelta sia disponibile nella scena corrente
            Scene currentScene = game.getCurrentScene();
            if (!currentScene.getChoices().stream().anyMatch(c -> c.getId().equals(choiceId))) {
                throw new GameException("Scelta non disponibile nella scena corrente");
            }

            // Aggiorna la scena corrente
            currentScene.getChoices().stream()
                .filter(c -> c.getId().equals(choiceId))
                .findFirst()
                .ifPresent(choice -> {
                    game.setCurrentScene(choice.getNextScene());
                    if (choice.getGivesItem() && choice.getItemToGive() != null) {
                        CollectedItem collectedItem = new CollectedItem();
                        collectedItem.setItem(choice.getItemToGive());
                        collectedItem.setInventory(game.getInventory());
                        game.getInventory().getCollectedItems().add(collectedItem);
                    }
                });

            Game updatedGame = gameRepository.save(game);
            logOperationComplete("makeChoice", "Scelta eseguita con successo");
            return updatedGame;
        } catch (Exception e) {
            handleException(e, "Errore durante l'esecuzione della scelta");
            throw new GameException("Impossibile eseguire la scelta: " + e.getMessage());
        }
    }

    @Transactional
    public Game solveRiddle(Long gameId, Long riddleId, String answer) {
        try {
            logOperationStart("solveRiddle", "Risoluzione enigma " + riddleId + " per il gioco: " + gameId);
            
            Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Gioco non trovato con ID: " + gameId));

            if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
                throw new GameException("Il gioco non è in corso");
            }

            // Verifica che l'enigma sia nella scena corrente
            Scene currentScene = game.getCurrentScene();
            if (!currentScene.getRiddles().stream().anyMatch(r -> r.getId().equals(riddleId))) {
                throw new GameException("Enigma non presente nella scena corrente");
            }

            // Verifica la risposta
            currentScene.getRiddles().stream()
                .filter(r -> r.getId().equals(riddleId))
                .findFirst()
                .ifPresent(riddle -> {
                    if (riddle.checkAnswer(answer)) {
                        riddle.solve();
                    } else {
                        throw new GameException("Risposta non corretta");
                    }
                });

            Game updatedGame = gameRepository.save(game);
            logOperationComplete("solveRiddle", "Enigma risolto con successo");
            return updatedGame;
        } catch (Exception e) {
            handleException(e, "Errore durante la risoluzione dell'enigma");
            throw new GameException("Impossibile risolvere l'enigma: " + e.getMessage());
        }
    }

    @Transactional
    public Game endGame(Long gameId) {
        try {
            logOperationStart("endGame", "Terminazione gioco: " + gameId);
            
            Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Gioco non trovato con ID: " + gameId));

            if (game.getStatus() == Game.GameStatus.COMPLETED) {
                throw new GameException("Il gioco è già stato completato");
            }

            game.setStatus(Game.GameStatus.COMPLETED);
            game.setEndTime(LocalDateTime.now());

            Game updatedGame = gameRepository.save(game);
            logOperationComplete("endGame", "Gioco terminato con successo");
            return updatedGame;
        } catch (Exception e) {
            handleException(e, "Errore durante la terminazione del gioco");
            throw new GameException("Impossibile terminare il gioco: " + e.getMessage());
        }
    }

    @Transactional
    public void delete(Long gameId) {
        try {
            logOperationStart("delete", "Eliminazione gioco: " + gameId);
            
            if (!gameRepository.existsById(gameId)) {
                throw new ResourceNotFoundException("Gioco non trovato con ID: " + gameId);
            }

            gameRepository.deleteById(gameId);
            logOperationComplete("delete", "Gioco eliminato con successo");
        } catch (Exception e) {
            handleException(e, "Errore durante l'eliminazione del gioco");
            throw new GameException("Impossibile eliminare il gioco: " + e.getMessage());
        }
    }
}

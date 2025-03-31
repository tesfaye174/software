package it.storieinterattive.controller;

import it.storieinterattive.model.*;
import it.storieinterattive.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/partite")
public class PartitaController {

    private final PartitaService partitaService;
    private final StoriaService storiaService;
    private final ScenarioService scenarioService;
    private final UtenteService utenteService;
    private final InventarioService inventarioService;
    private final OggettoRaccoltoService oggettoRaccoltoService;
    private final OggettoService oggettoService;
    private final SceltaService sceltaService;
    private final IndovinelloService indovinelloService;

    @Autowired
    public PartitaController(PartitaService partitaService, StoriaService storiaService,
                            ScenarioService scenarioService, UtenteService utenteService,
                            InventarioService inventarioService, OggettoRaccoltoService oggettoRaccoltoService,
                            OggettoService oggettoService, SceltaService sceltaService,
                            IndovinelloService indovinelloService) {
        this.partitaService = partitaService;
        this.storiaService = storiaService;
        this.scenarioService = scenarioService;
        this.utenteService = utenteService;
        this.inventarioService = inventarioService;
        this.oggettoRaccoltoService = oggettoRaccoltoService;
        this.oggettoService = oggettoService;
        this.sceltaService = sceltaService;
        this.indovinelloService = indovinelloService;
    }

    @GetMapping("/inizia/{storiaId}")
    public String iniziaPartita(@PathVariable Long storiaId) {
        Optional<Storia> storiaOpt = storiaService.findById(storiaId);
        if (storiaOpt.isPresent()) {
            Storia storia = storiaOpt.get();
            
            // Verifica che l'utente sia premium
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<Utente> utenteOpt = utenteService.findByUsername(username);
            
            if (utenteOpt.isPresent()) {
                Utente utente = utenteOpt.get();
                if (!utente.isPremium()) {
                    return "redirect:/premium";
                }
                
                // Trova il primo scenario della storia
                Optional<Scenario> primoScenarioOpt = scenarioService.findFirstByStoria(storia);
                if (primoScenarioOpt.isPresent()) {
                    Scenario primoScenario = primoScenarioOpt.get();
                    
                    // Crea una nuova partita
                    Partita partita = partitaService.iniziaNuovaPartita(utente, storia, primoScenario);
                    
                    return "redirect:/partite/gioca/" + partita.getId();
                }
            }
        }
        
        return "redirect:/storie/catalogo";
    }

    @GetMapping("/gioca/{partitaId}")
    public String giocaPartita(@PathVariable Long partitaId, Model model) {
        Optional<Partita> partitaOpt = partitaService.findById(partitaId);
        if (partitaOpt.isPresent()) {
            Partita partita = partitaOpt.get();
            
            // Verifica che l'utente corrente sia il giocatore della partita
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!partita.getUtente().getUsername().equals(username)) {
                return "redirect:/storie/catalogo";
            }
            
            model.addAttribute("partita", partita);
            model.addAttribute("scenario", partita.getScenarioCorrente());
            
            // Recupera le scelte disponibili per lo scenario corrente
            List<Scelta> scelte = sceltaService.findByScenario(partita.getScenarioCorrente());
            model.addAttribute("scelte", scelte);
            
            // Recupera gli indovinelli per lo scenario corrente
            List<Indovinello> indovinelli = indovinelloService.findByScenario(partita.getScenarioCorrente());
            model.addAttribute("indovinelli", indovinelli);
            
            // Recupera gli oggetti raccoglibili nello scenario corrente
            List<Oggetto> oggetti = oggettoService.findByScenario(partita.getScenarioCorrente());
            model.addAttribute("oggetti", oggetti);
            
            // Recupera l'inventario del giocatore
            Optional<Inventario> inventarioOpt = inventarioService.findByPartita(partita);
            if (inventarioOpt.isPresent()) {
                Inventario inventario = inventarioOpt.get();
                List<OggettoRaccolto> oggettiRaccolti = oggettoRaccoltoService.findByInventario(inventario);
                model.addAttribute("inventario", inventario);
                model.addAttribute("oggettiRaccolti", oggettiRaccolti);
            }
            
            return "partite/gioca";
        }
        
        return "redirect:/storie/catalogo";
    }

    @PostMapping("/scelta/{partitaId}/{sceltaId}")
    public String effettuaScelta(@PathVariable Long partitaId, @PathVariable Long sceltaId) {
        Optional<Partita> partitaOpt = partitaService.findById(partitaId);
        Optional<Scelta> sceltaOpt = sceltaService.findById(sceltaId);
        
        if (partitaOpt.isPresent() && sceltaOpt.isPresent()) {
            Partita partita = partitaOpt.get();
            Scelta scelta = sceltaOpt.get();
            
            // Verifica che l'utente corrente sia il giocatore della partita
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!partita.getUtente().getUsername().equals(username)) {
                return "redirect:/storie/catalogo";
            }
            
            // Verifica che la scelta appartenga allo scenario corrente
            if (!scelta.getScenario().getId().equals(partita.getScenarioCorrente().getId())) {
                return "redirect:/partite/gioca/" + partitaId;
            }
            
            // Verifica se la scelta richiede un oggetto
            if (scelta.getOggettoRichiesto() != null) {
                Optional<Inventario> inventarioOpt = inventarioService.findByPartita(partita);
                if (inventarioOpt.isPresent()) {
                    Inventario inventario = inventarioOpt.get();
                    boolean hasOggetto = oggettoRaccoltoService.existsByInventarioAndOggetto(inventario, scelta.getOggettoRichiesto());
                    if (!hasOggetto) {
                        // L'utente non ha l'oggetto richiesto
                        return "redirect:/partite/gioca/" + partitaId + "?error=oggetto";
                    }
                }
            }
            
            // Aggiorna lo scenario corrente
            partita.setScenarioCorrente(scelta.getScenarioDestinazione());
            partitaService.save(partita);
            
            return "redirect:/partite/gioca/" + partitaId;
        }
        
        return "redirect:/storie/catalogo";
    }

    @PostMapping("/indovinello/{partitaId}/{indovinelloId}")
    public String risolviIndovinello(@PathVariable Long partitaId, @PathVariable Long indovinelloId,
                                    @RequestParam String risposta) {
        Optional<Partita> partitaOpt = partitaService.findById(partitaId);
        Optional<Indovinello> indovinelloOpt = indovinelloService.findById(indovinelloId);
        
        if (partitaOpt.isPresent() && indovinelloOpt.isPresent()) {
            Partita partita = partitaOpt.get();
            Indovinello indovinello = indovinelloOpt.get();
            
            // Verifica che l'utente corrente sia il giocatore della partita
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!partita.getUtente().getUsername().equals(username)) {
                return "redirect:/storie/catalogo";
            }
            
            // Verifica che l'indovinello appartenga allo scenario corrente
            if (!indovinello.getScenario().getId().equals(partita.getScenarioCorrente().getId())) {
                return "redirect:/partite/gioca/" + partitaId;
            }
            
            // Verifica la risposta
            if (indovinelloService.verificaSoluzione(indovinelloId, risposta)) {
                // Risposta corretta, aggiorna lo scenario corrente
                partita.setScenarioCorrente(indovinello.getScenarioDestinazione());
                partitaService.save(partita);
                return "redirect:/partite/gioca/" + partitaId;
            } else {
                // Risposta errata
                return "redirect:/partite/gioca/" + partitaId + "?error=indovinello";
            }
        }
        
        return "redirect:/storie/catalogo";
    }

    @PostMapping("/raccogli/{partitaId}/{oggettoId}")
    public String raccogliOggetto(@PathVariable Long partitaId, @PathVariable Long oggettoId) {
        Optional<Partita> partitaOpt = partitaService.findById(partitaId);
        Optional<Oggetto> oggettoOpt = oggettoService.findById(oggettoId);
        
        if (partitaOpt.isPresent() && oggettoOpt.isPresent()) {
            Partita partita = partitaOpt.get();
            Oggetto oggetto = oggettoOpt.get();
            
            // Verifica che l'utente corrente sia il giocatore della partita
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!partita.getUtente().getUsername().equals(username)) {
                return "redirect:/storie/catalogo";
            }
            
            // Verifica che l'oggetto appartenga allo scenario corrente
            if (!oggetto.getScenario().getId().equals(partita.getScenarioCorrente().getId())) {
                return "redirect:/partite/gioca/" + partitaId;
            }
            
            // Aggiungi l'oggetto all'inventario
            Optional<Inventario> inventarioOpt = inventarioService.findByPartita(partita);
            if (inventarioOpt.isPresent()) {
                Inventario inventario = inventarioOpt.get();
                oggettoRaccoltoService.aggiungiOggettoAInventario(inventario, oggetto);
            }
            
            return "redirect:/partite/gioca/" + partitaId;
        }
        
        return "redirect:/storie/catalogo";
    }

    @GetMapping("/salva/{partitaId}")
    public String salvaPartita(@PathVariable Long partitaId) {
        Optional<Partita> partitaOpt = partitaService.findById(partitaId);
        if (partitaOpt.isPresent()) {
            Partita partita = partitaOpt.get();
            
            // Verifica che l'utente corrente sia il giocatore della partita
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!partita.getUtente().getUsername().equals(username)) {
                return "redirect:/storie/catalogo";
            }
            
            partitaService.salvaStatoPartita(partitaId);
            return "redirect:/partite/lista";
        }
        
        return "redirect:/storie/catalogo";
    }

    @GetMapping("/lista")
    public String listaPartite(Model model) {
        // Ottieni l'utente corrente
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Utente> utenteOpt = utenteService.findByUsername(username);
        
        if (utenteOpt.isPresent()) {
            Utente utente = utenteOpt.get();
            List<Partita> partite = partitaService.findByUtente(utente);
            model.addAttribute("partite", partite);
            return "partite/lista";
        }
        
        return "redirect:/storie/catalogo";
    }

    @GetMapping("/cancella/{partitaId}")
    public String cancellaPartita(@PathVariable Long partitaId) {
        Optional<Partita> partitaOpt = partitaService.findById(partitaId);
        if (partitaOpt.isPresent()) {
            Partita partita = partitaOpt.get();
            
            // Verifica che l'utente corrente sia il giocatore della partita
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            if (!partita.getUtente().getUsername().equals(username)) {
                return "redirect:/storie/catalogo";
            }
            
            partitaService.deleteById(partitaId);
            return "redirect:/partite/lista";
        }
        
        return "redirect:/storie/catalogo";
    }
}

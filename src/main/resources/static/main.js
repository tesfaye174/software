// Gestione degli stage del gioco
const stages = {
    fase1: {
        text: "Sei davanti a una foresta oscura. Vuoi entrare o tornare indietro?",
        choices: [
            { text: "Entra nella foresta", next: "fase3" },
            { text: "Torna indietro", next: "fine" }
        ]
    },
    fase2: {
        text: "Decidi di riposarti in una locanda. Vuoi esplorare la città o continuare a riposare?",
        choices: [
            { text: "Esplora la città", next: "fase4" },
            { text: "Continua a riposare", next: "fine" }
        ]
    },
    fase3: {
        text: "Dentro la foresta, senti rumori strani. Vuoi avvicinarti o scappare?",
        choices: [
            { text: "Avvicinati", next: "fase5" },
            { text: "Scappa", next: "fine" }
        ]
    },
    fase4: {
        text: "In città, scopri un misterioso negozio. Vuoi entrare o allontanarti?",
        choices: [
            { text: "Entra nel negozio", next: "fase6" },
            { text: "Allontanati", next: "fine" }
        ]
    },
    fase5: {
        text: "Scopri un mostro nascosto nella foresta. Sei pronto a combattere?",
        choices: [
            { text: "Combatti", next: "vittoria" },
            { text: "Fuggi", next: "fine" }
        ]
    },
    fase6: {
        text: "Il negozio è pieno di oggetti strani. Vuoi acquistare qualcosa?",
        choices: [
            { text: "Compra un oggetto magico", next: "fase7" },
            { text: "Esci dal negozio", next: "fine" }
        ]
    },
    fase7: {
        text: "Hai acquistato un potente oggetto magico! Torni alla foresta per affrontare il mostro.",
        choices: [
            { text: "Combatti con l'oggetto magico", next: "vittoria" },
            { text: "Fuggi", next: "fine" }
        ]
    },
    fine: {
        text: "La tua avventura è finita. Grazie per aver giocato!",
        choices: []
    },
    vittoria: {
        text: "Hai vinto! Hai sconfitto il mostro e salvato il regno. Complimenti, eroe!",
        choices: [
            { text: "Gioca di nuovo", next: "fase1" },
            { text: "Esci", next: "fine" }
        ]
    }
};

let currentStage = "fase1";

function updateStory() {
    const stage = stages[currentStage];
    document.getElementById("story-text").textContent = stage.text;
    const choicesContainer = document.getElementById("choices-container");
    choicesContainer.innerHTML = "";
    stage.choices.forEach(choice => {
        const button = document.createElement("button");
        button.textContent = choice.text;
        button.classList.add("choice-button");
        button.onclick = () => goToNextStage(choice.next);
        choicesContainer.appendChild(button);
    });
}

function goToNextStage(nextStage) {
    currentStage = nextStage;
    updateStory();
}

// Inizializzazione quando il documento è caricato
document.addEventListener('DOMContentLoaded', function() {
    // Inizializza il gestore utenti
    UserManager.init();
    
    // Gestisci il pulsante di logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            UserManager.logout();
        });
    }

    // Inizializza la storia
    updateStory();
});
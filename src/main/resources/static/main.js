/**
 * UserManager - Classe per la gestione degli utenti e dell'autenticazione
 */
const UserManager = {
    currentUser: null,
    
    /**
     * Inizializza il gestore utenti
     */
    init: function() {
        // Controlla se l'utente è già loggato (dal localStorage)
        const savedUser = localStorage.getItem('currentUser');
        if (savedUser) {
            try {
                this.currentUser = JSON.parse(savedUser);
                this.updateUIForAuthState(true);
            } catch (e) {
                console.error('Errore nel parsing dei dati utente:', e);
                localStorage.removeItem('currentUser');
            }
        } else {
            this.updateUIForAuthState(false);
        }
        
        // Aggiungi event listeners per login/logout
        this.setupEventListeners();
    },
    
    /**
     * Configura gli event listeners per login/logout
     */
    setupEventListeners: function() {
        // Login trigger nella sidebar
        const loginTrigger = document.getElementById('loginTrigger');
        if (loginTrigger) {
            loginTrigger.addEventListener('click', function(e) {
                e.preventDefault();
                window.location.href = 'login.html';
            });
        }
        
        // Login prompt button
        const loginPromptBtn = document.getElementById('loginPromptBtn');
        if (loginPromptBtn) {
            loginPromptBtn.addEventListener('click', function(e) {
                e.preventDefault();
                window.location.href = 'login.html';
            });
        }
        
        // Logout trigger
        const logoutTrigger = document.getElementById('logoutTrigger');
        if (logoutTrigger) {
            logoutTrigger.addEventListener('click', this.logout.bind(this));
        }
    },
    
    /**
     * Effettua il login dell'utente
     * @param {string} email - Email dell'utente
     * @param {string} password - Password dell'utente
     * @param {function} callback - Funzione di callback (error, data)
     */
    login: function(email, password, callback) {
        // Simulazione di login (in un'app reale, qui ci sarebbe una chiamata API)
        if (email && password) {
            // Crea un oggetto utente di esempio
            this.currentUser = { 
                email: email,
                username: email.split('@')[0], // Usa la parte prima della @ come username
                level: 5
            };
            
            // Salva l'utente nel localStorage
            localStorage.setItem('currentUser', JSON.stringify(this.currentUser));
            
            // Aggiorna l'UI
            this.updateUIForAuthState(true);
            
            // Chiama il callback con successo
            if (callback) callback(null, this.currentUser);
        } else {
            // Chiama il callback con errore
            if (callback) callback(new Error("Credenziali non valide"));
        }
    },
    
    /**
     * Effettua il logout dell'utente
     */
    logout: function(e) {
        if (e) e.preventDefault();
        
        // Rimuovi l'utente dal localStorage
        localStorage.removeItem('currentUser');
        this.currentUser = null;
        
        // Aggiorna l'UI
        this.updateUIForAuthState(false);
        
        // Reindirizza alla home
        window.location.href = 'index.html';
    },
    
    /**
     * Aggiorna l'interfaccia utente in base allo stato di autenticazione
     * @param {boolean} isAuthenticated - Se l'utente è autenticato
     */
    updateUIForAuthState: function(isAuthenticated) {
        // Elementi visibili solo agli utenti autenticati
        const authElements = document.querySelectorAll('[data-auth]');
        // Elementi visibili solo agli ospiti
        const guestElements = document.querySelectorAll('[data-guest]');
        
        // Aggiorna la visibilità degli elementi
        authElements.forEach(el => {
            el.style.display = isAuthenticated ? 'block' : 'none';
        });
        
        guestElements.forEach(el => {
            el.style.display = isAuthenticated ? 'none' : 'block';
        });
        
        // Aggiorna le informazioni dell'utente se autenticato
        if (isAuthenticated && this.currentUser) {
            // Aggiorna l'avatar con le iniziali dell'utente
            const userAvatar = document.getElementById('userAvatar');
            if (userAvatar) {
                const initials = this.currentUser.username.substring(0, 2).toUpperCase();
                userAvatar.textContent = initials;
            }
            
            // Aggiorna il nome utente
            const usernameElement = document.getElementById('username');
            if (usernameElement) {
                usernameElement.textContent = this.currentUser.username;
            }
            
            // Nascondi il prompt di login
            const loginPrompt = document.getElementById('loginPrompt');
            const adventureContainer = document.getElementById('adventureContainer');
            
            if (loginPrompt) loginPrompt.style.display = 'none';
            if (adventureContainer) adventureContainer.style.display = 'block';
        } else {
            // Mostra il prompt di login
            const loginPrompt = document.getElementById('loginPrompt');
            const adventureContainer = document.getElementById('adventureContainer');
            
            if (loginPrompt) loginPrompt.style.display = 'flex';
            if (adventureContainer) adventureContainer.style.display = 'none';
        }
    },
    
    /**
     * Verifica se l'utente è autenticato
     * @returns {boolean} - true se l'utente è autenticato, false altrimenti
     */
    isAuthenticated: function() {
        return this.currentUser !== null;
    },
    
    /**
     * Ottiene l'utente corrente
     * @returns {Object|null} - L'utente corrente o null se non autenticato
     */
    getCurrentUser: function() {
        return this.currentUser;
    }
};

/**
 * HomeManager - Classe per la gestione della home page
 */
const HomeManager = {
    /**
     * Inizializza il gestore della home page
     */
    init: function() {
        // Inizializza il gestore utenti
        UserManager.init();
        
        // Configura il toggle della sidebar
        this.setupSidebarToggle();
        
        // Correggi il link di registrazione
        this.fixRegistrationLink();
        
        // Configura gli event listeners per le avventure
        this.setupAdventureListeners();
    },
    
    /**
     * Configura il toggle della sidebar
     */
    setupSidebarToggle: function() {
        const toggleBtn = document.getElementById('toggleSidebar');
        const sidebar = document.getElementById('sidebar');
        
        if (toggleBtn && sidebar) {
            toggleBtn.addEventListener('click', function() {
                sidebar.classList.toggle('collapsed');
            });
        }
    },
    
    /**
     * Corregge il link di registrazione (da register.html a registrazione.html)
     */
    fixRegistrationLink: function() {
        const registerLinks = document.querySelectorAll('a[href="register.html"]');
        
        registerLinks.forEach(link => {
            link.setAttribute('href', 'registrazione.html');
        });
    },
    
    /**
     * Configura gli event listeners per le avventure
     */
    setupAdventureListeners: function() {
        // Gestisce i pulsanti di scelta nelle avventure
        const choiceButtons = document.querySelectorAll('.choice-button');
        
        choiceButtons.forEach(button => {
            button.addEventListener('click', function() {
                // Qui andrebbe la logica per gestire le scelte dell'avventura
                alert('Hai scelto: ' + this.textContent.trim());
            });
        });
        
        // Gestisce il pulsante per nuova avventura
        const newAdventureBtn = document.getElementById('newAdventureBtn');
        
        if (newAdventureBtn) {
            newAdventureBtn.addEventListener('click', function() {
                if (UserManager.isAuthenticated()) {
                    // Qui andrebbe la logica per creare una nuova avventura
                    alert('Creazione di una nuova avventura...');
                } else {
                    // Reindirizza al login se non autenticato
                    window.location.href = 'login.html';
                }
            });
        }
    }
};

// Inizializza il gestore della home page quando il documento è pronto
document.addEventListener('DOMContentLoaded', function() {
    // Se siamo nella home page, inizializza il gestore della home
    if (document.getElementById('sidebar')) {
        HomeManager.init();
    }
});
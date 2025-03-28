<script>
class UserManager {
    constructor() {
        this.currentUser = null;
        this.stories = this.loadStories();
    }

    init() {
        this.bindEvents();
        this.checkAuthStatus();
    }

    bindEvents() {
        document.addEventListener('click', (e) => {
            if (e.target.id === 'loginTrigger') this.showLoginModal();
            if (e.target.id === 'closeModal') this.hideLoginModal();
        });

        document.getElementById('loginForm')?.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });

        document.getElementById('logoutBtn')?.addEventListener('click', () => this.logout());
    }

    // Metodi di autenticazione
    async handleLogin() {
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        if (!this.validateEmail(email)) {
            this.showError('Formato email non valido');
            return;
        }

        if (password.length < 6) {
            this.showError('La password deve contenere almeno 6 caratteri');
            return;
        }

        try {
            await this.login(email, password);
            this.hideLoginModal();
            this.updateUI();
        } catch (error) {
            this.showError(error.message);
        }
    }

    validateEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    async login(email, password) {
        // Simulazione chiamata API
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                if (email && password) {
                    this.currentUser = {
                        email,
                        username: email.split('@')[0],
                        joinedDate: new Date().toISOString(),
                        token: Math.random().toString(36).substr(2)
                    };

                    this.saveUserData();
                    resolve();
                } else {
                    reject(new Error('Credenziali non valide'));
                }
            }, 500);
        });
    }

    logout() {
        this.currentUser = null;
        localStorage.removeItem('currentUser');
        this.updateUI();
        window.location.reload();
    }

    // Gestione dati utente
    saveUserData() {
        try {
            localStorage.setItem('currentUser', JSON.stringify({
                email: this.currentUser.email,
                username: this.currentUser.username,
                joinedDate: this.currentUser.joinedDate
            }));
        } catch (error) {
            console.error('Errore salvataggio dati:', error);
        }
    }

    checkAuthStatus() {
        try {
            const userData = localStorage.getItem('currentUser');
            if (userData) this.currentUser = JSON.parse(userData);
        } catch (error) {
            console.error('Errore lettura dati:', error);
        }
    }

    // Gestione storie
    loadStories() {
        try {
            return JSON.parse(localStorage.getItem('userStories')) || [];
        } catch {
            return [];
        }
    }

    saveStories() {
        localStorage.setItem('userStories', JSON.stringify(this.stories));
    }

    createStory(storyData) {
        if (!this.currentUser) return null;
        
        const newStory = {
            ...storyData,
            id: Date.now(),
            author: this.currentUser.username,
            created: new Date().toISOString()
        };

        this.stories.push(newStory);
        this.saveStories();
        return newStory;
    }

    // UI methods
    updateUI() {
        const authElements = document.querySelectorAll('[data-auth]');
        authElements.forEach(el => {
            el.style.display = this.currentUser ? 'block' : 'none';
        });

        const guestElements = document.querySelectorAll('[data-guest]');
        guestElements.forEach(el => {
            el.style.display = this.currentUser ? 'none' : 'block';
        });

        if (this.currentUser) {
            document.getElementById('userAvatar').textContent = this.currentUser.username[0].toUpperCase();
            document.getElementById('username').textContent = this.currentUser.username;
        }
    }

    showLoginModal() {
        document.getElementById('loginModal').style.display = 'flex';
    }

    hideLoginModal() {
        document.getElementById('loginModal').style.display = 'none';
    }

    showError(message) {
        const errorEl = document.getElementById('loginError');
        if (errorEl) {
            errorEl.textContent = message;
            errorEl.style.display = 'block';
            setTimeout(() => errorEl.style.display = 'none', 3000);
        }
    }
}

// Inizializzazione
document.addEventListener('DOMContentLoaded', () => {
    const userManager = new UserManager();
    userManager.init();
    window.userManager = userManager; // Debug
});
</script>
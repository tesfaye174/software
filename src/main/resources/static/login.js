document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('form-login');
    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            login();
        });
    }
});

async function login() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Validazione base dei campi
    if (!email || !password) {
        mostraErrore('Per favore, compila tutti i campi');
        return;
    }

    if (!validaEmail(email)) {
        mostraErrore('Per favore, inserisci un indirizzo email valido');
        return;
    }

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok) {
            // Salva il token di autenticazione
            localStorage.setItem('authToken', data.token);
            // Reindirizza alla dashboard o homepage
            window.location.href = 'index.html';
        } else {
            mostraErrore(data.message || 'Errore durante il login');
        }
    } catch (error) {
        console.error('Errore durante il login:', error);
        mostraErrore('Si è verificato un errore durante il login. Riprova più tardi.');
    }
}

function validaEmail(email) {
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regexEmail.test(email);
}

function mostraErrore(messaggio) {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.textContent = messaggio;
        errorDiv.style.display = 'block';

        // Nascondi il messaggio dopo 5 secondi
        setTimeout(() => {
            errorDiv.style.display = 'none';
        }, 5000);
    } else {
        alert(messaggio);
    }
}
function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\\.,;:\s@\"]+\.)+[^<>()[\]\\.,;:\s@\"]{2,})$/i;
    return re.test(email);
}

function checkPasswordStrength(password) {
    const strengthBar = document.getElementById('strength-bar');
    let strength = 0;
    if (password.length >= 8) strength += 1;
    if (password.match(/[a-z]+/)) strength += 1;
    if (password.match(/[A-Z]+/)) strength += 1;
    if (password.match(/[0-9]+/)) strength += 1;
    if (password.match(/[$@#&!]+/)) strength += 1;
    strengthBar.value = strength;
}

function registrazione() {
    const nomeUtente = document.getElementById('nome-utente').value;
    const email = document.getElementById('email').value;
    const givenPassword = document.getElementById('password-registrazione').value;
    const confermaPassword = document.getElementById('conferma-password').value;

    if (!validateEmail(email)) {
        alert("Email non valida!");
        document.getElementById('email').classList.add('error');
        return;
    } else {
        document.getElementById('email').classList.remove('error');
    }

    if (givenPassword !== confermaPassword) {
        alert("Le password non corrispondono!");
        document.getElementById('password-registrazione').classList.add('error');
        document.getElementById('conferma-password').classList.add('error');
        return;
    } else {
        document.getElementById('password-registrazione').classList.remove('error');
        document.getElementById('conferma-password').classList.remove('error');
    }

    document.getElementById('form-registrazione').addEventListener('submit', function(event) {
        event.preventDefault();

        const formData = {
            username: nomeUtente,
            email: email,
            password: givenPassword
        };

        fetch('http://localhost:8080/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
        .then(response => response.json())
        .then(data => {
            alert('Registration successful!');
            console.log(data);
            
            UserManager.login(nomeUtente, givenPassword, (loginError, loginData) => {
                if (loginError) {
                    alert("Registrazione completata, ma errore durante il login automatico. Prova ad accedere manualmente.");
                    window.location.href = "login.html";
                    return;
                }
                window.location.href = "home.html";
            });
        })
        .catch(error => {
            alert('Registration failed!');
            console.error('Error:', error);
        });
    });
}

// Inizializza il gestore utenti quando la pagina si carica
document.addEventListener('DOMContentLoaded', function() {
    UserManager.init();
});
function registrazione() {
    const nomeUtente = document.getElementById('nome-utente').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password-registrazione').value;
    const confermaPassword = document.getElementById('conferma-password').value;

    if (password !== confermaPassword) {
        alert('Le password non coincidono. Riprova.');
        return;
    }

    // Simulazione della registrazione (puoi sostituirla con una chiamata a un'API server-side)
    alert(`Registrazione completata! Benvenuto, ${nomeUtente}`);
    // Resetta il form
    document.getElementById('form-registrazione').reset();
}

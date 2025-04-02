// Script principale per Storie Intrecciate

// Gestione del tema chiaro/scuro
function toggleTheme() {
    const body = document.body;
    if (body.classList.contains('dark-theme')) {
        body.classList.remove('dark-theme');
        localStorage.setItem('theme', 'light');
    } else {
        body.classList.add('dark-theme');
        localStorage.setItem('theme', 'dark');
    }
}

// Controlla se l'utente ha già impostato un tema preferito
document.addEventListener('DOMContentLoaded', function() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-theme');
    }
    
    // Inizializza UserManager se disponibile
    if (typeof UserManager !== 'undefined') {
        UserManager.init();
    }
});

// Animazioni per elementi con classi specifiche
document.addEventListener('DOMContentLoaded', function() {
    const animatedElements = document.querySelectorAll('.fade-in, .slide-up, .slide-down, .slide-left, .slide-right, .pulse');
    
    animatedElements.forEach(element => {
        element.classList.add('animated');
    });
});
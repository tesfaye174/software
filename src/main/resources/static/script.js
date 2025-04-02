// Gestione avanzata del tema chiaro/scuro
function toggleTheme() {
    const body = document.body;
    const themeButton = document.getElementById('theme-toggle');

    // Toggle tema e aggiorna elementi UI correlati
    const isDark = body.classList.toggle('dark-theme');
    const theme = isDark ? 'dark' : 'light';

    // Aggiorna testo e accessibilità del pulsante
    if (themeButton) {
        const buttonText = isDark ? 'Attiva tema chiaro' : 'Attiva tema scuro';
        themeButton.textContent = buttonText;
        themeButton.setAttribute('aria-label', buttonText);
    }

    // Salvataggio preferenze con gestione errori
    try {
        localStorage.setItem('theme', theme);
    } catch (e) {
        console.error('Salvataggio preferenza fallito:', e);
    }
}

// Inizializzazione all'avvio
document.addEventListener('DOMContentLoaded', function() {
    // Applicazione tema salvato
    try {
        const savedTheme = localStorage.getItem('theme') || 'light';
        document.body.classList.toggle('dark-theme', savedTheme === 'dark');
    } catch (e) {
        console.error('Lettura preferenza fallita:', e);
    }

    // Inizializzazione moduli con gestione errori
    if (typeof UserManager !== 'undefined') {
        try {
            UserManager.init();
        } catch (e) {
            console.error('Inizializzazione UserManager fallita:', e);
        }
    }

    // Animazioni avanzate con Intersection Observer
    const animatedElements = document.querySelectorAll('[class*="slide-"], .fade-in, .pulse');
    const animationObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animated');
                // animationObserver.unobserve(entry.target); // Disabilita per animazioni ripetute
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px' // Anticipa il trigger del 5%
    });

    animatedElements.forEach(el => animationObserver.observe(el));
});
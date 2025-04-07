document.addEventListener('DOMContentLoaded', function() {
    // Vérifier si l'utilisateur a déjà une préférence enregistrée localement
    const localTheme = localStorage.getItem('theme');
    <script th:src="@{/js/darkmode.js}" defer></script>

    // Vérifier si le serveur a défini une préférence via un attribut de données
    const serverThemePreference = document.documentElement.getAttribute('data-server-theme');

    // Déterminer le thème initial
    let currentTheme;
    if (localTheme) {
        // Priorité à la préférence locale
        currentTheme = localTheme;
    } else if (serverThemePreference) {
        // Utiliser la préférence du serveur si présente
        currentTheme = serverThemePreference;
    } else {
        // Fallback aux préférences système
        currentTheme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    // Appliquer le thème actuel
    if (currentTheme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    }

    // Détecter les préférences du système (mode sombre)
    const prefersDarkScheme = window.matchMedia('(prefers-color-scheme: dark)');

    // Si c'est la première visite et que l'utilisateur préfère le mode sombre au niveau du système
    if (!localStorage.getItem('theme') && !serverThemePreference && prefersDarkScheme.matches) {
        document.documentElement.setAttribute('data-theme', 'dark');
        localStorage.setItem('theme', 'dark');
    }

    // Écouter les changements de préférence au niveau du système
    prefersDarkScheme.addEventListener('change', (e) => {
        // Ne changer automatiquement que si l'utilisateur n'a pas fait de choix manuel
        if (!localStorage.getItem('theme') && !serverThemePreference) {
            const newTheme = e.matches ? 'dark' : 'light';
            document.documentElement.setAttribute('data-theme', newTheme);
        }
    });
});
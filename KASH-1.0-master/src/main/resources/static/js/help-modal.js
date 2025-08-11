// Funciones para la ventana modal de ayuda

// Función para mostrar el modal de ayuda
function showHelpModal() {
    document.getElementById("helpModal").style.display = "block";
}

// Función para cerrar el modal de ayuda
function closeHelpModal() {
    document.getElementById("helpModal").style.display = "none";
    
    // Guardar en sessionStorage que el modal se ha mostrado
    sessionStorage.setItem('helpModalShown', 'true');
    
    // Crear el botón de ayuda flotante después de cerrar el modal
    createHelpButton();
}

// Función para determinar el tipo de usuario basado en la URL
function getUserType() {
    const path = window.location.pathname;
    
    if (path.includes('/aprendiz/')) {
        return 'aprendiz';
    } else if (path.includes('/asesor/')) {
        return 'asesor';
    } else if (path.includes('/coordinador/')) {
        return 'coordinador';
    } else {
        return 'general';
    }
}

// Inicializar el modal de ayuda cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    // Verificar si el modal ya se ha mostrado antes en esta sesión
    const modalShown = sessionStorage.getItem('helpModalShown');
    
    // Solo mostrar el modal si no se ha mostrado antes en esta sesión
    if (!modalShown) {
        // Mostrar el modal de ayuda después de 1 segundo
        setTimeout(function() {
            const helpModal = document.getElementById("helpModal");
            if (helpModal) {
                showHelpModal();
                
                // Cerrar automáticamente después de 30 segundos
                setTimeout(function() {
                    closeHelpModal();
                }, 30000);
            }
        }, 1000);
    } else {
        // Si ya se mostró antes, crear un botón de ayuda flotante
        createHelpButton();
    }
    
    // Cerrar modal al hacer clic fuera de él
    window.addEventListener('click', function(event) {
        var helpModal = document.getElementById("helpModal");
        if (helpModal && event.target == helpModal) {
            closeHelpModal();
        }
    });
    
    // Asignar evento al botón de cerrar
    var closeButton = document.querySelector('.help-modal-close');
    if (closeButton) {
        closeButton.addEventListener('click', closeHelpModal);
    }
    
    // Asignar evento al botón de entendido
    var entendidoButton = document.querySelector('.help-modal-footer button');
    if (entendidoButton) {
        entendidoButton.addEventListener('click', closeHelpModal);
    }
});

// Función para crear un botón de ayuda flotante
function createHelpButton() {
    // Verificar si ya existe el botón
    if (document.getElementById("helpButton")) return;
    
    // Verificar si existe el modal de ayuda
    const helpModal = document.getElementById("helpModal");
    if (!helpModal) return;
    
    const helpButton = document.createElement("button");
    helpButton.id = "helpButton";
    helpButton.className = "help-floating-button";
    helpButton.innerHTML = '<i class="fas fa-question-circle"></i>';
    helpButton.title = "Mostrar ayuda";
    
    // Agregar evento para mostrar el modal al hacer clic
    helpButton.addEventListener("click", showHelpModal);
    
    // Agregar el botón al body
    document.body.appendChild(helpButton);
} 
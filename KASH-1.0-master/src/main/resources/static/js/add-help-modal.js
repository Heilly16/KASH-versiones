/**
 * Script para añadir automáticamente el modal de ayuda a cualquier vista
 * 
 * Uso: 
 * 1. Incluir este script en la página
 * 2. Llamar a la función addHelpModal con el título y contenido deseado
 * 
 * Ejemplo:
 * addHelpModal('Ayuda - Mi Vista', [
 *    { title: 'Funcionalidad 1', description: 'Descripción de la funcionalidad 1' },
 *    { title: 'Funcionalidad 2', description: 'Descripción de la funcionalidad 2' }
 * ]);
 */

function addHelpModal(title, items, additionalText = '') {
    // Crear elementos del modal
    const modal = document.createElement('div');
    modal.id = 'helpModal';
    modal.className = 'help-modal';
    
    // Crear encabezado
    const header = document.createElement('div');
    header.className = 'help-modal-header';
    
    const headerTitle = document.createElement('h3');
    headerTitle.innerHTML = `<i class="fas fa-question-circle"></i> ${title}`;
    
    const closeButton = document.createElement('button');
    closeButton.className = 'help-modal-close';
    closeButton.innerHTML = '&times;';
    closeButton.onclick = function() { closeHelpModal(); };
    
    header.appendChild(headerTitle);
    header.appendChild(closeButton);
    
    // Crear contenido
    const content = document.createElement('div');
    content.className = 'help-modal-content';
    
    const introText = document.createElement('p');
    introText.textContent = 'Esta sección le permite gestionar sus tareas. A continuación se detallan las principales funcionalidades:';
    content.appendChild(introText);
    
    const list = document.createElement('ul');
    items.forEach(item => {
        const listItem = document.createElement('li');
        listItem.innerHTML = `<strong>${item.title}:</strong> ${item.description}`;
        list.appendChild(listItem);
    });
    content.appendChild(list);
    
    if (additionalText) {
        const additionalPara = document.createElement('p');
        additionalPara.textContent = additionalText;
        content.appendChild(additionalPara);
    }
    
    // Crear pie del modal
    const footer = document.createElement('div');
    footer.className = 'help-modal-footer';
    
    const confirmButton = document.createElement('button');
    confirmButton.textContent = 'Entendido';
    confirmButton.onclick = function() { closeHelpModal(); };
    
    footer.appendChild(confirmButton);
    
    // Ensamblar el modal
    modal.appendChild(header);
    modal.appendChild(content);
    modal.appendChild(footer);
    
    // Añadir el modal al body
    document.body.appendChild(modal);
    
    // Mostrar el modal después de un breve retraso
    setTimeout(function() {
        showHelpModal();
        
        // Cerrar automáticamente después de 60 segundos
        setTimeout(function() {
            closeHelpModal();
        }, 60000);
    }, 1000);
}

// Función para mostrar el modal de ayuda
function showHelpModal() {
    document.getElementById("helpModal").style.display = "block";
}

// Función para cerrar el modal de ayuda
function closeHelpModal() {
    document.getElementById("helpModal").style.display = "none";
}

// Inicializar el modal de ayuda cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    // Cerrar modal al hacer clic fuera de él
    window.addEventListener('click', function(event) {
        var helpModal = document.getElementById("helpModal");
        if (helpModal && event.target == helpModal) {
            closeHelpModal();
        }
    });
}); 
package com.proyecto.KASH.controlador;

import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.UsuarioServicio2;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IngresoControlador {

    @Autowired
    private UsuarioServicio2 servicio;

    @GetMapping("/index")
    public String mostrarIndex() {
        return "index"; // Asegúrate de que este método existe
    }

    @GetMapping("/ingresar")
    public String ingresar(@RequestParam String usuario, @RequestParam String pass, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioEncontrado = servicio.findByUsuario(usuario).orElse(null);

        if (usuarioEncontrado != null && BCrypt.checkpw(pass, usuarioEncontrado.getPass())) {
            // Verificar si el usuario está activo
            if (!"activo".equalsIgnoreCase(usuarioEncontrado.getEstado())) {
                redirectAttributes.addFlashAttribute("mensajeError", "Su cuenta se encuentra inactiva. Contacte al administrador.");
                return "redirect:/";
            }
            
            String rol = usuarioEncontrado.getRolSeleccionado();
            session.setAttribute("usuario", usuarioEncontrado);

            System.out.println("Usuario encontrado: " + usuarioEncontrado);
            System.out.println("Rol del usuario: " + rol);

            switch (rol.trim().toLowerCase()) {
                case "coordinador":
                    return "redirect:/coordinador/usuarios";
                case "aprendiz":
                    return "redirect:/aprendiz/asesorias";
                case "asesor":
                    return "redirect:/asesor/asesorias";
                default:
                    redirectAttributes.addFlashAttribute("mensajeError", "Rol no válido.");
                    return "redirect:/";
            }
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Usuario o contraseña incorrectos.");
            return "redirect:/";
        }
    }

    @GetMapping("/cerrar-sesion")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // Eliminar la sesión
        return "redirect:/"; // Redirigir al inicio de sesión
    }

}

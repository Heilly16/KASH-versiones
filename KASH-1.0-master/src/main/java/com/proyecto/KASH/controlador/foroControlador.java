package com.proyecto.KASH.controlador;

import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.UsuarioServicio;
import com.proyecto.KASH.servicio.UsuarioServicio2;
import org.springframework.ui.Model;
import com.proyecto.KASH.servicio.foroServicio;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.foro;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class foroControlador {

    @Autowired
    private foroServicio servicio;

    @Autowired
    private UsuarioServicio2 usuarioServicio;

    @GetMapping("/foro")
    public String foro(Model modelo) {
        modelo.addAttribute("foro", servicio.listarForos());
        return "foro";
    }


   @GetMapping("/ingresar/foro")
    public String ingresar(@RequestParam String usuario, @RequestParam String pass, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioEncontrado = usuarioServicio.findByUsuario(usuario).orElse(null);

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
                    return "redirect:/foro";
                case "aprendiz":
                    return "redirect:/foro";
                case "asesor":
                    return "redirect:/ForoFormulario";
                default:
                    redirectAttributes.addFlashAttribute("mensajeError", "Rol no válido.");
                    return "redirect:/";
            }
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Usuario o contraseña incorrectos.");
            return "redirect:/";
        }
    }

    @GetMapping("/foro/imagen/{id}")
    public void obtenerFoto(@PathVariable Long id, HttpServletResponse response) throws IOException, SQLException {
        // Recuperamos el foro por ID
        foro foro = servicio.obtenerFotoId(id);

        if (foro != null && foro.getFoto() != null) {
            // Establecemos el tipo de contenido como imagen para mostrarla en la respuesta
            response.setContentType("image/jpeg");
            response.getOutputStream().write(foro.getFoto().getBytes(1, (int) foro.getFoto().length()));
            response.getOutputStream().close();
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}

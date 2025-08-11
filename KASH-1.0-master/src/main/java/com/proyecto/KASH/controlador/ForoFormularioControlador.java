package com.proyecto.KASH.controlador;

import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.foro;
import com.proyecto.KASH.servicio.EmailService;
import com.proyecto.KASH.servicio.UsuarioServicioImpl;
import com.proyecto.KASH.servicio.foroServicio;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ForoFormularioControlador {

    @Autowired
    private foroServicio foroServicio;

    @Autowired
    private UsuarioServicioImpl usuarioServicio;

    @Autowired
    private EmailService correoServicio;

    @GetMapping("/ForoFormulario")
    public String mostrarFormularioForo(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        System.out.println("Usuario en sesión: " + usuario.getNombres());
        model.addAttribute("usuario", usuario);

        return "ForoFormulario"; // Carga la vista ForoFormulario.html
    }

    @PostMapping("/foroNuevo")
    public String foroNuevo(
            @RequestParam String nombre,
            @RequestParam int idUsuario,
            @RequestParam int trimestre,
            @RequestParam String tema,
            @RequestParam String contenido,
            @RequestParam("imagen") MultipartFile imagen, // Recibimos la imagen
            Model model) throws IOException {

        // Crear un nuevo objeto Foro
        foro nuevoForo = new foro();
        nuevoForo.setNombre(nombre);
        nuevoForo.setIdUsuario(idUsuario);
        nuevoForo.setFecha(LocalDate.now());
        nuevoForo.setTrimestre(trimestre);
        nuevoForo.setTema(tema);
        nuevoForo.setContenido(contenido);

        // Guardar el foro en la base de datos sin la imagen
        foro foroRegistrado = foroServicio.guardarForo(nuevoForo);

        // Si la imagen no está vacía, guardarla en la base de datos
        if (!imagen.isEmpty()) {
            foroServicio.guardarFoto(imagen, nuevoForo.getId());
        }

        if (foroRegistrado != null) {
            List<Usuario> usuarios = usuarioServicio.obtenerTodosLosUsuarios();

            for (Usuario usuario : usuarios) {
                String nombreUsuario = usuario.getNombres();
                String correoUsuario = usuario.getCorreo();

                String asunto = "¡Nuevo Foro Disponible! 📢";
                String mensaje = "Hola, " + nombreUsuario + " 👋\n\n"
                        + "Se ha creado un nuevo foro sobre: " + tema + "\n\n"
                        + "Ven a la página para observarlo y participar en la discusión. ¡No te lo pierdas! 🎉\n\n"
                        + "Saludos,\n"
                        + "El equipo de KASH 🚀";

                // Enviar correo individual a cada usuario
                correoServicio.sendMassEmail(List.of(correoUsuario), asunto, mensaje);
            }

        }

        // Agregar mensaje de éxito al modelo
        model.addAttribute("mensaje", "El foro se ha creado correctamente");

        // Redirigir a la página de foros
        return "redirect:/foro";
    }
}

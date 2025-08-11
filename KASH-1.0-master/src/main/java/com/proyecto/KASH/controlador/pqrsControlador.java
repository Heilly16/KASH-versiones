package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.PQRSRepository;
import com.proyecto.KASH.entidad.EstadoPQRS;
import com.proyecto.KASH.entidad.PQRS;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.EmailService;
import com.proyecto.KASH.servicio.PQRSServicio;
import com.proyecto.KASH.servicio.UsuarioServicio;
import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class pqrsControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private PQRSServicio pqrsServicio;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PQRSRepository PQRSRepositorio;

    @PostMapping("/enviarPQRS")
    public String enviarPQRS(
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("fecha") String fecha,
            @RequestParam("tipo") String tipo,
            @RequestParam("detalles") String detalles,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("pqrsControlador.enviarPQRS() called");
        System.out.println("idUsuario: " + idUsuario);
        System.out.println("fecha: " + fecha);
        System.out.println("tipo: " + tipo);
        System.out.println("detalles: " + detalles);

        Optional<Usuario> usuario = usuarioServicio.buscarPorIdUsuario(idUsuario);

        if (usuario.isPresent()) {
            Usuario usuarioEncontrado = usuario.get();
            String nombreUsuario = usuarioEncontrado.getNombres();
            String correoUsuario = usuarioEncontrado.getCorreo();

            // Buscar un coordinador disponible (rol = "coordinador")
            Optional<Usuario> coordinador = usuarioServicio.buscarPorRol("coordinador");

            if (coordinador.isEmpty()) {
                redirectAttributes.addFlashAttribute("mensajeError", "No hay coordinadores disponibles.");
                return "redirect:/foro";
            }

            Usuario coordinadorAsignado = coordinador.get();

            // Convertir la fecha
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fechaConvertida = LocalDate.parse(fecha, formatter);

            // Crear la PQRS
            PQRS pqrs = new PQRS();
            pqrs.setIdUsuario(idUsuario);
            pqrs.setIdCoordinador(coordinadorAsignado.getIdUsuario()); // Asignar coordinador
            pqrs.setNombre(nombreUsuario);
            pqrs.setEmail(correoUsuario);
            pqrs.setFecha(fechaConvertida);
            pqrs.setTipo(tipo);
            pqrs.setDetalles(detalles);
            pqrs.setEstado(EstadoPQRS.Activo);   // para estado por defecto


            // Guardar la PQRS
            PQRS pqrsGuardada = pqrsServicio.guardarPQRS(pqrs);

            if (pqrsGuardada != null) {
                String asunto = "PQRS Kash, " + nombreUsuario + "!";
                String mensaje = "Hola " + nombreUsuario + ", tu PQRS fue registrada y enviada exitosamente al coordinador. Pronto recibirás una respuesta.";

                if (correoUsuario != null && !correoUsuario.isEmpty()) {
                    try {
                        emailService.sendEmail(correoUsuario, asunto, mensaje);
                    } catch (Exception e) {
                        System.out.println("Error al enviar el correo: " + e.getMessage());
                    }
                }
                redirectAttributes.addFlashAttribute("mensajeExito", "La PQRS fue enviada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "Hubo un problema al registrar la PQRS.");
            }
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "El usuario no existe. Verifica el número de documento.");
        }

        return "redirect:/foro"; // Retorna a la vista
    }

    @GetMapping("/coordinador/buscarPQRS-simple")
    public String buscarPQRS(@RequestParam(name = "filtro", required = false) String filtro, Model model) {
        List<PQRS> pqrsList;

        if (filtro != null && !filtro.isEmpty()) {
            pqrsList = pqrsServicio.buscarPorCriterio(filtro);
        } else {
            pqrsList = pqrsServicio.listarTodas();
        }

        model.addAttribute("pqrsList", pqrsList);
        model.addAttribute("filtro", filtro);
        return "coordinador/PQRS"; // Asegúrate de que esta vista exista
    }

    @PostMapping("/coordinador/responderPQRS")
    public String responderPQRS(@RequestParam("id") Long idPQRS,
            @RequestParam("respuesta") String respuesta) throws MessagingException {
        PQRS pqrs = PQRSRepositorio.findById(idPQRS).orElse(null);
        if (pqrs != null) {
            // Enviar correo
            emailService.sendEmail(
                    pqrs.getEmail(),
                    "Respuesta a tu PQRS",
                    respuesta
            );

            // Cambiar estado a Inactivo
           pqrs.setEstado(EstadoPQRS.Inactivo); // al responder la PQRS
            PQRSRepositorio.save(pqrs);
        }
        return "redirect:/coordinador/PQRS";
    }

}

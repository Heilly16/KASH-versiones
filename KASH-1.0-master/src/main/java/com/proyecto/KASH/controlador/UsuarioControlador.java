package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.servicio.EmailService;
import com.proyecto.KASH.servicio.ProgramaServicio;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UsuarioControlador {

    @Autowired
    private Usuario2Repositorio usuarioRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private ProgramaServicio programaServicio;

    @GetMapping("/registrar")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("nuevoUsuario", new Usuario());
        model.addAttribute("programas", programaServicio.listarNombresProgramas());
        return "registrar";
    }

    @PostMapping("/guardarUsuario")
    public String guardarUsuario(
            Model model,
            @RequestParam String rolSeleccionado,
            @RequestParam String nombres,
            @RequestParam String primerA,
            @RequestParam String segundoA,
            @RequestParam String fNacimiento,
            @RequestParam String tDocumento,
            @RequestParam String idUsuario,
            @RequestParam(required = false, defaultValue = "N/A") String trimestre,
            @RequestParam String correo,
            @RequestParam String numero,
            @RequestParam String usuario,
            @RequestParam String pass,
            @RequestParam String programa,
            @RequestParam(required = false) String etapa,
            RedirectAttributes redirectAttributes
    ) {

        // --- Validaciones ---
        if (fNacimiento == null || fNacimiento.isBlank()) {
            redirectAttributes.addFlashAttribute("fechaVacia", "Debe ingresar la fecha de nacimiento.");
            return "redirect:/registrar";
        }

        LocalDate fechaNacimiento;
        try {
            fechaNacimiento = LocalDate.parse(fNacimiento);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("fechaInvalida", "El formato de la fecha no es válido.");
            return "redirect:/registrar";
        }

        if (usuarioRepository.existsByUsuario(usuario)) {
            redirectAttributes.addFlashAttribute("usuario", "El nombre de usuario ya está registrado. Intente con otro.");
            return "redirect:/registrar";
        }

        if (!idUsuario.matches("\\d+")) {
            redirectAttributes.addFlashAttribute("NumDocumento", "El número de documento debe contener solo números.");
            return "redirect:/registrar";
        }

        if (!Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$").matcher(pass).matches()) {
            redirectAttributes.addFlashAttribute("contraseña", "La contraseña debe contener letras y números y al menos 6 caracteres.");
            return "redirect:/registrar";
        }

        Long idUsuarioNum = Long.parseLong(idUsuario);
        int trimestreNum = "N/A".equals(trimestre) ? 0 : Integer.parseInt(trimestre);
        int edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();

        if (edad < 14) {
            redirectAttributes.addFlashAttribute("edad14", "No puedes registrarte si eres menor de 14 años.");
            return "redirect:/registrar";
        }

        if ((tDocumento.equals("Cedula de Ciudadania") || tDocumento.equals("Cedula de Extranjeria")) && edad < 18) {
            redirectAttributes.addFlashAttribute("tipoDocumento", "Debes tener al menos 18 años para usar este tipo de documento.");
            return "redirect:/registrar";
        }

        if (correo == null || correo.isBlank()) {
            redirectAttributes.addFlashAttribute("correo", "El correo electrónico es obligatorio.");
            return "redirect:/registrar";
        }

        if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            redirectAttributes.addFlashAttribute("formato", "El formato del correo no es válido.");
            return "redirect:/registrar";
        }

        if (!numero.matches("\\d{7,15}")) {
            redirectAttributes.addFlashAttribute("contacto", "El número de contacto debe tener entre 7 y 15 dígitos.");
            return "redirect:/registrar";
        }

        if (rolSeleccionado.equals("asesor") && trimestreNum <= 3 && !"N/A".equals(trimestre)) {
            redirectAttributes.addFlashAttribute("error", "Para ser asesor debes estar en un trimestre mayor a 2.");
            return "redirect:/registrar";
        }
        
        if (programa == null || programa.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un programa.");
            return "redirect:/registrar";
        }
        
        // Validar que si etapa es "Lectiva", el trimestre no puede ser N/A
        if ("Lectiva".equals(etapa) && "N/A".equals(trimestre)) {
            redirectAttributes.addFlashAttribute("error", "Si estás en etapa lectiva, debes seleccionar un trimestre válido.");
            return "redirect:/registrar";
        }

        if (rolSeleccionado.isBlank() || nombres.isBlank() || primerA.isBlank() || fNacimiento == null || tDocumento.isBlank()
                || idUsuario.isBlank() || trimestre.isBlank() || correo.isBlank() || numero.isBlank() || usuario.isBlank() || pass.isBlank()) {
            redirectAttributes.addFlashAttribute("camposVacios", "Debe llenar los campos obligatorios.");
            return "redirect:/registrar";
        }

        // --- Guardar Usuario ---
        Usuario nuevoUsuario = new Usuario();
        if ("aprendiz".equals(rolSeleccionado)) {
            nuevoUsuario.setRolSeleccionado("Aprendiz");
        } else {
            nuevoUsuario.setRolSeleccionado(rolSeleccionado);
        }
        nuevoUsuario.setNombres(nombres);
        nuevoUsuario.setPrimerA(primerA);
        nuevoUsuario.setSegundoA(segundoA);
        nuevoUsuario.setFNacimiento(fechaNacimiento);
        nuevoUsuario.setTDocumento(tDocumento);
        nuevoUsuario.setIdUsuario(idUsuarioNum);
        nuevoUsuario.setTrimestre(trimestre);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setNumero(numero);
        nuevoUsuario.setUsuario(usuario);
        nuevoUsuario.setPrograma(programa);
        nuevoUsuario.setEtapa(etapa);
        String passEncriptada = BCrypt.hashpw(pass, BCrypt.gensalt());
        nuevoUsuario.setPass(passEncriptada);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        if (usuarioGuardado != null) {
            String asunto = "Bienvenido a KASH, " + usuario + "!";
            String mensaje = "Hola " + usuario + ",\n\n"
                    + "Tu rol en KASH es: " + rolSeleccionado + ".\n\n"
                    + "En este espacio tendrás experiencia en las asesorías: un asesor las impartirá y los aprendices las tomarán.\n\n"
                    + "También podrás disfrutar del foro y participar en otras actividades.\n\n"
                    + "¡Bienvenido a la comunidad de KASH!";

            try {
                emailService.sendEmail(usuarioGuardado.getCorreo(), asunto, mensaje);
                redirectAttributes.addFlashAttribute("success", "Usuario registrado correctamente. Revisa tu correo.");
            } catch (Exception e) {
                System.out.println("Error al enviar el correo: " + e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Error al enviar el correo, pero el usuario fue registrado.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Hubo un error al registrar el usuario.");
        }

        return "redirect:/index";
    }

    @PostMapping("/actualizarPerfil")
    public String actualizarPerfil(
            @RequestParam("correo") String correo,
            @RequestParam("numero") String numero,
            @RequestParam("tDocumento") String tDocumento,
            @RequestParam("redirectUrl") String redirectUrl,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/index";
        }
        
        try {
            // Actualizar solo los campos permitidos
            usuario.setCorreo(correo);
            usuario.setNumero(numero);
            usuario.setTDocumento(tDocumento);
            
            // Guardar los cambios
            usuarioRepository.save(usuario);
            
            // Actualizar la sesión con los datos actualizados
            session.setAttribute("usuario", usuario);
            
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/api/sesion-activa")
    @ResponseBody
    public ResponseEntity<?> sesionActiva(HttpSession session) {
        if (session.getAttribute("usuario") != null) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}

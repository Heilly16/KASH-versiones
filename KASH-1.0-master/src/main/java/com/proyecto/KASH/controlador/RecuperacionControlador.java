    package com.proyecto.KASH.controlador;

    import com.proyecto.KASH.servicio.RecuperacionServicio;
    import com.proyecto.KASH.servicio.UsuarioServicio;
    import jakarta.transaction.Transactional;
    import java.util.HashMap;
    import java.util.Map;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    @Controller
    @RequestMapping("/recuperacion")
    public class RecuperacionControlador {

        @Autowired
        private RecuperacionServicio recuperacionServicio;

        @Autowired
        private UsuarioServicio usuarioServicio;

        @GetMapping("/contrasena")
        public String mostrarFormularioRecuperacion() {
            return "RecuperarPass";
        }

        @PostMapping("/solicitar")
        public ResponseEntity<Map<String, String>> solicitarRecuperacion(@RequestParam String email) {
            Map<String, String> response = new HashMap<>();

            if (email == null || email.isEmpty()) {
                response.put("success", "false");
                response.put("message", "El correo es obligatorio.");
                return ResponseEntity.badRequest().body(response);
            }

            boolean enviado = recuperacionServicio.enviarCodigoRecuperacion(email);

            if (!enviado) {
                response.put("success", "false");
                response.put("message", "El correo no está registrado.");
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", "true");
            response.put("message", "Código enviado al correo.");
            return ResponseEntity.ok(response);
        }

        @PostMapping("/validar")
        public ResponseEntity<Map<String, String>> validarCodigo(@RequestParam String email, @RequestParam String codigo) {
            Map<String, String> response = new HashMap<>();

            boolean valido = recuperacionServicio.validarCodigo(email, codigo);

            if (valido) {
                response.put("success", "true");
                response.put("message", "Código correcto. Procede a cambiar la contraseña.");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", "false");
                response.put("message", "Código incorrecto.");
                return ResponseEntity.badRequest().body(response);
            }
        }

        @Transactional
        @PostMapping("/cambiar")
        public ResponseEntity<Map<String, String>> cambiarContrasena(
                @RequestParam String email,
                @RequestParam String nuevaPassword) {

            usuarioServicio.cambiarContrasena(email, nuevaPassword);
            recuperacionServicio.eliminarCodigo(email);

            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("message", "Contraseña actualizada correctamente.");

            return ResponseEntity.ok(response);
        }

    }

package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.AprendizAsesoriaRepositorio;
import com.proyecto.KASH.Repository.AsesoriaRepositorio;
import com.proyecto.KASH.Repository.FotosRepositorio;
import com.proyecto.KASH.Repository.GrupoAprendizRepositorio;
import com.proyecto.KASH.Repository.GrupoRepositorio;
import com.proyecto.KASH.Repository.PreguntaRepositorio;
import com.proyecto.KASH.Repository.PruebaRepositorio;
import com.proyecto.KASH.Repository.RespuestaRepositorio;
import com.proyecto.KASH.Repository.RespuestaSeleccionadaRepositorio;
import com.proyecto.KASH.Repository.ResultadoPruebaRepositorio;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Asistencia;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Pregunta;
import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.Respuesta;
import com.proyecto.KASH.entidad.RespuestaSeleccionada;
import com.proyecto.KASH.entidad.ResultadoPrueba;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.fotos;
import com.proyecto.KASH.servicio.AsesorAsistenciaServicio;
import com.proyecto.KASH.servicio.AsesorComponenteServicio;
import com.proyecto.KASH.servicio.AsesorPruebaServicio;
import com.proyecto.KASH.servicio.AsesoriaServicio;
import com.proyecto.KASH.servicio.EmailService;
import com.proyecto.KASH.servicio.FotoServicio;
import com.proyecto.KASH.servicio.GrupoServicio;
import com.proyecto.KASH.servicio.UsuarioServicio;
import com.proyecto.KASH.servicio.AprendizComponenteServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.TemporalQueries.localDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.web.server.ServerWebExchangeExtensionsKt.principal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import java.time.format.DateTimeFormatter;

@Controller
public class AsesorControlador {

    // foto
    @Autowired
    private FotoServicio fotoServicio;  // Inyectamos la interfaz FotoServicio

    @Autowired
    private AprendizComponenteServicio aprendizComponenteServicio;

    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable("id") Long idUsuario) throws IOException, SQLException {
        fotos foto = fotoServicio.obtenerFotoPorIdUsuario(idUsuario);
        if (foto != null) {
            // Convierte el Blob a un arreglo de bytes
            byte[] fotoBytes = foto.getFoto().getBytes(1, (int) foto.getFoto().length());

            // Devuelve la imagen como un ResponseEntity con el tipo de contenido adecuado
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // O usa el tipo adecuado para tu imagen (image/png, etc.)
                    .body(fotoBytes);
        }
        // Si no se encuentra la foto, retorna una respuesta 404 (No encontrado)
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/fotoAsesor/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerFotoAsesor(@PathVariable("id") Long idUsuario) throws IOException, SQLException {
        try {
            fotos foto = fotoServicio.obtenerFotoPorIdUsuario(idUsuario);
            if (foto != null && foto.getFoto() != null) {
                // Convierte el Blob a un arreglo de bytes
                byte[] fotoBytes = foto.getFoto().getBytes(1, (int) foto.getFoto().length());

                // Devuelve la imagen como un ResponseEntity con el tipo de contenido adecuado
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(fotoBytes);
            } else {
                // Si no se encuentra la foto o el Blob es nulo, devolvemos una respuesta exitosa pero vacía
                // El cliente mostrará la imagen por defecto definida en el atributo alt del HTML
                // Opcional: podrías cargar una imagen por defecto desde los recursos estáticos aquí
                // Si no se encuentra la foto o el Blob es nulo, cargar la imagen por defecto
                try (java.io.InputStream in = new org.springframework.core.io.ClassPathResource("static/img/perfil.png").getInputStream()) {
                    byte[] defaultImageBytes = org.springframework.util.StreamUtils.copyToByteArray(in);
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_PNG) // Asumiendo que perfil.png es PNG
                            .body(defaultImageBytes);
                } catch (IOException e2) {
                    System.err.println("Error al cargar la imagen por defecto: " + e2.getMessage());
                    return ResponseEntity.notFound().build(); // Si la imagen por defecto falla, 404
                }
            }
        } catch (Exception e) {
            System.err.println("Error general al obtener foto del asesor para ID " + idUsuario + ": " + e.getMessage());
            // En caso de cualquier otro error, intentar devolver la imagen por defecto
            try (java.io.InputStream in = new org.springframework.core.io.ClassPathResource("static/img/perfil.png").getInputStream()) {
                byte[] defaultImageBytes = org.springframework.util.StreamUtils.copyToByteArray(in);
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(defaultImageBytes);
            } catch (IOException e3) {
                System.err.println("Error al cargar la imagen por defecto después de un error general: " + e3.getMessage());
                return ResponseEntity.notFound().build(); // Si la imagen por defecto falla, 404
            }
        }
    }

    @PostMapping("/asesor/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto, HttpSession session, Model model, @RequestParam("redirectUrl") String redirectUrl) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        if (!foto.isEmpty()) {
            try {
                // Verifica si el asesor ya tiene una foto
                fotos fotoExistente = fotoServicio.obtenerFotoPorIdUsuario(asesor.getIdUsuario());

                if (fotoExistente != null) {
                    // Si ya existe una foto, actualiza la foto existente
                    fotoServicio.actualizarFoto(foto, asesor.getIdUsuario());
                    model.addAttribute("mensaje", "Foto actualizada exitosamente");
                } else {
                    // Si no existe una foto, guarda la nueva foto
                    fotoServicio.guardarFoto(foto, asesor.getIdUsuario());
                    model.addAttribute("mensaje", "Foto subida exitosamente");
                }
            } catch (IOException e) {
                model.addAttribute("error", "Error al subir la foto");
            }
        }
        return "redirect:" + redirectUrl; // Redirige al perfil después de subir la foto
    }

    @Autowired
    private AsesoriaServicio asesoriaServicio;

    @RequestMapping("/asesor/asesorias")
    public String AsesorAsesorias(Model model, HttpSession session, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null || !"asesor".equalsIgnoreCase(asesor.getRolSeleccionado())) {
            return "redirect:/index";
        }

        Long idAsesor = asesor.getIdUsuario();
        String nombre = asesor.getNombres();
        List<Asesoria> todasAsesorias = asesoriaServicio.obtenerAsesoriasPorAsesor(idAsesor); // <- importante cambiar método

        Map<Integer, Asesoria> asesoriasPorGrupo = new HashMap<>();
        LocalDate hoy = LocalDate.now();

        for (Asesoria a : todasAsesorias) {
            if (a.getFecha() == null) {
                continue;
            }

            int idGrupo = a.getGrupo().getId();
            Asesoria actual = asesoriasPorGrupo.get(idGrupo);

            if (actual == null) {
                asesoriasPorGrupo.put(idGrupo, a);
            } else {
                long distanciaActual = Math.abs(ChronoUnit.DAYS.between(hoy, actual.getFecha()));
                long distanciaNueva = Math.abs(ChronoUnit.DAYS.between(hoy, a.getFecha()));

                if (distanciaNueva < distanciaActual
                        || (distanciaNueva == distanciaActual && a.getFecha().isBefore(actual.getFecha()))) {
                    asesoriasPorGrupo.put(idGrupo, a);
                }
            }
        }

        List<Asesoria> asesoriasFiltradas = new ArrayList<>(asesoriasPorGrupo.values());
        asesoriasFiltradas.sort((a1, a2) -> {
            boolean a1EsFutura = !a1.getFecha().isBefore(hoy);
            boolean a2EsFutura = !a2.getFecha().isBefore(hoy);

            if (a1EsFutura && !a2EsFutura) {
                return -1;
            }
            if (!a1EsFutura && a2EsFutura) {
                return 1;
            }

            return a1.getFecha().compareTo(a2.getFecha());
        });

        Asesoria asesoriaProxima = asesoriasFiltradas.stream()
                .filter(a -> !a.getFecha().isBefore(hoy))
                .min(Comparator.comparing(Asesoria::getFecha))
                .orElse(null);

        if (asesoriaProxima != null) {
            model.addAttribute("idAsesoriaProxima", asesoriaProxima.getId());
            model.addAttribute("fechaProximaAsesoria", asesoriaProxima.getFecha());
        }

        model.addAttribute("asesorias", asesoriasFiltradas);
        model.addAttribute("nombre", nombre);

        Set<Integer> gruposTotales = new HashSet<>();
        Set<Integer> gruposActivos = new HashSet<>();
        Set<Integer> gruposCompletados = new HashSet<>();

        for (Asesoria a : asesoriasFiltradas) {
            int idGrupo = a.getGrupo().getId();
            gruposTotales.add(idGrupo);

            if ("Activa".equalsIgnoreCase(a.getEstado())) {
                gruposActivos.add(idGrupo);
            }

            if (a.isCompletada()) {
                gruposCompletados.add(idGrupo);
            }
        }

        model.addAttribute("totalAsesorias", gruposTotales.size());
        model.addAttribute("asesoriasActivas", gruposActivos.size());
        model.addAttribute("asesoriasCompletadas", gruposCompletados.size());

        model.addAttribute("asesor", asesor);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "asesor/asesorias";
    }

    @PostMapping("/asesor/asesoria/{id}/completar")
    public String marcarComoCompletada(@PathVariable("id") int id, RedirectAttributes redirect) {
        Optional<Asesoria> opt = asesoriaRepositorio.findById(id);
        if (opt.isPresent()) {
            Asesoria asesoria = opt.get();
            
            // Verificar si la fecha y hora de la asesoría ya pasaron
            LocalDate fechaActual = LocalDate.now();
            LocalTime horaActual = LocalTime.now();
            
            boolean fechaPasada = asesoria.getFecha().isBefore(fechaActual);
            boolean mismaFechaHoraPasada = asesoria.getFecha().isEqual(fechaActual) && 
                                          asesoria.getHora().isBefore(horaActual);
            
            if (fechaPasada || mismaFechaHoraPasada) {
                asesoria.setEstado("Inactiva");
                asesoria.setCompletada(true);
                asesoriaRepositorio.save(asesoria);
                redirect.addFlashAttribute("mensaje", "Asesoría marcada como completada.");
            } else {
                redirect.addFlashAttribute("error", "No se puede marcar como completada una asesoría que aún no ha ocurrido.");
            }
        }
        return "redirect:/asesor/asesorias";
    }

    //Vista crearAsesorias
    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private EmailService correoServicio;

    @Autowired
    private GrupoServicio grupoServicio;

    @Autowired
    private AsesoriaRepositorio asesoriasRepositorio;

    @GetMapping("/asesor/crearAsesorias")
    public String crearAsesorias(Model model, HttpSession session, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");

        if (asesor != null) {
            // 1. Grupos del asesor
            List<Grupo> gruposAsignados = grupoServicio.obtenerGruposAceptadosPorAsesor(asesor.getIdUsuario());

            // 2. Grupos con asesorías ya creadas
            List<Grupo> gruposConAsesoria = asesoriasRepositorio.findDistinctGrupoByAsesorId(asesor.getIdUsuario());

            // 3. Filtrar grupos sin asesoría
            List<Grupo> gruposSinAsesoria = gruposAsignados.stream()
                    .filter(grupo -> !gruposConAsesoria.contains(grupo))
                    .collect(Collectors.toList());

            model.addAttribute("grupos", gruposSinAsesoria);
        }

        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);

        model.addAttribute("asesor", asesor);
        model.addAttribute("redirectUrl", request.getRequestURI());
        model.addAttribute("fechaActual", LocalDate.now());
        model.addAttribute("fechaManana", manana);

        return "asesor/crearAsesorias";
    }

    @PostMapping("/asesor/guardarAsesoria")
    public String guardarAsesoria(
            @RequestParam("grupoId") int idGrupo,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("hora") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam("link") String link,
            @RequestParam("diasAsesoria") int diasAsesoria,
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            RedirectAttributes redirectAttributes
    ) {

        Grupo grupo = grupoServicio.obtenerGrupoPorId(idGrupo);
        if (grupo == null) {
            redirectAttributes.addFlashAttribute("error", "El grupo no existe.");
            return "redirect:/asesor/crearAsesorias";
        }
        
        // Validar que la hora no esté en el rango de 10 PM a 6 AM
        LocalTime horaInicio = LocalTime.of(22, 0); // 10 PM
        LocalTime horaFin = LocalTime.of(6, 0); // 6 AM
        
        if ((hora.isAfter(horaInicio) || hora.equals(horaInicio)) || (hora.isBefore(horaFin) || hora.equals(horaFin))) {
            redirectAttributes.addFlashAttribute("error", "No se pueden programar asesorías entre las 10:00 PM y las 6:00 AM.");
            return "redirect:/asesor/crearAsesorias";
        }
        
        // Validar que la fecha de inicio sea al menos del día siguiente
        LocalDate mañana = LocalDate.now().plusDays(1);
        if (fechaInicio.isBefore(mañana)) {
            redirectAttributes.addFlashAttribute("error", "La fecha de inicio debe ser al menos a partir de mañana.");
            return "redirect:/asesor/crearAsesorias";
        }

        // Aquí haces la validación de conflicto, por ejemplo llamando al repositorio o servicio
        if (asesoriaServicio.existeConflicto(grupo, fecha, hora)) {
            redirectAttributes.addFlashAttribute("error", "El grupo o asesor ya tienen una asesoría en esa fecha y hora.");
            return "redirect:/asesor/crearAsesorias";
        }

        Asesoria asesoria = new Asesoria();
        asesoria.setGrupo(grupo);
        asesoria.setFecha(fecha);
        asesoria.setHora(hora);
        asesoria.setLink(link);
        asesoria.setDias_asesoria(diasAsesoria);
        asesoria.setFecha_inicio(fechaInicio);
        asesoria.setEstado("Activa");

        // Solo aquí llamas al método void que guarda y envía correos
        asesoriaServicio.crearAsesoria(asesoria);

        redirectAttributes.addFlashAttribute("mensaje", "Asesoría creada y notificada correctamente.");
        return "redirect:/asesor/crearAsesorias";
    }

    @Autowired
    private AsesorAsistenciaServicio asistenciaServicio;

    @Autowired
    private AsesoriaRepositorio asesoriaRepositorio;

    //Vista Asistencia
    @RequestMapping("/asesor/asistencia")
    public String AsesorAsistencia(Model model, HttpSession session, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";
        }

        Long idAsesor = asesor.getIdUsuario();
        String nombre = asesor.getNombres();
        List<Asesoria> todasAsesorias = asesoriaServicio.obtenerAsesoriasPorAsesor(idAsesor);

        LocalDate hoy = LocalDate.now();
        Map<Integer, Asesoria> asesoriasPorGrupo = new HashMap<>();

        for (Asesoria a : todasAsesorias) {
            if (a.getFecha() == null) {
                continue;
            }
            int idGrupo = a.getGrupo().getId();
            
            // Si ya existe una asesoría para este grupo, verificar si ya tiene asistencia registrada
            if (asesoriasPorGrupo.containsKey(idGrupo)) {
                Asesoria actual = asesoriasPorGrupo.get(idGrupo);
                
                // Si la asesoría actual ya tiene asistencia registrada, buscar la siguiente
                if (asistenciaServicio.existeAsistenciaRegistrada((long)actual.getId())) {
                    // Si la asesoría es de hoy, mantenerla
                    if (actual.getFecha().equals(hoy)) {
                        continue;
                    }
                    
                    // Si la nueva asesoría es de hoy, reemplazar
                    if (a.getFecha().equals(hoy)) {
                        asesoriasPorGrupo.put(idGrupo, a);
                        continue;
                    }
                    
                    // Si ninguna es de hoy, elegir la más cercana a futuro
                    if (a.getFecha().isAfter(hoy) && 
                        (actual.getFecha().isBefore(hoy) || a.getFecha().isBefore(actual.getFecha()))) {
                        asesoriasPorGrupo.put(idGrupo, a);
                    }
                }
                // Si no tiene asistencia registrada, mantener la más cercana a hoy
                else {
                    long distanciaActual = Math.abs(ChronoUnit.DAYS.between(hoy, actual.getFecha()));
                    long distanciaNueva = Math.abs(ChronoUnit.DAYS.between(hoy, a.getFecha()));

                    if (distanciaNueva < distanciaActual
                            || (distanciaNueva == distanciaActual && a.getFecha().isBefore(actual.getFecha()))) {
                        asesoriasPorGrupo.put(idGrupo, a);
                    }
                }
            } else {
                // Si es la primera asesoría para este grupo, guardarla
                asesoriasPorGrupo.put(idGrupo, a);
            }
        }

        List<Asesoria> asesoriasFiltradas = new ArrayList<>(asesoriasPorGrupo.values());

        asesoriasFiltradas.sort((a1, a2) -> {
            if (a1.isCompletada() && !a2.isCompletada()) {
                return 1;
            }
            if (!a1.isCompletada() && a2.isCompletada()) {
                return -1;
            }

            boolean a1EsFutura = !a1.getFecha().isBefore(hoy);
            boolean a2EsFutura = !a2.getFecha().isBefore(hoy);

            if (a1EsFutura && !a2EsFutura) {
                return -1;
            }
            if (!a1EsFutura && a2EsFutura) {
                return 1;
            }

            return a1.getFecha().compareTo(a2.getFecha());
        });

        Asesoria asesoriaProxima = asesoriasFiltradas.stream()
                .filter(a -> !a.getFecha().isBefore(hoy))
                .min(Comparator.comparing(Asesoria::getFecha))
                .orElse(null);

        if (asesoriaProxima != null) {
            model.addAttribute("idAsesoriaProxima", asesoriaProxima.getId());
            model.addAttribute("fechaProximaAsesoria", asesoriaProxima.getFecha());
        }

        Set<Integer> gruposTotales = new HashSet<>();
        Set<Integer> gruposActivos = new HashSet<>();
        Set<Integer> gruposCompletados = new HashSet<>();

        Map<Integer, List<Usuario>> aprendicesPorAsesoria = new HashMap<>();
        Map<Integer, Boolean> asistenciaRegistrada = new HashMap<>();

        for (Asesoria a : asesoriasFiltradas) {
            int idGrupo = a.getGrupo().getId();
            gruposTotales.add(idGrupo);

            if ("Activa".equalsIgnoreCase(a.getEstado())) {
                gruposActivos.add(idGrupo);
            }

            if (a.isCompletada()) {
                gruposCompletados.add(idGrupo);
            }

            // Llenar aprendices del grupo de esta asesoría
            List<Usuario> usuarioAprendiz = new ArrayList<>();
            for (GrupoAprendiz ga : a.getGrupo().getAprendices()) {
                usuarioAprendiz.add(ga.getUsuario());
            }

            aprendicesPorAsesoria.put(a.getId(), usuarioAprendiz);
            
            // Verificar si ya se registró asistencia para esta asesoría
            boolean tieneAsistencia = asistenciaServicio.existeAsistenciaRegistrada((long)a.getId());
            asistenciaRegistrada.put(a.getId(), tieneAsistencia);
        }

        model.addAttribute("usuarioAprendiz", aprendicesPorAsesoria);
        model.addAttribute("asistenciaRegistrada", asistenciaRegistrada);
        model.addAttribute("fechaActual", hoy);
        model.addAttribute("asesorias", asesoriasFiltradas);
        model.addAttribute("nombre", nombre);
        model.addAttribute("asesor", asesor);
        model.addAttribute("totalAsesorias", gruposTotales.size());
        model.addAttribute("asesoriasActivas", gruposActivos.size());
        model.addAttribute("asesoriasCompletadas", gruposCompletados.size());
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "asesor/asistencia";
    }

    @Autowired
    private GrupoAprendizRepositorio grupoAprendizRepositorio;

    @GetMapping("/asesor/aprendicesPorAsesoria/{id}")
    @ResponseBody
    public List<Map<String, Object>> obtenerAprendicesPorAsesoria(@PathVariable("id") Long idAsesoria) {
        // Obtener el ID del grupo como Integer
        int idGrupo = asesoriaRepositorio.findById(idAsesoria).get().getGrupo().getIdGrupo();
        Integer idGrupoInteger = Integer.valueOf(idGrupo);
        
        List<GrupoAprendiz> grupoAprendices = grupoAprendizRepositorio
                .findByGrupo_Id(idGrupoInteger);

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (GrupoAprendiz ga : grupoAprendices) {
            Usuario aprendiz = ga.getUsuario();
            if (aprendiz != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("idUsuario", aprendiz.getIdUsuario());
                map.put("nombreCompleto", aprendiz.getNombres() + " " + aprendiz.getPrimerA() + " " + aprendiz.getSegundoA());
                resultado.add(map);
            }
        }

        return resultado;
    }

    @PostMapping("/asesor/registrarAsistencia")
    public String registrarAsistencias(
            @RequestParam("idAsesoria") Long idAsesoria,
            @RequestParam("idUsuarios") List<Long> idUsuarios,
            @RequestParam("asistencias") List<Boolean> asistencias,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Asesoria> optAsesoria = asesoriaServicio.obtenerPorId(idAsesoria.intValue());
        if (optAsesoria.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La asesoría no fue encontrada.");
            return "redirect:/asesor/asistencia";
        }
        Asesoria asesoria = optAsesoria.get();

        if (idUsuarios.size() != asistencias.size()) {
            redirectAttributes.addFlashAttribute("error", "El número de aprendices y asistencias no coincide.");
            return "redirect:/asesor/asistencia";
        }
        
        // Verificar que la asistencia se registre el mismo día de la asesoría
        LocalDate fechaActual = LocalDate.now();
        if (!asesoria.getFecha().equals(fechaActual)) {
            redirectAttributes.addFlashAttribute("error", "La asistencia solo puede registrarse el mismo día de la asesoría.");
            return "redirect:/asesor/asistencia";
        }

        for (int i = 0; i < idUsuarios.size(); i++) {
            Long idUsuario = idUsuarios.get(i);
            Boolean asistio = asistencias.get(i);

            usuarioServicio.buscarPorIdUsuario(idUsuario).ifPresent(usuario -> {
                Asistencia asistencia = new Asistencia();
                asistencia.setAsesoria(asesoria);
                asistencia.setUsuario(usuario);
                asistencia.setFecha(LocalDate.now());
                asistencia.setAsistio(asistio);
                asistenciaServicio.guardar(asistencia);
            });
        }

        redirectAttributes.addFlashAttribute("success", "Asistencias registradas correctamente.");
        return "redirect:/asesor/asistencia";
    }

    //Vista grupos
    @GetMapping("/asesor/grupo")
    public String mostrarGruposDelAsesor(HttpSession session, Model model, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";
        }

        // Obtener grupos asignados al asesor actual
        List<Grupo> gruposDelAsesor = grupoServicio.obtenerGruposPorAsesor(asesor.getIdUsuario());

        // Obtener grupos disponibles para el asesor
        // Filtrar para mostrar un solo grupo por componente y con menos de 5 estudiantes
        List<Grupo> todosGruposDisponibles = grupoRepositorio.findGruposSinAsesor();
        Map<String, Grupo> gruposPorComponente = new HashMap<>();
        
        // Asesorías activas para marcar grupos
        List<Asesoria> asesoriasActivas = asesoriaServicio.obtenerAsesoriasActivas();
        Set<Integer> idsGruposConAsesoria = asesoriasActivas.stream()
                .map(Asesoria::getGrupo)
                .filter(Objects::nonNull)
                .map(Grupo::getIdGrupo)
                .collect(Collectors.toSet());

        // Obtener el programa del asesor
        String programaAsesor = asesor.getPrograma();

        // Filtrar por nombre de componente, número de estudiantes y programa del asesor
        for (Grupo grupo : todosGruposDisponibles) {
            String nombreComponente = grupo.getNombre(); // El nombre del grupo es el nombre del componente
            
            // Verificar si el componente pertenece al programa del asesor
            boolean perteneceAlPrograma = aprendizComponenteServicio.componentePerteneceAPrograma(nombreComponente, programaAsesor);
            
            // Solo considerar grupos con menos de 5 estudiantes y que pertenezcan al programa del asesor
            if (perteneceAlPrograma && grupo.getAprendices() != null && grupo.getAprendices().size() < 5) {
                // Solo añadir si no hay un grupo para este componente o si este no tiene asesorías activas
                if (!gruposPorComponente.containsKey(nombreComponente) ||
                    (!idsGruposConAsesoria.contains(grupo.getIdGrupo()) && 
                     idsGruposConAsesoria.contains(gruposPorComponente.get(nombreComponente).getIdGrupo()))) {
                    gruposPorComponente.put(nombreComponente, grupo);
                }
            }
        }
        
        List<Grupo> gruposDisponiblesFiltrados = new ArrayList<>(gruposPorComponente.values());

        // Agregar al modelo
        model.addAttribute("asesor", asesor);
        model.addAttribute("gruposAsignados", gruposDelAsesor);
        model.addAttribute("gruposDisponibles", gruposDisponiblesFiltrados);
        model.addAttribute("gruposConAsesoriaActiva", idsGruposConAsesoria);
        model.addAttribute("redirectUrl", request.getRequestURI());
        model.addAttribute("nombre", asesor.getNombres());

        return "asesor/grupo";
    }

    @GetMapping("/asesor/grupos/disponibles")
    public String mostrarGruposDisponibles(
            @RequestParam(required = false) String filtro,
            HttpSession session,
            Model model, HttpServletRequest request) {

        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";
        }

        Long idAsesor = asesor.getIdUsuario();
        String programaAsesor = asesor.getPrograma();

        // Buscar solo los grupos que coincidan con el filtro
        List<Grupo> gruposFiltrados = grupoRepositorio.findGruposDisponiblesPorFiltro(filtro);
        
        // Filtrar por programa del asesor
        List<Grupo> gruposFiltradosPorPrograma = new ArrayList<>();
        for (Grupo grupo : gruposFiltrados) {
            String nombreComponente = grupo.getNombre();
            if (aprendizComponenteServicio.componentePerteneceAPrograma(nombreComponente, programaAsesor)) {
                gruposFiltradosPorPrograma.add(grupo);
            }
        }

        // Asesorías activas (si la vista necesita marcar algo)
        List<Asesoria> asesoriasActivas = asesoriaServicio.obtenerAsesoriasActivas();
        Set<Integer> idsGruposConAsesoria = asesoriasActivas.stream()
                .map(Asesoria::getGrupo)
                .filter(Objects::nonNull)
                .map(Grupo::getIdGrupo)
                .collect(Collectors.toSet());

        // Enviar al modelo SOLO lo necesario
        model.addAttribute("asesor", asesor);
        model.addAttribute("gruposConAsesoriaActiva", idsGruposConAsesoria);
        model.addAttribute("gruposDisponibles", gruposFiltradosPorPrograma); // Ahora filtrados por programa
        model.addAttribute("filtro", filtro);
        model.addAttribute("redirectUrl", request.getRequestURI());

        return "asesor/grupo";
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/asesor/entrarGrupo")
    public String entrarGrupo(@RequestParam("idGrupo") Long idGrupo, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";
        }
        
        Long idAsesor = asesor.getIdUsuario();

        // Verificar límite de grupos activos para asesor (máximo 5)
        List<Grupo> gruposActivos = grupoServicio.obtenerGruposActivosPorAsesor(idAsesor);
        if (gruposActivos.size() >= 5) {
            redirectAttributes.addFlashAttribute("error", "No puedes unirte a más de 5 grupos activos.");
            return "redirect:/asesor/grupo";
        }
        
        // Obtener el grupo
        int idGrupoInt = idGrupo.intValue();
        Grupo grupo = grupoRepositorio.findById(idGrupoInt).orElse(null);
        if (grupo == null) {
            redirectAttributes.addFlashAttribute("error", "El grupo no existe.");
            return "redirect:/asesor/grupo";
        }
        
        // Verificar que el grupo no tenga ya un asesor
        if (grupo.getAsesor() != null) {
            redirectAttributes.addFlashAttribute("error", "Este grupo ya tiene un asesor asignado.");
            return "redirect:/asesor/grupo";
        }
        
        // 1. Asignar asesor al grupo directamente con SQL en lugar de usar procedimiento almacenado
        jdbcTemplate.update("UPDATE grupo SET idAsesor = ? WHERE id = ?", idAsesor, idGrupoInt);

        // 2. Crear un nuevo grupo para este componente si es necesario
        grupoServicio.crearGrupoSiNecesario(grupo.getNombre());

        // 3. Obtener aprendices del grupo
        Integer idGrupoInteger = Integer.valueOf(grupo.getIdGrupo());
        List<GrupoAprendiz> grupoAprendices = grupoAprendizRepositorio.findByGrupo_Id(idGrupoInteger);

        List<String> correosAprendices = new ArrayList<>();
        for (GrupoAprendiz ga : grupoAprendices) {
            Usuario aprendiz = ga.getUsuario();
            if (aprendiz != null && aprendiz.getCorreo() != null) {
                correosAprendices.add(aprendiz.getCorreo());
            }
        }

        // 4. Enviar correo masivo
        if (!correosAprendices.isEmpty()) {
            String asunto = "Un asesor se ha unido a tu grupo";
            String cuerpo = "Hola, \n\n"
                    + "Te informamos que el asesor " + asesor.getNombres() + " "
                    + asesor.getPrimerA() + " " + asesor.getSegundoA()
                    + " se ha unido al grupo '" + grupo.getNombre() + "'.\n\n"
                    + "Gracias por usar KASH.";

            correoServicio.sendMassEmail(correosAprendices, asunto, cuerpo);
        }
        
        redirectAttributes.addFlashAttribute("mensaje", "Te has unido al grupo exitosamente.");
        return "redirect:/asesor/grupo";
    }

    //Vista Componente
    @Autowired
    private AsesorComponenteServicio asesorComponenteServicio;

    @GetMapping("/asesor/componente")
    public String registrarComponentes(HttpSession session, Model model, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        // Obtener la lista de componentes disponibles
        List<String> componentesDisponibles = asesorComponenteServicio.obtenerNombresComponentesUnicos();

        // Obtener los componentes elegidos por el asesor
        List<Componente> componentesElegidos = asesorComponenteServicio.obtenerComponentePorUsuario(asesor.getIdUsuario());

        model.addAttribute("componentesDisponibles", componentesDisponibles);
        model.addAttribute("componentesElegidos", componentesElegidos);
        model.addAttribute("asesor", asesor);
        model.addAttribute("redirectUrl", request.getRequestURI());
        return "asesor/componente"; // Retorna la vista para registrar componentes
    }

    @PostMapping("/asesor/componente")
    public String registrarComponente(@RequestParam("componente") String componenteNombre, HttpSession session,
            Model model
    ) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        // Comprobar que el asesor no haya elegido ya 3 componentes
        if (asesorComponenteServicio.obtenerComponentePorUsuario(asesor.getIdUsuario()).size() >= 3) {
            model.addAttribute("mensaje", "No puedes registrar más de 3 componentes.");
            return "redirect:/asesor/componente";  // Redirige al formulario
        }

        // Crear y guardar el nuevo componente
        Componente nuevoComponente = new Componente();
        nuevoComponente.setNombre(componenteNombre);
        nuevoComponente.setUsuario(asesor);

        asesorComponenteServicio.guardarComponente(nuevoComponente);

        return "redirect:/asesor/componente";  // Redirige de nuevo para actualizar la lista
    }

    @PostMapping("/asesor/eliminarComponente")
    @Transactional
    public String eliminarComponente(@RequestParam("id") Long componenteId, HttpSession session,
            Model model
    ) {
        // Obtener el asesor de la sesión
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";  // Si no hay sesión, redirige al index
        }

        // Llamamos al servicio para eliminar la relación
        asesorComponenteServicio.eliminarComponente(componenteId, asesor.getIdUsuario());

        // Mostrar mensaje de éxito
        model.addAttribute("mensaje", "Componente eliminado exitosamente.");
        return "redirect:/asesor/componente";  // Redirige a la vista donde se muestran los componentes
    }

    // Vista Prueba
    @Autowired
    private AsesorPruebaServicio pruebaServicio;

    @Autowired
    private GrupoRepositorio grupoRepositorio;

    @Autowired
    private RespuestaSeleccionadaRepositorio respuestaSeleccionadaRepositorio;

    @Autowired
    private ResultadoPruebaRepositorio resultadoPruebaRepositorio;

    // Mostrar formulario inicial para crear una nueva prueba
    @GetMapping("/asesor/prueba")
    public String asignarPruebas(Model model, HttpSession session, HttpServletRequest request) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");

        if (asesor == null) {
            return "redirect:/index";
        }

        List<Grupo> gruposAsesor = grupoRepositorio.findGruposByAsesor(asesor);
        List<Prueba> todasLasPruebas = pruebaRepositorio.findAll();

        // Agrupar pruebas por id del grupo
        Map<Integer, List<Prueba>> pruebasPorGrupo = new HashMap<>();
        for (Prueba prueba : todasLasPruebas) {
            int idGrupo = prueba.getGrupo().getId(); // ID es int
            pruebasPorGrupo.computeIfAbsent(idGrupo, k -> new ArrayList<>()).add(prueba);
        }

        // Declarar el mapa principal una sola vez, fuera del bucle
        Map<Long, Map<Usuario, List<RespuestaSeleccionada>>> respuestasPorPrueba = new HashMap<>();
        
        // Mapa para las calificaciones
        Map<Long, Map<Usuario, Double>> calificacionesPorPrueba = new HashMap<>();

        for (Prueba prueba : todasLasPruebas) {
            // Este mapa es para agrupar las respuestas de esta prueba por aprendiz
            Map<Usuario, List<RespuestaSeleccionada>> mapa = new HashMap<>();
            
            // Mapa para las calificaciones de esta prueba
            Map<Usuario, Double> calificaciones = new HashMap<>();

            // Obtener todas las respuestas de esta prueba
            List<RespuestaSeleccionada> todas = respuestaSeleccionadaRepositorio.findByPrueba(prueba);

            // Agrupar por aprendiz
            for (RespuestaSeleccionada r : todas) {
                Usuario aprendiz = r.getAprendiz();
                mapa.computeIfAbsent(aprendiz, k -> new ArrayList<>()).add(r);

                System.out.println("Aprendiz: " + aprendiz.getNombres() + " " + aprendiz.getPrimerA() + " " + aprendiz.getSegundoA()
                        + " | Pregunta: " + r.getPregunta().getEnunciado()
                        + " | Respuesta: " + r.getRespuestaSeleccionada().getTexto());
            }
            
            // Obtener calificaciones para esta prueba
            for (Usuario aprendiz : mapa.keySet()) {
                Optional<ResultadoPrueba> resultadoOpt = resultadoPruebaRepositorio.findByAprendizIdAndPruebaId(aprendiz.getIdUsuario(), prueba.getId());
                if (resultadoOpt.isPresent()) {
                    calificaciones.put(aprendiz, resultadoOpt.get().getPuntuacion());
                } else {
                    calificaciones.put(aprendiz, 0.0); // Si no hay resultado, calificación 0
                }
            }

            // Asociar las respuestas agrupadas al ID de la prueba
            respuestasPorPrueba.put(prueba.getId(), mapa);
            calificacionesPorPrueba.put(prueba.getId(), calificaciones);
        }

// Agregar al modelo
        model.addAttribute("respuestasPorPrueba", respuestasPorPrueba);
        model.addAttribute("calificacionesPorPrueba", calificacionesPorPrueba);

        // Agregar modelo para la vista
        model.addAttribute("asesor", asesor);
        model.addAttribute("nombre", asesor.getNombres()); // Nombre para el encabezado
        model.addAttribute("grupos", gruposAsesor);
        model.addAttribute("pruebasPorGrupo", pruebasPorGrupo);
        model.addAttribute("todasLasPruebas", todasLasPruebas);
        model.addAttribute("redirectUrl", request.getRequestURI());
        
        // Para depuración y ayuda
        Map<Integer, String> grupoNombres = new HashMap<>();
        for (Grupo grupo : gruposAsesor) {
            grupoNombres.put(grupo.getId(), grupo.getNombre());
        }
        model.addAttribute("grupoNombres", grupoNombres);

        return "asesor/prueba";
    }

    @Autowired
    private PruebaRepositorio pruebaRepositorio;

    @Autowired
    private Usuario2Repositorio usuarioRepositorio;

    @GetMapping("/asesor/prueba/{pruebaId}/aprendiz/{aprendizId}/respuestas")
    public String verRespuestasAprendiz(@PathVariable Long pruebaId, @PathVariable Long aprendizId, Model model) {
        Prueba prueba = pruebaRepositorio.findById(pruebaId).orElse(null);
        Usuario aprendiz = usuarioRepositorio.findById(aprendizId).orElse(null);

        if (prueba == null || aprendiz == null) {
            return "redirect:/asesor/prueba";
        }

        List<RespuestaSeleccionada> respuestas = respuestaSeleccionadaRepositorio.findByAprendizAndPrueba(aprendiz, prueba);

        model.addAttribute("respuestas", respuestas);
        model.addAttribute("prueba", prueba);
        model.addAttribute("aprendiz", aprendiz);

        return "asesor/prueba"; // Volver a la vista principal de pruebas
    }

    @GetMapping("/asesor/prueba/{pruebaId}/aprendices")
    public String verAprendicesPorPrueba(@PathVariable Long pruebaId, Model model) {
        Prueba prueba = pruebaRepositorio.findById(pruebaId).orElse(null);

        if (prueba == null) {
            return "redirect:/asesor/prueba";
        }

        Grupo grupo = prueba.getGrupo(); // Ya asociado a la prueba

        // Obtener los aprendizajes desde grupoAprendiz
        List<GrupoAprendiz> relaciones = grupo.getAprendices(); // asegúrate que esta sea la lista en la entidad
        List<Usuario> aprendices = relaciones.stream()
                .map(GrupoAprendiz::getUsuario)
                .collect(Collectors.toList());

        model.addAttribute("prueba", prueba);
        model.addAttribute("aprendices", aprendices);

        return "asesor/prueba"; // Vista para mostrar aprendices
    }

    @PostMapping("/guardar")
    public String guardarPrueba(@ModelAttribute Prueba prueba, HttpServletRequest request, HttpSession session) {
        // Obtener asesor de la sesión
        Usuario asesor = (Usuario) session.getAttribute("usuario");

        List<Pregunta> preguntas = new ArrayList<>();

        String totalPreguntasStr = request.getParameter("totalPreguntas");
        int totalPreguntas = 0;
        if (totalPreguntasStr != null && !totalPreguntasStr.isEmpty()) {
            totalPreguntas = Integer.parseInt(totalPreguntasStr);
        } else {
            // Manejar el caso en que totalPreguntas no se proporciona
            // Puedes establecer un valor predeterminado o mostrar un error
            System.err.println("Error: totalPreguntas no se proporcionó en la solicitud.");
            return "redirect:/asesor/prueba"; // Redirige a la página de prueba o muestra un mensaje de error
        }

        for (int i = 0; i < totalPreguntas; i++) {
            Pregunta pregunta = new Pregunta();
            // Obtener el ID de la pregunta (si existe, para edición)
            String preguntaIdStr = request.getParameter("preguntas[" + i + "].id");
            if (preguntaIdStr != null && !preguntaIdStr.isEmpty() && !preguntaIdStr.equals("0")) {
                pregunta.setId(Long.parseLong(preguntaIdStr));
            }
            
            pregunta.setEnunciado(request.getParameter("preguntas[" + i + "].texto"));
            pregunta.setPrueba(prueba); // Asociar la pregunta a la prueba

            String respuestaCorrectaStr = request.getParameter("Correcta_" + i);
            int respuestaCorrectaIndex = -1; // Usamos -1 para indicar que no se ha encontrado
            if (respuestaCorrectaStr != null && !respuestaCorrectaStr.isEmpty()) {
                respuestaCorrectaIndex = Integer.parseInt(respuestaCorrectaStr);
            } else {
                System.err.println("Error: Correcta_" + i + " no se proporcionó en la solicitud.");
                // Considerar si se debe redirigir o manejar de otra forma
                // Por ahora, continuaremos con un valor por defecto o un error
            }
            
            List<Respuesta> respuestas = new ArrayList<>();
            String[] opciones = {
                request.getParameter("preguntas[" + i + "].opcionA"),
                request.getParameter("preguntas[" + i + "].opcionB"),
                request.getParameter("preguntas[" + i + "].opcionC")
            };

            for (int j = 0; j < opciones.length; j++) {
                Respuesta respuesta = new Respuesta();
                respuesta.setTexto(opciones[j]);
                respuesta.setEsCorrecta(j == respuestaCorrectaIndex);
                respuesta.setPregunta(pregunta); // Asociar la respuesta a la pregunta
                respuestas.add(respuesta);
            }

            pregunta.setRespuestas(respuestas);
            preguntas.add(pregunta);
        }

        // Guardar la prueba y sus preguntas
        pruebaServicio.guardarPrueba(prueba, preguntas);

        // Obtener grupo desde la prueba
        Grupo grupo = prueba.getGrupo();

        // Enviar correo a los aprendices del grupo
        if (grupo != null && grupo.getAprendices()!= null) {
            List<String> correosAprendices = new ArrayList<>();
            
            // Obtener correos de los aprendices
            for (GrupoAprendiz ga : grupo.getAprendices()) {
                Usuario aprendiz = ga.getUsuario();
                if (aprendiz != null && aprendiz.getCorreo() != null && !aprendiz.getCorreo().isEmpty()) {
                    correosAprendices.add(aprendiz.getCorreo());
                }
            }

            if (!correosAprendices.isEmpty()) {
                String asunto = "Nueva prueba asignada";
                String cuerpo = "Hola,\n\n"
                        + "El asesor " + asesor.getNombres() + " " + asesor.getPrimerA() + " " + asesor.getSegundoA()
                        + " ha creado una nueva prueba para el grupo '" + grupo.getNombre() + "'.\n\n"
                        + "Gracias por usar KASH.";

                correoServicio.sendMassEmail(correosAprendices, asunto, cuerpo);
            }
        }

        return "redirect:/asesor/prueba";
    }

    @GetMapping("/asesor/grupo/{idGrupo}/asesorias")
    @ResponseBody
    public Map<String, Object> obtenerAsesoriasGrupo(@PathVariable int idGrupo) {
        Grupo grupo = grupoServicio.obtenerGrupoPorId(idGrupo);
        Map<String, Object> resultado = new HashMap<>();
        
        if (grupo == null) {
            resultado.put("nombreGrupo", "Grupo no encontrado");
            resultado.put("asesorias", Collections.emptyList());
            return resultado;
        }
        
        List<Asesoria> asesorias = asesoriaServicio.obtenerAsesoriasPorGrupo(idGrupo);
        
        // Convertir a formato JSON amigable
        List<Map<String, Object>> asesoriasJSON = new ArrayList<>();
        for (Asesoria asesoria : asesorias) {
            Map<String, Object> asesoriaMap = new HashMap<>();
            asesoriaMap.put("id", asesoria.getId());
            asesoriaMap.put("fechaFormateada", asesoria.getFechaFormateada());
            asesoriaMap.put("horaFormateada", asesoria.getHoraFormateada());
            asesoriaMap.put("estado", asesoria.getEstado());
            asesoriaMap.put("completada", asesoria.isCompletada());
            asesoriaMap.put("link", asesoria.getLink());
            asesoriasJSON.add(asesoriaMap);
        }
        
        resultado.put("nombreGrupo", grupo.getNombre());
        resultado.put("asesorias", asesoriasJSON);
        
        return resultado;
    }

    @PostMapping("/asesor/asesoria/{id}/editar")
    public String editarAsesoria(
            @PathVariable("id") int id,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam("hora") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora,
            @RequestParam("link") String link,
            RedirectAttributes redirect) {
        
        Optional<Asesoria> opt = asesoriaRepositorio.findById(id);
        if (opt.isPresent()) {
            Asesoria asesoria = opt.get();
            
            // Validar que la hora no esté en el rango de 10 PM a 6 AM
            LocalTime horaInicio = LocalTime.of(22, 0); // 10 PM
            LocalTime horaFin = LocalTime.of(6, 0); // 6 AM
            
            if ((hora.isAfter(horaInicio) || hora.equals(horaInicio)) || (hora.isBefore(horaFin) || hora.equals(horaFin))) {
                redirect.addFlashAttribute("error", "No se pueden programar asesorías entre las 10:00 PM y las 6:00 AM.");
                return "redirect:/asesor/asesorias";
            }
            
            // Verificar que no haya conflicto con otras asesorías
            if (asesoriaServicio.existeConflicto(asesoria.getGrupo(), fecha, hora)) {
                redirect.addFlashAttribute("error", "Ya existe otra asesoría programada en esa fecha y hora.");
                return "redirect:/asesor/asesorias";
            }
            
            // Actualizar solo esta asesoría específica
            asesoria.setFecha(fecha);
            asesoria.setHora(hora);
            asesoria.setLink(link);
            asesoriaRepositorio.save(asesoria);
            
            redirect.addFlashAttribute("mensaje", "Asesoría actualizada correctamente.");
        } else {
            redirect.addFlashAttribute("error", "No se encontró la asesoría.");
        }
        return "redirect:/asesor/asesorias";
    }

    @PostMapping("/asesor/salirGrupo")
    @Transactional
    public String salirGrupo(@RequestParam("idGrupo") Long idGrupo, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return "redirect:/index";
        }

        try {
            // Verificar si el grupo existe
            Optional<Grupo> grupoOptional = grupoRepositorio.findById(idGrupo.intValue());
            if (grupoOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El grupo no existe");
                return "redirect:/asesor/grupo";
            }

            Grupo grupo = grupoOptional.get();
            
            // Verificar que el asesor es el asignado al grupo
            if (grupo.getAsesor() == null || !grupo.getAsesor().getIdUsuario().equals(asesor.getIdUsuario())) {
                redirectAttributes.addFlashAttribute("error", "No eres el asesor de este grupo");
                return "redirect:/asesor/grupo";
            }
            
            // Recopilar información de los aprendices para notificaciones
            List<String> correosAprendices = new ArrayList<>();
            List<Usuario> aprendicesGrupo = new ArrayList<>();
            
            if (grupo.getAprendices() != null) {
                for (GrupoAprendiz ga : grupo.getAprendices()) {
                    if (ga != null && ga.getUsuario() != null) {
                        Usuario aprendiz = ga.getUsuario();
                        aprendicesGrupo.add(aprendiz);
                        if (aprendiz.getCorreo() != null && !aprendiz.getCorreo().isEmpty()) {
                            correosAprendices.add(aprendiz.getCorreo());
                        }
                    }
                }
            }
            
            // 1. Eliminar todas las asesorías asociadas al grupo
            List<Asesoria> asesoriasGrupo = asesoriaServicio.obtenerAsesoriasPorGrupo(grupo.getIdGrupo());
            for (Asesoria asesoria : asesoriasGrupo) {
                try {
                    // Eliminar asistencias asociadas a la asesoria
                    List<Asistencia> asistencias = asistenciaServicio.obtenerAsistenciasPorAsesoria((long)asesoria.getId());
                    if (asistencias != null && !asistencias.isEmpty()) {
                        asistenciaServicio.eliminarAsistencias(asistencias);
                    }
                    // Ahora sí eliminar la asesoria
                    asesoriaServicio.eliminarAsesoria(asesoria.getId());
                } catch (Exception e) {
                    System.out.println("Error al eliminar asesoría o asistencias: " + e.getMessage());
                }
            }
            
            // 2. Eliminar todas las pruebas asociadas al grupo
            // Implementar si es necesario
            
            // 3. Desasignar el asesor del grupo
            grupo.setAsesor(null);
            grupoRepositorio.save(grupo);
            
            // 4. Enviar notificación a todos los aprendices del grupo
            if (!correosAprendices.isEmpty()) {
                try {
                    String asunto = "El asesor ha abandonado el grupo " + grupo.getNombre();
                    String mensaje = "Estimado(a) aprendiz,\n\n" +
                            "Te informamos que el asesor " + asesor.getNombres() + " " + asesor.getPrimerA() + 
                            " ha abandonado el grupo " + grupo.getNombre() + ".\n\n" +
                            "Las asesorías programadas han sido canceladas. " +
                            "El grupo estará disponible para que un nuevo asesor pueda tomar el relevo.\n\n" +
                            "Te mantendremos informado cuando un nuevo asesor sea asignado al grupo.\n\n" +
                            "Este mensaje es una notificación automática.";
                    
                    correoServicio.sendMassEmail(correosAprendices, asunto, mensaje);
                } catch (Exception e) {
                    System.out.println("Error al enviar notificaciones a los aprendices: " + e.getMessage());
                }
            }
            
            redirectAttributes.addFlashAttribute("mensaje", "Has abandonado el grupo exitosamente y se han eliminado las asesorías asociadas");
            return "redirect:/asesor/grupo";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            return "redirect:/asesor/grupo";
        }
    }

    @PostMapping("/asesor/asesoria/{id}/actualizar-fecha")
    public ResponseEntity<?> actualizarFechaAsesoria(
            @PathVariable("id") int id,
            @RequestParam("nuevaFecha") String nuevaFechaStr,
            @RequestParam("nuevaHora") String nuevaHoraStr,
            HttpSession session) {
        
        try {
            Usuario asesor = (Usuario) session.getAttribute("usuario");
            if (asesor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autorizado"));
            }
            
            // Obtener la asesoría
            Optional<Asesoria> optAsesoria = asesoriaRepositorio.findById(id);
            if (!optAsesoria.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Asesoría no encontrada"));
            }
            
            Asesoria asesoria = optAsesoria.get();
            
            // Verificar que el asesor es el correcto
            if (asesoria.getGrupo().getAsesor() == null || 
                !asesoria.getGrupo().getAsesor().getIdUsuario().equals(asesor.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permiso para modificar esta asesoría"));
            }
            
            // Guardar fecha y hora anteriores para el correo
            LocalDate fechaAnterior = asesoria.getFecha();
            LocalTime horaAnterior = asesoria.getHora();
            String fechaAnteriorStr = asesoria.getFechaFormateada();
            String horaAnteriorStr = asesoria.getHoraFormateada();
            
            // Convertir fecha y hora
            LocalDate nuevaFecha = nuevaFechaStr.isEmpty() ? asesoria.getFecha() : LocalDate.parse(nuevaFechaStr);
            LocalTime nuevaHora = nuevaHoraStr.isEmpty() ? asesoria.getHora() : LocalTime.parse(nuevaHoraStr);
            
            // Verificar si realmente hay cambios
            boolean cambioFecha = !nuevaFecha.equals(fechaAnterior);
            boolean cambioHora = !nuevaHora.equals(horaAnterior);
            
            if (!cambioFecha && !cambioHora) {
                return ResponseEntity.ok(Map.of(
                    "mensaje", "No se realizaron cambios",
                    "nuevaFecha", nuevaFecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    "nuevaHora", nuevaHora.format(DateTimeFormatter.ofPattern("HH:mm"))
                ));
            }
            
            // Actualizar la asesoría
            asesoria.setFecha(nuevaFecha);
            asesoria.setHora(nuevaHora);
            asesoriaRepositorio.save(asesoria);
            
            // Obtener los aprendices del grupo para enviar correos
            List<GrupoAprendiz> grupoAprendices = asesoria.getGrupo().getAprendices();
            List<String> correosAprendices = new ArrayList<>();
            
            for (GrupoAprendiz ga : grupoAprendices) {
                Usuario aprendiz = ga.getUsuario();
                if (aprendiz != null && aprendiz.getCorreo() != null) {
                    correosAprendices.add(aprendiz.getCorreo());
                }
            }
            
            // Enviar correo a los aprendices si hay cambios
            if (!correosAprendices.isEmpty()) {
                String asunto = "Cambio en asesoría - " + asesoria.getGrupo().getNombre();
                StringBuilder cuerpo = new StringBuilder();
                cuerpo.append("Hola, \n\n");
                cuerpo.append("Te informamos que el asesor ").append(asesor.getNombres()).append(" ").append(asesor.getPrimerA());
                cuerpo.append(" ha modificado una asesoría del grupo '").append(asesoria.getGrupo().getNombre()).append("'.\n\n");
                
                if (cambioFecha) {
                    cuerpo.append("Fecha anterior: ").append(fechaAnteriorStr).append("\n");
                    cuerpo.append("Nueva fecha: ").append(nuevaFecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
                }
                
                if (cambioHora) {
                    cuerpo.append("Hora anterior: ").append(horaAnteriorStr).append("\n");
                    cuerpo.append("Nueva hora: ").append(nuevaHora.format(DateTimeFormatter.ofPattern("HH:mm"))).append("\n");
                }
                
                cuerpo.append("\nGracias por usar KASH.");
                
                correoServicio.sendMassEmail(correosAprendices, asunto, cuerpo.toString());
            }
            
            return ResponseEntity.ok(Map.of(
                "mensaje", "Fecha/hora actualizada correctamente",
                "nuevaFecha", nuevaFecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                "nuevaHora", nuevaHora.format(DateTimeFormatter.ofPattern("HH:mm"))
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar la fecha: " + e.getMessage()));
        }
    }

    @GetMapping("/asesor/asesoria/{id}/datos")
    public ResponseEntity<?> obtenerDatosAsesoria(@PathVariable("id") int id, HttpSession session) {
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado"));
        }
        
        Optional<Asesoria> optAsesoria = asesoriaRepositorio.findById(id);
        if (!optAsesoria.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Asesoría no encontrada"));
        }
        
        Asesoria asesoria = optAsesoria.get();
        
        // Verificar que el asesor es el correcto
        if (asesoria.getGrupo().getAsesor() == null || 
            !asesoria.getGrupo().getAsesor().getIdUsuario().equals(asesor.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permiso para ver esta asesoría"));
        }
        
        // Devolver los datos de la asesoría
        return ResponseEntity.ok(Map.of(
            "id", asesoria.getId(),
            "fecha", asesoria.getFecha().toString(),
            "hora", asesoria.getHora().toString(),
            "fechaFormateada", asesoria.getFechaFormateada(),
            "horaFormateada", asesoria.getHoraFormateada()
        ));
    }
    
    @PostMapping("/asesor/asesoria/verificar-conflictos")
    public ResponseEntity<?> verificarConflictos(
            @RequestParam("asesoriaId") int asesoriaId,
            @RequestParam("nuevaFecha") String nuevaFechaStr,
            @RequestParam("nuevaHora") String nuevaHoraStr,
            HttpSession session) {
        
        try {
            Usuario asesor = (Usuario) session.getAttribute("usuario");
            if (asesor == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autorizado", "conflicto", true));
            }
            
            // Obtener la asesoría
            Optional<Asesoria> optAsesoria = asesoriaRepositorio.findById(asesoriaId);
            if (!optAsesoria.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Asesoría no encontrada", "conflicto", true));
            }
            
            Asesoria asesoria = optAsesoria.get();
            
            // Verificar que el asesor es el correcto
            if (asesoria.getGrupo().getAsesor() == null || 
                !asesoria.getGrupo().getAsesor().getIdUsuario().equals(asesor.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permiso para modificar esta asesoría", "conflicto", true));
            }
            
            // Convertir fecha y hora
            LocalDate nuevaFecha = LocalDate.parse(nuevaFechaStr);
            LocalTime nuevaHora = LocalTime.parse(nuevaHoraStr);
            
            // Verificar que la fecha no sea anterior a hoy
            LocalDate hoy = LocalDate.now();
            if (nuevaFecha.isBefore(hoy)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "La fecha no puede ser anterior a hoy.", "conflicto", true));
            }
            
            // Verificar que la hora no esté en el rango de 10 PM a 6 AM
            LocalTime horaInicio = LocalTime.of(22, 0); // 10 PM
            LocalTime horaFin = LocalTime.of(6, 0); // 6 AM
            
            if ((nuevaHora.isAfter(horaInicio) || nuevaHora.equals(horaInicio)) || 
                (nuevaHora.isBefore(horaFin) || nuevaHora.equals(horaFin))) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", "No se pueden programar asesorías entre las 10:00 PM y las 6:00 AM.", "conflicto", true));
            }
            
            // Verificar conflictos con otras asesorías (excluyendo la actual)
            boolean hayConflicto = false;
            String mensajeConflicto = "";
            
            // Verificar si hay conflicto con otra asesoría del mismo grupo
            List<Asesoria> asesoriasGrupo = asesoriaRepositorio.findByGrupo(asesoria.getGrupo());
            for (Asesoria a : asesoriasGrupo) {
                if (a.getId() != asesoria.getId() && // No es la misma asesoría
                    a.getFecha().equals(nuevaFecha) && a.getHora().equals(nuevaHora)) {
                    hayConflicto = true;
                    mensajeConflicto = "Ya existe otra asesoría para este grupo en la misma fecha y hora.";
                    break;
                }
            }
            
            // Verificar si hay conflicto con otra asesoría del mismo asesor en otro grupo
            if (!hayConflicto) {
                List<Asesoria> asesoriasAsesor = asesoriaRepositorio.findByGrupo_Asesor_IdUsuario(asesor.getIdUsuario());
                for (Asesoria a : asesoriasAsesor) {
                    if (a.getId() != asesoria.getId() && // No es la misma asesoría
                        a.getFecha().equals(nuevaFecha) && a.getHora().equals(nuevaHora)) {
                        hayConflicto = true;
                        mensajeConflicto = "Ya tienes otra asesoría programada en la misma fecha y hora.";
                        break;
                    }
                }
            }
            
            if (hayConflicto) {
                return ResponseEntity.badRequest()
                        .body(Map.of("mensaje", mensajeConflicto, "conflicto", true));
            }
            
            // Si no hay conflictos
            return ResponseEntity.ok(Map.of("mensaje", "No hay conflictos", "conflicto", false));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al verificar conflictos: " + e.getMessage(), "conflicto", true));
        }
    }

    // Endpoint REST para obtener pruebas por grupo
    @GetMapping("/api/asesor/pruebas/grupo/{grupoId}")
    @ResponseBody
    public Map<String, Object> obtenerPruebasPorGrupo(@PathVariable int grupoId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            response.put("error", "Sesión no válida");
            return response;
        }

        try {
            // Obtener todas las pruebas del grupo - convertir int a Long
            List<Prueba> pruebas = pruebaRepositorio.findByGrupoId((long) grupoId);
            
            // Convertir a formato JSON amigable
            List<Map<String, Object>> pruebasJSON = new ArrayList<>();
            for (Prueba prueba : pruebas) {
                Map<String, Object> pruebaMap = new HashMap<>();
                pruebaMap.put("id", prueba.getId());
                pruebaMap.put("tema", prueba.getTema());
                pruebaMap.put("fechaInicio", prueba.getFechaInicio() != null ? prueba.getFechaInicio().toString() : null);
                pruebaMap.put("fechaFin", prueba.getFechaFin() != null ? prueba.getFechaFin().toString() : null);
                pruebasJSON.add(pruebaMap);
            }
            
            response.put("success", true);
            response.put("pruebas", pruebasJSON);
            response.put("total", pruebasJSON.size());
            
        } catch (Exception e) {
            response.put("error", "Error al obtener las pruebas: " + e.getMessage());
        }
        
        return response;
    }

    // Endpoint REST para obtener resultados de una prueba específica
    @GetMapping("/api/asesor/pruebas/{pruebaId}/resultados")
    @ResponseBody
    public Map<String, Object> obtenerResultadosPrueba(@PathVariable Long pruebaId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Usuario asesor = (Usuario) session.getAttribute("usuario");
        if (asesor == null) {
            response.put("error", "Sesión no válida");
            return response;
        }

        try {
            // Obtener la prueba
            Prueba prueba = pruebaRepositorio.findById(pruebaId).orElse(null);
            if (prueba == null) {
                response.put("error", "Prueba no encontrada");
                return response;
            }

            // Obtener todas las respuestas de esta prueba
            List<RespuestaSeleccionada> todasLasRespuestas = respuestaSeleccionadaRepositorio.findByPrueba(prueba);
            
            // Agrupar por aprendiz y obtener calificaciones
            Map<String, Object> aprendicesConResultados = new HashMap<>();
            
            for (RespuestaSeleccionada respuesta : todasLasRespuestas) {
                Usuario aprendiz = respuesta.getAprendiz();
                String keyAprendiz = aprendiz.getIdUsuario().toString();
                
                if (!aprendicesConResultados.containsKey(keyAprendiz)) {
                    Map<String, Object> datosAprendiz = new HashMap<>();
                    datosAprendiz.put("id", aprendiz.getIdUsuario());
                    datosAprendiz.put("nombre", aprendiz.getNombres() + " " + aprendiz.getPrimerA() + " " + aprendiz.getSegundoA());
                    datosAprendiz.put("estado", "Presentado");
                    
                    // Obtener calificación
                    Optional<ResultadoPrueba> resultadoOpt = resultadoPruebaRepositorio.findByAprendizIdAndPruebaId(aprendiz.getIdUsuario(), pruebaId);
                    if (resultadoOpt.isPresent()) {
                        datosAprendiz.put("calificacion", resultadoOpt.get().getPuntuacion());
                    } else {
                        datosAprendiz.put("calificacion", 0.0);
                    }
                    
                    aprendicesConResultados.put(keyAprendiz, datosAprendiz);
                }
            }
            
            response.put("success", true);
            response.put("prueba", Map.of(
                "id", prueba.getId(),
                "tema", prueba.getTema() != null ? prueba.getTema() : "Sin título"
            ));
            response.put("aprendices", new ArrayList<>(aprendicesConResultados.values()));
            response.put("total", aprendicesConResultados.size());
            
        } catch (Exception e) {
            response.put("error", "Error al obtener los resultados: " + e.getMessage());
        }
        
        return response;
    }

}

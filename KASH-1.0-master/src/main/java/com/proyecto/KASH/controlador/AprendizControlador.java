package com.proyecto.KASH.controlador;

import static com.itextpdf.text.html.HtmlTags.A;
import static com.itextpdf.text.pdf.Barcode128.Barcode128CodeSet.A;
import com.proyecto.KASH.Repository.AprendizAsistenciaRepositorio;
import com.proyecto.KASH.Repository.GrupoRepositorio;
import com.proyecto.KASH.Repository.ResultadoPruebaRepositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Asistencia;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.ResultadoPrueba;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.fotos;
import com.proyecto.KASH.servicio.AprendizAsesoriaServicio;
import com.proyecto.KASH.servicio.AprendizAsistenciaServicio;
import com.proyecto.KASH.servicio.AprendizComponenteServicio;
import com.proyecto.KASH.servicio.AprendizPruebaServicio;
import com.proyecto.KASH.servicio.AsesorAsistenciaServicio;
import com.proyecto.KASH.servicio.FotoServicio;
import com.proyecto.KASH.servicio.GrupoAprendizServicio;
import com.proyecto.KASH.servicio.GrupoServicio;
import com.proyecto.KASH.servicio.ResultadoPruebaServicio;
import com.proyecto.KASH.servicio.UsuarioServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import static javax.swing.text.html.HTML.Tag.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.format.DateTimeFormatter;
import com.proyecto.KASH.servicio.EmailService;
import com.proyecto.KASH.Repository.PreguntaRepositorio;
import com.proyecto.KASH.Repository.RespuestaRepositorio;
import com.proyecto.KASH.entidad.Pregunta;
import com.proyecto.KASH.entidad.Respuesta;

@Controller
public class AprendizControlador {

    //Perfil
    @Autowired
    private FotoServicio fotoServicio;  // Inyectamos la interfaz FotoServicio

    @Autowired
    private GrupoServicio grupoServicio;

    @GetMapping("/fotoAprendiz/{id}")
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

    @PostMapping("/aprendiz/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto,
            @RequestParam("redirectUrl") String redirectUrl,
            HttpSession session, Model model) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null) {
            return "redirect:/index";
        }

        if (!foto.isEmpty()) {
            try {
                fotos fotoExistente = fotoServicio.obtenerFotoPorIdUsuario(aprendiz.getIdUsuario());

                if (fotoExistente != null) {
                    fotoServicio.actualizarFoto(foto, aprendiz.getIdUsuario());
                    model.addAttribute("mensaje", "Foto actualizada exitosamente");
                } else {
                    fotoServicio.guardarFoto(foto, aprendiz.getIdUsuario());
                    model.addAttribute("mensaje", "Foto subida exitosamente");
                }
            } catch (IOException e) {
                model.addAttribute("error", "Error al subir la foto");
            }
        }

        // Redirige a la misma URL de donde se subió la foto
        return "redirect:" + redirectUrl;
    }

    //Grupos
    @Autowired
    private GrupoAprendizServicio grupoAprendizServicio;

    @Autowired
    private GrupoRepositorio grupoRepositorio;

    @Autowired
    private EmailService correoServicio;

    @GetMapping("/aprendiz/grupo")
    public String mostrarGruposDelAprendiz(HttpSession session, Model model, HttpServletRequest request) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            return "redirect:/index";
        }

        Long idAprendiz = aprendiz.getIdUsuario();
        String programaAprendiz = aprendiz.getPrograma();

        // 1. Grupos actuales del aprendiz
        List<Grupo> gruposDelAprendiz = grupoAprendizServicio.obtenerGruposPorAprendiz(idAprendiz);

        // 2. Asesorías activas
        List<Asesoria> asesoriasActivas = asesoriaServicio.obtenerAsesoriasActivas();
        Set<Integer> idsGruposConAsesoria = asesoriasActivas.stream()
                .map(Asesoria::getGrupo)
                .filter(Objects::nonNull)
                .map(Grupo::getIdGrupo)
                .collect(Collectors.toSet());

        // 3. Grupos disponibles - Mostrar un solo grupo por componente
        // y filtrar los que tienen más de 5 estudiantes
        List<Grupo> todosGruposDisponibles = grupoRepositorio.findGruposDisponiblesParaAprendiz(idAprendiz);
        Map<String, Grupo> gruposPorComponente = new HashMap<>();

        // Filtrar por nombre de componente, número de estudiantes y programa del aprendiz
        for (Grupo grupo : todosGruposDisponibles) {
            String nombreComponente = grupo.getNombre(); // El nombre del grupo es el nombre del componente

            // Verificar si el componente pertenece al programa del aprendiz
            boolean perteneceAlPrograma = aprendizComponenteServicio.componentePerteneceAPrograma(nombreComponente, programaAprendiz);

            // Solo considerar grupos con menos de 5 estudiantes y que pertenezcan al programa del aprendiz
            if (perteneceAlPrograma && grupo.getAprendices() != null && grupo.getAprendices().size() < 5) {
                // Solo añadir si no hay un grupo para este componente o si este no tiene asesorías activas
                if (!gruposPorComponente.containsKey(nombreComponente)
                        || (!idsGruposConAsesoria.contains(grupo.getIdGrupo())
                        && idsGruposConAsesoria.contains(gruposPorComponente.get(nombreComponente).getIdGrupo()))) {
                    gruposPorComponente.put(nombreComponente, grupo);
                }
            }
        }

        List<Grupo> gruposDisponiblesFiltrados = new ArrayList<>(gruposPorComponente.values());

        // Enviar al modelo
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("grupos", gruposDelAprendiz);
        model.addAttribute("gruposConAsesoriaActiva", idsGruposConAsesoria);
        model.addAttribute("gruposDisponibles", gruposDisponiblesFiltrados);
        model.addAttribute("redirectUrl", request.getRequestURI());
        model.addAttribute("nombre", aprendiz.getNombres());

        // Configurar ayuda para esta vista
        configurarAyuda(model, request);

        return "aprendiz/grupo";
    }

    @GetMapping("/aprendiz/grupos/disponibles")
    public String mostrarGruposDisponibles(
            @RequestParam(required = false) String filtro,
            HttpSession session,
            Model model, HttpServletRequest request) {

        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            return "redirect:/index";
        }

        Long idAprendiz = aprendiz.getIdUsuario();

        // Buscar grupos que el aprendiz no tenga y que no tengan el mismo nombre que los grupos a los que ya pertenece
        List<Grupo> gruposDisponibles = grupoRepositorio.findGruposNoAsignadosYSinNombreDuplicado(idAprendiz);

        // Aplicar filtro si existe
        if (filtro != null && !filtro.isEmpty()) {
            String filtroLower = filtro.toLowerCase();
            gruposDisponibles = gruposDisponibles.stream()
                    .filter(g -> g.getNombre().toLowerCase().contains(filtroLower)
                    || (g.getAsesor() != null && g.getAsesor().getNombres().toLowerCase().contains(filtroLower)))
                    .collect(Collectors.toList());
        }

        // Asesorías activas (si la vista necesita marcar algo)
        List<Asesoria> asesoriasActivas = asesoriaServicio.obtenerAsesoriasActivas();
        Set<Integer> idsGruposConAsesoria = asesoriasActivas.stream()
                .map(Asesoria::getGrupo)
                .filter(Objects::nonNull)
                .map(Grupo::getIdGrupo)
                .collect(Collectors.toSet());

        // Enviar al modelo SOLO lo necesario
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("gruposConAsesoriaActiva", idsGruposConAsesoria);
        model.addAttribute("gruposDisponibles", gruposDisponibles);
        model.addAttribute("filtro", filtro);
        model.addAttribute("redirectUrl", request.getRequestURI());

        // Configurar ayuda para esta vista
        configurarAyuda(model, request);

        return "aprendiz/grupo";
    }

    // Eliminamos la referencia a JdbcTemplate ya que usaremos JPA
    @PostMapping("/aprendiz/entrarGrupo")
    public String entrarGrupo(@RequestParam("idGrupo") Long idGrupo, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            return "redirect:/index";
        }

        Long idAprendiz = aprendiz.getIdUsuario();

        // Verificar si el aprendiz ya está en el grupo
        if (grupoAprendizServicio.existeAprendizEnGrupo(idAprendiz, idGrupo.intValue())) {
            redirectAttributes.addFlashAttribute("error", "Ya estás inscrito en este grupo.");
            return "redirect:/aprendiz/grupo";
        }

        // Verificar límite de grupos activos para aprendiz (máximo 5)
        List<Grupo> gruposActivos = grupoAprendizServicio.obtenerGruposPorAprendiz(idAprendiz);
        if (gruposActivos.size() >= 5) {
            redirectAttributes.addFlashAttribute("error", "No puedes unirte a más de 5 grupos activos.");
            return "redirect:/aprendiz/grupo";
        }

        // Obtener el grupo
        Optional<Grupo> grupoOpt = grupoServicio.buscarPorId(idGrupo);
        if (grupoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El grupo no existe.");
            return "redirect:/aprendiz/grupo";
        }

        Grupo grupo = grupoOpt.get();

        // Verificar si el grupo está lleno (5 o más aprendices)
        if (grupo.getAprendices() != null && grupo.getAprendices().size() >= 5) {
            redirectAttributes.addFlashAttribute("error", "El grupo está lleno.");
            return "redirect:/aprendiz/grupo";
        }

        // Registrar al aprendiz en el grupo
        grupoAprendizServicio.registrarAprendizEnGrupo(idAprendiz, idGrupo.intValue());

        // Si el grupo ahora está lleno, crear un nuevo grupo para este componente
        if (grupo.getAprendices() != null && grupo.getAprendices().size() >= 5) {
            grupoServicio.crearGrupoSiNecesario(grupo.getNombre());
        }

        redirectAttributes.addFlashAttribute("mensaje", "Te has unido al grupo exitosamente.");
        return "redirect:/aprendiz/grupo";
    }

    //Asistencia
    @Autowired
    private AprendizAsistenciaServicio asistenciaServicio;

    @Autowired
    private AprendizAsistenciaRepositorio asistenciaRepositorio;

    @RequestMapping("/aprendiz/asistencia")
    public String Aprendiz4(@RequestParam(name = "fecha", required = false) String fechaStr,
            Model model, HttpSession session, HttpServletRequest request) {

        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        String nombre = aprendiz.getNombres(); // Agregar el nombre

        // Formatear fechas según localización
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Establecer la fecha actual si no se proporciona
        LocalDate fecha = (fechaStr != null && !fechaStr.isEmpty())
                ? LocalDate.parse(fechaStr, formato)
                : LocalDate.now();

        // Obtener asistencias para el aprendiz y la fecha especificada
        List<Asistencia> asistencias = asistenciaServicio.getAsistenciaByFechaAndAprendiz(fecha, aprendiz.getIdUsuario());

        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("nombre", nombre); // Agregar el nombre al modelo
        model.addAttribute("asistencias", asistencias);
        model.addAttribute("fecha", fecha.format(formato));
        model.addAttribute("redirectUrl", request.getRequestURI());

        // Configurar ayuda para esta vista
        configurarAyuda(model, request);

        return "aprendiz/asistencia";
    }

    //asesorias
    @Autowired
    private AprendizAsesoriaServicio asesoriaServicio;

    @RequestMapping("/aprendiz/asesorias")
    public String Aprendiz5(Model model, HttpSession session, HttpServletRequest request) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");

        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            return "redirect:/index";
        }

        Long idAprendiz = aprendiz.getIdUsuario();
        String nombre = aprendiz.getNombres();
        List<Asesoria> todasAsesorias = asesoriaServicio.obtenerAsesoriasPorAprendiz(idAprendiz);

        Map<Integer, Asesoria> asesoriasPorGrupo = new HashMap<>();
        LocalDate hoy = LocalDate.now();

        for (Asesoria a : todasAsesorias) {
            if (a.getFecha() == null) {
                continue;
            }

            int idGrupo = a.getGrupo().getId();
            Asesoria actual = asesoriasPorGrupo.get(idGrupo);

            // Si no hay una asesoría registrada aún para este grupo
            if (actual == null) {
                asesoriasPorGrupo.put(idGrupo, a);
            } else {
                // Elegir la asesoría más cercana a hoy (en adelante si existe, si no la más reciente)
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

            // Primero las asesorías futuras (o de hoy), luego las pasadas
            if (a1EsFutura && !a2EsFutura) {
                return -1;
            }
            if (!a1EsFutura && a2EsFutura) {
                return 1;
            }

            // Dentro de cada grupo, ordenarlas por fecha ascendente
            return a1.getFecha().compareTo(a2.getFecha());
        });

        Asesoria asesoriaProxima = asesoriasFiltradas.stream()
                .filter(a -> !a.getFecha().isBefore(hoy)) // Solo futuras o de hoy
                .min(Comparator.comparing(Asesoria::getFecha)) // La más cercana
                .orElse(null);

        if (asesoriaProxima != null) {
            model.addAttribute("idAsesoriaProxima", asesoriaProxima.getId());
            model.addAttribute("fechaProximaAsesoria", asesoriaProxima.getFecha());
        }

        model.addAttribute("asesorias", asesoriasFiltradas);
        model.addAttribute("nombre", nombre);

        // Contadores por grupo único
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

        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("redirectUrl", request.getRequestURI());

        // Configurar ayuda para esta vista
        configurarAyuda(model, request);

        return "aprendiz/asesorias";
    }

    //Componente
    @Autowired
    private AprendizComponenteServicio aprendizComponenteServicio;

    @GetMapping("/aprendiz/componente")
    public String componente(
            Model model,
            @RequestParam(name = "buscar", required = false) String buscar,
            HttpSession session,
            HttpServletRequest request) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"aprendiz".equalsIgnoreCase(usuario.getRolSeleccionado())) {
            return "redirect:/index";
        }

        // Obtener el programa del aprendiz
        String programaAprendiz = usuario.getPrograma();

        List<Componente> componentesDisponibles;

        // Filtrar componentes por programa del aprendiz
        if (programaAprendiz != null && !programaAprendiz.isEmpty()) {
            componentesDisponibles = aprendizComponenteServicio.findByPrograma(programaAprendiz);
        } else {
            componentesDisponibles = aprendizComponenteServicio.findAll();
        }

        // Aplicar búsqueda si existe
        if (buscar != null && !buscar.isEmpty()) {
            List<Componente> componentesFiltrados = new ArrayList<>();
            for (Componente componente : componentesDisponibles) {
                if (componente.getNombre().toLowerCase().contains(buscar.toLowerCase())) {
                    componentesFiltrados.add(componente);
                }
            }
            componentesDisponibles = componentesFiltrados;
        }

        // Obtener los nombres de los componentes disponibles
        List<String> nombresComponentes = componentesDisponibles.stream()
                .map(Componente::getNombre)
                .collect(Collectors.toList());

        // Obtener los componentes ya elegidos por el aprendiz
        List<Componente> componentesElegidos = aprendizComponenteServicio.findComponentesByAprendizId(usuario.getIdUsuario());

        model.addAttribute("componentesDisponibles", nombresComponentes);
        model.addAttribute("componentesElegidos", componentesElegidos);
        model.addAttribute("aprendiz", usuario);
        model.addAttribute("nombre", usuario.getNombres());
        model.addAttribute("buscar", buscar);
        model.addAttribute("redirectUrl", "/aprendiz/componente");

        // Configurar ayuda para esta vista
        configurarAyuda(model, request);

        return "aprendiz/componente";
    }

    @PostMapping("/aprendiz/componente")
    public String registrarComponente(
            @RequestParam String componente,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"aprendiz".equalsIgnoreCase(usuario.getRolSeleccionado())) {
            return "redirect:/index";
        }

        try {
            // Verificar si el componente pertenece al programa del aprendiz
            String programaAprendiz = usuario.getPrograma();
            if (programaAprendiz != null && !programaAprendiz.isEmpty()) {
                boolean pertenece = aprendizComponenteServicio.componentePerteneceAPrograma(componente, programaAprendiz);
                if (!pertenece) {
                    redirectAttributes.addFlashAttribute("error", "El componente seleccionado no pertenece a tu programa de formación.");
                    return "redirect:/aprendiz/componente";
                }
            }

            // Registrar el componente para el aprendiz
            aprendizComponenteServicio.registrarComponenteParaAprendiz(usuario.getIdUsuario(), componente);
            redirectAttributes.addFlashAttribute("mensaje", "Componente registrado exitosamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el componente: " + e.getMessage());
        }

        return "redirect:/aprendiz/componente";
    }

    @PostMapping("/aprendiz/eliminarComponente")
    public String eliminarComponente(
            @RequestParam Integer id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"aprendiz".equalsIgnoreCase(usuario.getRolSeleccionado())) {
            return "redirect:/index";
        }

        try {
            aprendizComponenteServicio.eliminarComponentePorId(id);
            redirectAttributes.addFlashAttribute("mensaje", "Componente eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el componente: " + e.getMessage());
        }

        return "redirect:/aprendiz/componente";
    }

    //Prueba
    @Autowired
    private AprendizPruebaServicio pruebaServicio;

    @Autowired
    private ResultadoPruebaServicio resultadoPruebaServicio;

    @Autowired
    private ResultadoPruebaRepositorio resultadoPruebaRepo;

    @Autowired
    private PreguntaRepositorio preguntaRepositorio;

    @Autowired
    private RespuestaRepositorio respuestaRepositorio;

    @RequestMapping("/aprendiz/prueba")
    public String Aprendiz7(HttpSession session, Model model, HttpServletRequest request) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            return "redirect:/index"; // Redirige si no hay sesión
        }
        
        List<Prueba> pruebas = pruebaServicio.obtenerPruebasPorAprendiz(aprendiz.getIdUsuario());
        
        // Cargar preguntas y respuestas para cada prueba
        for (Prueba prueba : pruebas) {
            List<Pregunta> preguntas = preguntaRepositorio.findByPruebaId(prueba.getId());
            for (Pregunta pregunta : preguntas) {
                List<Respuesta> respuestas = respuestaRepositorio.findByPreguntaId(pregunta.getId());
                pregunta.setRespuestas(respuestas);
            }
            prueba.setPreguntas(preguntas);
        }
        
        model.addAttribute("pruebas", pruebas);

        Long idAprendiz = aprendiz.getIdUsuario();
        String nombre = aprendiz.getNombres(); // Agregar el nombre

        // Obtener todos los grupos del aprendiz
        List<Grupo> todosGrupos = grupoAprendizServicio.obtenerGruposPorAprendiz(idAprendiz);

        // Mapa para guardar resultados de las pruebas respondidas
        Map<Long, Double> resultados = new HashMap<>();

        for (Prueba prueba : pruebas) {
            Optional<ResultadoPrueba> resultadoOpt = resultadoPruebaServicio.obtenerResultadoPorAprendizYPrueba(idAprendiz, prueba.getId());
            resultadoOpt.ifPresent(resultado -> resultados.put(prueba.getId(), resultado.getPuntuacion()));
        }

        model.addAttribute("resultados", resultados);
        model.addAttribute("nombre", nombre); // Agregar el nombre al modelo
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("grupos", todosGrupos);
        model.addAttribute("redirectUrl", request.getRequestURI());

        // Configurar ayuda para esta vista
        configurarAyuda(model, request);

        return "aprendiz/prueba";
    }

    @PostMapping("/resolverPrueba/{id}")
    public String procesarRespuestas(@PathVariable Long id,
            @RequestParam Map<String, String> params,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");

        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            return "redirect:/index";
        }

        Map<Long, Long> respuestasSeleccionadas = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("pregunta_")) {
                Long idPregunta = Long.parseLong(entry.getKey().replace("pregunta_", ""));
                Long idRespuesta = Long.parseLong(entry.getValue());
                respuestasSeleccionadas.put(idPregunta, idRespuesta);
            }
        }

        Optional<ResultadoPrueba> resultadoOpt = resultadoPruebaRepo.findByAprendizIdAndPruebaId(aprendiz.getIdUsuario(), id);

        double puntuacion;
        if (resultadoOpt.isPresent()) {
            puntuacion = pruebaServicio.actualizarPrueba(aprendiz.getIdUsuario(), id, respuestasSeleccionadas, resultadoOpt.get());
            redirectAttributes.addFlashAttribute("mensaje", "Prueba actualizada correctamente. Tu puntuación es: " + puntuacion);
        } else {
            puntuacion = pruebaServicio.resolverPrueba(aprendiz.getIdUsuario(), id, respuestasSeleccionadas);
            redirectAttributes.addFlashAttribute("mensaje", "Prueba completada correctamente. Tu puntuación es: " + puntuacion);
        }

        return "redirect:/aprendiz/prueba";
    }

    @GetMapping("/aprendiz/grupo/{idGrupo}/asesorias")
    @ResponseBody
    public Map<String, Object> obtenerAsesoriasGrupo(@PathVariable int idGrupo, HttpSession session) {
        Map<String, Object> respuesta = new HashMap<>();

        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            respuesta.put("error", "Sesión no válida");
            respuesta.put("asesorias", Collections.emptyList());
            return respuesta;
        }

        Grupo grupo = grupoServicio.obtenerGrupoPorId(idGrupo);
        if (grupo == null) {
            respuesta.put("error", "Grupo no encontrado");
            respuesta.put("asesorias", Collections.emptyList());
            return respuesta;
        }

        // Verificar que el aprendiz pertenece a este grupo
        boolean perteneceAlGrupo = grupoAprendizServicio.verificarAprendizEnGrupo(aprendiz.getIdUsuario(), idGrupo);
        if (!perteneceAlGrupo) {
            respuesta.put("error", "No tienes acceso a este grupo");
            respuesta.put("asesorias", Collections.emptyList());
            return respuesta;
        }

        // Añadir el nombre del grupo a la respuesta
        respuesta.put("nombreGrupo", grupo.getNombre());

        List<Asesoria> asesorias = asesoriaServicio.obtenerAsesoriasPorGrupo(idGrupo);

        // Convertir a formato JSON amigable
        List<Map<String, Object>> asesoriasMap = new ArrayList<>();
        for (Asesoria asesoria : asesorias) {
            Map<String, Object> asesoriaMap = new HashMap<>();
            asesoriaMap.put("id", asesoria.getId());
            asesoriaMap.put("fecha", asesoria.getFecha().toString());
            asesoriaMap.put("fechaFormateada", asesoria.getFechaFormateada());
            asesoriaMap.put("horaFormateada", asesoria.getHoraFormateada());
            asesoriaMap.put("estado", asesoria.getEstado());
            asesoriaMap.put("completada", asesoria.isCompletada());
            asesoriaMap.put("link", asesoria.getLink());
            asesoriasMap.add(asesoriaMap);
        }

        respuesta.put("asesorias", asesoriasMap);
        return respuesta;
    }

    @PostMapping("/aprendiz/salirGrupo")
    public String salirGrupo(@RequestParam("idGrupo") Long idGrupo, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario aprendiz = (Usuario) session.getAttribute("usuario");
        if (aprendiz == null || !"aprendiz".equalsIgnoreCase(aprendiz.getRolSeleccionado())) {
            System.out.println("[salirGrupo] No hay usuario en sesión.");
            return "redirect:/index";
        }

        try {
            System.out.println("[salirGrupo] Aprendiz que intenta salir del grupo: " + aprendiz.getIdUsuario());
            System.out.println("[salirGrupo] ID del grupo: " + idGrupo);

            // Verificar si el grupo existe
            Optional<Grupo> grupoOptional = grupoServicio.buscarPorId(idGrupo);
            if (grupoOptional.isEmpty()) {
                System.out.println("[salirGrupo] Grupo no encontrado con ID: " + idGrupo);
                redirectAttributes.addFlashAttribute("error", "El grupo no existe");
                return "redirect:/aprendiz/grupo";
            }

            Grupo grupo = grupoOptional.get();

            // Eliminar al aprendiz del grupo
            boolean eliminado = grupoAprendizServicio.eliminarAprendizDeGrupo(idGrupo, aprendiz.getIdUsuario());
            System.out.println("[salirGrupo] Resultado de eliminarAprendizDeGrupo: " + eliminado);
            if (eliminado) {
                System.out.println("[salirGrupo] Aprendiz eliminado del grupo correctamente.");

                // Notificar al asesor si hay uno asignado
                if (grupo.getAsesor() != null) {
                    try {
                        String asunto = "Un aprendiz ha abandonado tu grupo";
                        String mensaje = "Hola " + grupo.getAsesor().getNombres() + ",\n\n"
                                + "El aprendiz " + aprendiz.getNombres() + " " + aprendiz.getPrimerA() + " ha abandonado el grupo " + grupo.getNombre() + ".\n\n"
                                + "Información del aprendiz:\n"
                                + "- Nombre: " + aprendiz.getNombres() + " " + aprendiz.getPrimerA() + "\n"
                                + "- Correo: " + aprendiz.getCorreo() + "\n"
                                + "- Trimestre: " + aprendiz.getTrimestre() + "\n\n"
                                + "Este mensaje es una notificación automática.";

                        correoServicio.sendEmail(grupo.getAsesor().getCorreo(), asunto, mensaje);
                        System.out.println("[salirGrupo] Correo enviado al asesor: " + grupo.getAsesor().getCorreo());
                    } catch (Exception e) {
                        System.out.println("[salirGrupo] Error al enviar notificación al asesor:");
                        e.printStackTrace();
                    }
                }

                redirectAttributes.addFlashAttribute("mensaje", "Has abandonado el grupo exitosamente");
            } else {
                System.out.println("[salirGrupo] No se pudo eliminar al aprendiz del grupo.");
                redirectAttributes.addFlashAttribute("error", "No se pudo salir del grupo");
            }

            return "redirect:/aprendiz/grupo";

        } catch (Exception e) {
            System.out.println("[salirGrupo] Error general al intentar salir del grupo:");
            e.printStackTrace(); // <---- Esto imprimirá el error completo en la consola
            redirectAttributes.addFlashAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            return "redirect:/aprendiz/grupo";
        }
    }

    private void configurarAyuda(Model model, HttpServletRequest request) {
        // Obtener la ruta actual para determinar qué página es
        String path = request.getRequestURI();

        if (path.contains("/aprendiz/asesorias")) {
            model.addAttribute("helpTitle", "Ayuda - Asesorías");
            model.addAttribute("helpItems", new String[][]{
                {"Calendario", "Visualice todas las asesorías programadas."},
                {"Enlaces", "Acceda directamente a las reuniones de asesoría desde esta vista."},
                {"Asistencia", "Podrá ver sus registros de asistencia."}
            });
            model.addAttribute("helpFooter", "Recuerde unirse a tiempo a las asesorías programadas.");
        } else if (path.contains("/aprendiz/asistencia")) {
            model.addAttribute("helpTitle", "Ayuda - Asistencia");
            model.addAttribute("helpItems", new String[][]{
                {"Registro", "Revise su historial de asistencia a asesorías."},
                {"Estado", "Verifique su estado (presente o ausente) para cada asesoría."}
            });
            model.addAttribute("helpFooter", "La asistencia a las asesorías es importante para su proceso formativo.");
        } else if (path.contains("/aprendiz/grupo")) {
            model.addAttribute("helpTitle", "Ayuda - Grupos");
            model.addAttribute("helpItems", new String[][]{
                {"Inscripción", "Puede inscribirse a los grupos disponibles."},
                {"Información", "Conozca detalles sobre el grupo y su asesor."}
            });
            model.addAttribute("helpFooter", "Seleccione el grupo que más se adapte a sus necesidades de formación.");
        } else if (path.contains("/aprendiz/prueba")) {
            model.addAttribute("helpTitle", "Ayuda - Pruebas");
            model.addAttribute("helpItems", new String[][]{
                {"Evaluaciones", "Visualice las pruebas asignadas por el asesor."},
                {"Respuestas", "Responda las pruebas dentro del tiempo establecido."},
                {"Resultados", "Consulte sus calificaciones una vez evaluado."}
            });
            model.addAttribute("helpFooter", "Asegúrese de completar todas las pruebas antes de la fecha límite.");
        } else if (path.contains("/aprendiz/componente")) {
            model.addAttribute("helpTitle", "Ayuda - Componentes");
            model.addAttribute("helpItems", new String[][]{
                {"Consulta", "Revise los componentes formativos disponibles."},
                {"Contenidos", "Explore los contenidos de cada componente."}
            });
            model.addAttribute("helpFooter", "Los componentes formativos son fundamentales para su proceso de aprendizaje.");
        }
    }

}

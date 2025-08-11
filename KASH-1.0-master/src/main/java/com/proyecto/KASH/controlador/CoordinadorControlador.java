package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.foroRepositorio;
import com.proyecto.KASH.entidad.Comentario;
import com.proyecto.KASH.entidad.PQRS;
import com.proyecto.KASH.entidad.Programa;
import com.proyecto.KASH.entidad.Usuario;
import com.proyecto.KASH.entidad.foro;
import com.proyecto.KASH.entidad.fotos;
import com.proyecto.KASH.servicio.ComentarioServicio;
import com.proyecto.KASH.servicio.FotoServicio;
import com.proyecto.KASH.servicio.PQRSServicioImpl;
import com.proyecto.KASH.servicio.UsuarioServicio2;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.servicio.AsesoriaServicio;
import com.proyecto.KASH.Repository.AsesoriaRepositorio;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.servicio.GrupoServicio;
import com.proyecto.KASH.servicio.AprendizComponenteServicio;
import java.util.Objects;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.servicio.ProgramaServicio;
import java.util.ArrayList;
import java.util.Optional;
import com.proyecto.KASH.servicio.CorreoServicio;
import com.proyecto.KASH.entidad.Asistencia;
import com.proyecto.KASH.Repository.AprendizAsistenciaRepositorio;
import com.proyecto.KASH.Repository.PQRSRepository;
import com.proyecto.KASH.Repository.RolRepository;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.EmailUtil;
import com.proyecto.KASH.entidad.EstadoPQRS;
import com.proyecto.KASH.entidad.GrupoAprendiz;

@Controller
public class CoordinadorControlador {

    @Autowired
    private FotoServicio fotoServicio;

    @Autowired
    private ComentarioServicio comentarioServicio;

    @Autowired
    private PQRSServicioImpl pqrsServicio;

    @Autowired
    private foroRepositorio foroRepositorio;
    
    @Autowired 
    private UsuarioServicio2 usuarioServicio;

    @Autowired
    private AsesoriaServicio asesoriaServicio;
    
    @Autowired
    private AsesoriaRepositorio asesoriaRepositorio;

    @Autowired
    private GrupoServicio grupoServicio;

    @Autowired
    private AprendizComponenteServicio componenteServicio;

    @Autowired
    private ProgramaServicio programaServicio;

    @Autowired
    private CorreoServicio correoServicio;

    @Autowired
    private AprendizAsistenciaRepositorio asistenciaRepositorio;

    //vista coordinador perfil
    @RequestMapping("/coordinador")
    public String Coordinador(HttpSession session, Model model) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null || !"coordinador".equalsIgnoreCase(coordinador.getRolSeleccionado())) {
            return "redirect:/index";
        }

        fotos foto = fotoServicio.obtenerFotoPorIdUsuario(coordinador.getIdUsuario());
        model.addAttribute("foto", foto);
        model.addAttribute("coordinador", coordinador);

        return "coordinador/coordinador";
    }

    @GetMapping("/fotoCoordinador/{id}")
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

    @PostMapping("/coordinador/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto, HttpSession session, Model model) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index"; // Redirige si no hay sesión
        }

        if (!foto.isEmpty()) {
            try {
                // Verifica si el asesor ya tiene una foto
                fotos fotoExistente = fotoServicio.obtenerFotoPorIdUsuario(coordinador.getIdUsuario());

                if (fotoExistente != null) {
                    // Si ya existe una foto, actualiza la foto existente
                    fotoServicio.actualizarFoto(foto, coordinador.getIdUsuario());
                    model.addAttribute("mensaje", "Foto actualizada exitosamente");
                } else {
                    // Si no existe una foto, guarda la nueva foto
                    fotoServicio.guardarFoto(foto, coordinador.getIdUsuario());
                    model.addAttribute("mensaje", "Foto subida exitosamente");
                }
            } catch (IOException e) {
                model.addAttribute("error", "Error al subir la foto");
            }
        }
        return "redirect:/coordinador"; // Redirige al perfil después de subir la foto
    }

    //vista del coordinador del foro
    @GetMapping("/coordinador/foro")
    public String mostrarForos(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String trimestre,
            Model model, 
            HttpSession session) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index";
        }

        List<foro> foros = foroRepositorio.findAll();

        // Aplicar filtros
        if (filtro != null && !filtro.trim().isEmpty()) {
            foros = foros.stream()
                    .filter(f -> f.getNombre().toLowerCase().contains(filtro.toLowerCase()) ||
                           f.getTema().toLowerCase().contains(filtro.toLowerCase()) ||
                           f.getContenido().toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (tema != null && !tema.isEmpty() && !tema.equals("todos")) {
            foros = foros.stream()
                    .filter(f -> f.getTema().equals(tema))
                    .collect(Collectors.toList());
        }
        
        if (trimestre != null && !trimestre.isEmpty() && !trimestre.equals("todos")) {
            foros = foros.stream()
                    .filter(f -> String.valueOf(f.getTrimestre()).equals(trimestre))
                    .collect(Collectors.toList());
        }
        
        // Obtener lista de temas y trimestres únicos para los selectores
        List<String> temas = foroRepositorio.findAll().stream()
                .map(f -> f.getTema())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        List<String> trimestres = foroRepositorio.findAll().stream()
                .map(f -> String.valueOf(f.getTrimestre()))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        model.addAttribute("coordinador", coordinador);
        model.addAttribute("foros", foros);
        model.addAttribute("filtro", filtro);
        model.addAttribute("tema", tema);
        model.addAttribute("trimestre", trimestre);
        model.addAttribute("temas", temas);
        model.addAttribute("trimestres", trimestres);
        
        return "coordinador/foro";
    }

    @GetMapping("/coordinador/comentarios/{idForo}")
    @ResponseBody
    public List<Comentario> obtenerComentarios(@PathVariable int idForo) {
        return comentarioServicio.ObtenerComentario(idForo);
    }

    @DeleteMapping("/coordinador/comentarios/eliminar/{id}")
    @ResponseBody
    public void eliminarComentario(@PathVariable Long id) {
        comentarioServicio.eliminarComentario(id);
    }

    //vista del coordiandor de las PQRS
    @GetMapping("/coordinador/PQRS")
    public String mostrarPQRS(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            Model model, 
            HttpSession session) {
        System.out.println("CoordinadorControlador.mostrarPQRS() called");
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index";
        }
        
        List<PQRS> listaPQRS = pqrsServicio.obtenerTodasPQRS();
        
        // Aplicar filtros
        if (filtro != null && !filtro.trim().isEmpty()) {
            listaPQRS = listaPQRS.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(filtro.toLowerCase()) ||
                           p.getEmail().toLowerCase().contains(filtro.toLowerCase()) ||
                           p.getTipo().toLowerCase().contains(filtro.toLowerCase()) ||
                           p.getDetalles().toLowerCase().contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (tipo != null && !tipo.isEmpty() && !tipo.equals("todos")) {
            listaPQRS = listaPQRS.stream()
                    .filter(p -> p.getTipo().equals(tipo))
                    .collect(Collectors.toList());
        }
        
        if (estado != null && !estado.isEmpty() && !estado.equals("todos")) {
            listaPQRS = listaPQRS.stream()
                    .filter(p -> p.getEstado().name().equals(estado))
                    .collect(Collectors.toList());
        }
        
        model.addAttribute("coordinador", coordinador);
        model.addAttribute("pqrsList", listaPQRS);
        model.addAttribute("filtro", filtro);
        model.addAttribute("tipo", tipo);
        model.addAttribute("estado", estado);
        
        return "coordinador/PQRS";
    }
    
    @GetMapping("/coordinador/buscarPQRS")
    public String buscarPQRS(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            Model model, 
            HttpSession session) {
        return mostrarPQRS(filtro, tipo, estado, model, session);
    }

    //vista del coordinador de los usuarios
    @RequestMapping("/coordinador/usuarios")
    public String Coordinador3(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String trimestre,
            Model model, 
            HttpSession session) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index";
        }

        List<Usuario> usuarios;

        // Aplicar filtros
        if (filtro != null && !filtro.trim().isEmpty()) {
            usuarios = usuarioServicio.buscarUsuarioPorFiltro(filtro);
        } else {
            usuarios = usuarioServicio.listarUsuarios();
        }
        
        if (rol != null && !rol.isEmpty() && !rol.equals("todos")) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getRolSeleccionado().equals(rol))
                    .collect(Collectors.toList());
        }
        
        if (trimestre != null && !trimestre.isEmpty() && !trimestre.equals("todos")) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getTrimestre() != null && u.getTrimestre().equals(trimestre))
                    .collect(Collectors.toList());
        }
        
        // Obtener lista de trimestres únicos para el selector
        List<String> trimestres = usuarioServicio.listarUsuarios().stream()
                .map(Usuario::getTrimestre)
                .filter(Objects::nonNull)
                .filter(t -> !t.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("coordinador", coordinador);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtro", filtro);
        model.addAttribute("rol", rol);
        model.addAttribute("trimestre", trimestre);
        model.addAttribute("trimestres", trimestres);
        
        return "coordinador/usuarios";
    }
    
    @GetMapping("/coordinador/buscar")
    public String buscarUsuarios(
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String trimestre,
            Model model, 
            HttpSession session) {
        return Coordinador3(filtro, rol, trimestre, model, session);
    }

    @PostMapping("/coordinador/subir-csv")
    public String subirArchivoCsv(@RequestParam("archivo") MultipartFile archivo, RedirectAttributes redirectAttributes, Model model) {
          try {
        List<Usuario> usuariosCargados = usuarioServicio.cargarUsuariosDesdeCSV(archivo);

        model.addAttribute("mensaje", "Usuarios cargados exitosamente: " + usuariosCargados.size());
        model.addAttribute("usuarios", usuariosCargados);
        return "redirect:/coordinador/usuarios"; 

    } catch (IOException e) {
        e.printStackTrace();
        model.addAttribute("error", "Ocurrió un error al cargar el archivo: " + e.getMessage());
        return "errorCarga"; 
    }
    }

    @GetMapping("/coordinador/asesorias")
    public String asesorias(Model modelo, HttpSession session) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index";
        }
        
        // Redirigir a filtrarAsesorias sin filtros aplicados
        return filtrarAsesorias(null, null, null, null, null, null, modelo, session);
    }
    
    @GetMapping("/coordinador/asesorias/filtrar")
    public String filtrarAsesorias(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String completada,
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) String idUsuario,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            Model modelo, 
            HttpSession session) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index";
        }
        
        // Obtener todas las asesorías
        List<Asesoria> asesorias = asesoriaServicio.listarTodasLasAsesorias();
        
        // Aplicar filtros
        if (estado != null && !estado.equals("todos")) {
            asesorias = asesorias.stream()
                    .filter(a -> a.getEstado().equals(estado))
                    .collect(Collectors.toList());
        }
        
        if (completada != null && !completada.equals("todos")) {
            boolean completadaValue = completada.equals("si");
            asesorias = asesorias.stream()
                    .filter(a -> a.isCompletada() == completadaValue)
                    .collect(Collectors.toList());
        }
        
        if (grupoId != null && grupoId > 0) {
            asesorias = asesorias.stream()
                    .filter(a -> a.getGrupo() != null && a.getGrupo().getId() == grupoId)
                    .collect(Collectors.toList());
        }
        
        if (idUsuario != null && !idUsuario.trim().isEmpty()) {
            Long idUsuarioLong;
            try {
                idUsuarioLong = Long.parseLong(idUsuario.trim());
                asesorias = asesorias.stream()
                        .filter(a -> {
                            Grupo grupo = a.getGrupo();
                            if (grupo == null) return false;
                            
                            // Buscar en el asesor del grupo
                            if (grupo.getAsesor() != null && grupo.getAsesor().getIdUsuario().equals(idUsuarioLong)) {
                                return true;
                            }
                            
                            // Buscar en los aprendices del grupo
                            if (grupo.getAprendices() != null) {
                                return grupo.getAprendices().stream()
                                        .anyMatch(ga -> {
                                            Usuario u = ga.getAprendiz();
                                            return u != null && u.getIdUsuario().equals(idUsuarioLong);
                                        });
                            }
                            
                            return false;
                        })
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Si el ID no es un número válido, no aplicar filtro
                // pero mostrar un log
                System.out.println("ID de usuario no válido: " + idUsuario);
            }
        }
        
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            LocalDate desde = LocalDate.parse(fechaDesde);
            asesorias = asesorias.stream()
                    .filter(a -> a.getFecha() != null && (a.getFecha().isEqual(desde) || a.getFecha().isAfter(desde)))
                    .collect(Collectors.toList());
        }
        
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            LocalDate hasta = LocalDate.parse(fechaHasta);
            asesorias = asesorias.stream()
                    .filter(a -> a.getFecha() != null && (a.getFecha().isEqual(hasta) || a.getFecha().isBefore(hasta)))
                    .collect(Collectors.toList());
        }
        
        // Obtener todos los grupos para el selector
        List<Grupo> grupos = grupoServicio.listarGrupos();
        
        // Estadísticas
        int totalAsesorias = asesorias.size();
        int asesoriasActivas = (int) asesorias.stream().filter(a -> "Activa".equals(a.getEstado())).count();
        int asesoriasCompletadas = (int) asesorias.stream().filter(Asesoria::isCompletada).count();
        
        modelo.addAttribute("coordinador", coordinador);
        modelo.addAttribute("asesorias", asesorias);
        modelo.addAttribute("grupos", grupos);
        modelo.addAttribute("totalAsesorias", totalAsesorias);
        modelo.addAttribute("asesoriasActivas", asesoriasActivas);
        modelo.addAttribute("asesoriasCompletadas", asesoriasCompletadas);
        
        // Guardar filtros para mantenerlos en la vista
        modelo.addAttribute("estadoFiltro", estado);
        modelo.addAttribute("completadaFiltro", completada);
        modelo.addAttribute("grupoIdFiltro", grupoId);
        modelo.addAttribute("idUsuarioFiltro", idUsuario);
        modelo.addAttribute("fechaDesdeFiltro", fechaDesde);
        modelo.addAttribute("fechaHastaFiltro", fechaHasta);
        
        return "coordinador/asesorias";
    }

    @GetMapping("/coordinador/asesorias/generarPDF")
    public ResponseEntity<byte[]> generarPdfAsesorias(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String completada,
            @RequestParam(required = false) Long grupoId,
            @RequestParam(required = false) String idUsuario,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            HttpSession session) {
        try {
            Usuario coordinador = (Usuario) session.getAttribute("usuario");
            if (coordinador == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Obtener todas las asesorías
            List<Asesoria> asesoriasFiltradas = asesoriaServicio.listarTodasLasAsesorias();
            
            // Aplicar filtros
            if (estado != null && !estado.isEmpty() && !estado.equals("todos")) {
                asesoriasFiltradas = asesoriasFiltradas.stream()
                        .filter(a -> estado.equals(a.getEstado()))
                        .collect(Collectors.toList());
            }
            
            if (completada != null && !completada.isEmpty() && !completada.equals("todos")) {
                boolean esCompletada = completada.equals("si");
                asesoriasFiltradas = asesoriasFiltradas.stream()
                        .filter(a -> a.isCompletada() == esCompletada)
                        .collect(Collectors.toList());
            }
            
            if (grupoId != null && grupoId > 0) {
                asesoriasFiltradas = asesoriasFiltradas.stream()
                        .filter(a -> a.getGrupo() != null && a.getGrupo().getId() == grupoId)
                        .collect(Collectors.toList());
            }
            
            if (idUsuario != null && !idUsuario.trim().isEmpty()) {
                Long idUsuarioLong;
                try {
                    idUsuarioLong = Long.parseLong(idUsuario.trim());
                    asesoriasFiltradas = asesoriasFiltradas.stream()
                            .filter(a -> {
                                Grupo grupo = a.getGrupo();
                                if (grupo == null) return false;
                                
                                // Buscar en el asesor del grupo
                                if (grupo.getAsesor() != null && grupo.getAsesor().getIdUsuario().equals(idUsuarioLong)) {
                                    return true;
                                }
                                
                                // Buscar en los aprendices del grupo
                                if (grupo.getAprendices() != null) {
                                    return grupo.getAprendices().stream()
                                            .anyMatch(ga -> {
                                                Usuario u = ga.getAprendiz();
                                                return u != null && u.getIdUsuario().equals(idUsuarioLong);
                                            });
                                }
                                
                                return false;
                            })
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    // Si el ID no es un número válido, no aplicar filtro
                    System.out.println("ID de usuario no válido para PDF: " + idUsuario);
                }
            }
            
            if (fechaDesde != null && !fechaDesde.isEmpty()) {
                LocalDate desde = LocalDate.parse(fechaDesde);
                asesoriasFiltradas = asesoriasFiltradas.stream()
                        .filter(a -> a.getFecha() != null && (a.getFecha().isEqual(desde) || a.getFecha().isAfter(desde)))
                        .collect(Collectors.toList());
            }
            
            if (fechaHasta != null && !fechaHasta.isEmpty()) {
                LocalDate hasta = LocalDate.parse(fechaHasta);
                asesoriasFiltradas = asesoriasFiltradas.stream()
                        .filter(a -> a.getFecha() != null && (a.getFecha().isEqual(hasta) || a.getFecha().isBefore(hasta)))
                        .collect(Collectors.toList());
            }
            
            // Crear el documento PDF
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Título del reporte
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            Paragraph title = new Paragraph("Reporte de Asesorías", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Información de filtros aplicados
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Filtros aplicados:", subtitleFont));
            if (estado != null && !estado.isEmpty() && !estado.equals("todos")) {
                document.add(new Paragraph("Estado: " + estado, normalFont));
            }
            if (completada != null && !completada.isEmpty() && !completada.equals("todos")) {
                document.add(new Paragraph("Completada: " + (completada.equals("si") ? "Sí" : "No"), normalFont));
            }
            if (grupoId != null && grupoId > 0) {
                // Buscar el nombre del grupo
                Grupo grupo = asesoriasFiltradas.stream()
                        .filter(a -> a.getGrupo() != null && a.getGrupo().getId() == grupoId)
                        .map(a -> a.getGrupo())
                        .findFirst()
                        .orElse(null);
                
                String nombreGrupo = grupo != null ? grupo.getNombre() : "No encontrado";
                document.add(new Paragraph("Grupo: " + nombreGrupo, normalFont));
            }
            if (fechaDesde != null && !fechaDesde.isEmpty()) {
                document.add(new Paragraph("Fecha desde: " + fechaDesde, normalFont));
            }
            if (fechaHasta != null && !fechaHasta.isEmpty()) {
                document.add(new Paragraph("Fecha hasta: " + fechaHasta, normalFont));
            }
            
            // Estadísticas
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Resumen:", subtitleFont));
            document.add(new Paragraph("Total de asesorías: " + asesoriasFiltradas.size(), normalFont));
            int activas = (int) asesoriasFiltradas.stream().filter(a -> "Activa".equals(a.getEstado())).count();
            document.add(new Paragraph("Asesorías activas: " + activas, normalFont));
            int completadas = (int) asesoriasFiltradas.stream().filter(Asesoria::isCompletada).count();
            document.add(new Paragraph("Asesorías completadas: " + completadas, normalFont));
            
            // Tabla de asesorías
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Detalle de asesorías:", subtitleFont));
            
            PdfPTable table = new PdfPTable(6); // 6 columnas
            table.setWidthPercentage(100);
            
            // Encabezados de la tabla
            PdfPCell cell = new PdfPCell(new Phrase("ID", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Grupo", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Asesor", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Fecha", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Estado", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Completada", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            // Formato para fechas
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            // Datos de la tabla
            for (Asesoria asesoria : asesoriasFiltradas) {
                table.addCell(String.valueOf(asesoria.getId()));
                table.addCell(asesoria.getGrupo().getNombre());
                
                Usuario asesor = asesoria.getGrupo().getAsesor();
                String nombreAsesor = asesor != null ? (asesor.getNombres() + " " + asesor.getPrimerA()) : "No asignado";
                table.addCell(nombreAsesor);
                
                String fechaFormateada = asesoria.getFecha() != null ? asesoria.getFecha().format(dateFormatter) : "N/A";
                table.addCell(fechaFormateada);
                
                table.addCell(asesoria.getEstado());
                table.addCell(asesoria.isCompletada() ? "Sí" : "No");
            }
            
            document.add(table);
            
            // Agregar pie de página con la fecha de generación
            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("Reporte generado: " + new Date().toString(), normalFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);
            
            document.close();
            
            // Configurar la respuesta HTTP con el PDF
            byte[] pdfBytes = baos.toByteArray();
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=reporte_asesorias.pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (DocumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/coordinador/componentes")
    public String gestionarComponentes(Model model, HttpSession session) {
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index";
        }
        
        // Obtener todos los componentes únicos
        List<String> componentesDisponibles = componenteServicio.obtenerNombresComponentesUnicos();
        
        // Obtener información de grupos por componente
        Map<String, Integer> gruposPorComponente = new HashMap<>();
        for (String nombreComponente : componentesDisponibles) {
            List<Grupo> grupos = grupoServicio.buscarGruposPorNombres(Collections.singletonList(nombreComponente));
            gruposPorComponente.put(nombreComponente, grupos.size());
        }
        
        // Obtener lista de programas
        List<String> programas = programaServicio.listarNombresProgramas();
        
        model.addAttribute("coordinador", coordinador);
        model.addAttribute("componentesDisponibles", componentesDisponibles);
        model.addAttribute("gruposPorComponente", gruposPorComponente);
        model.addAttribute("programas", programas);
        
        return "coordinador/componentes";
    }
    
    @PostMapping("/coordinador/componentes/crear")
    public String crearComponente(
            @RequestParam("nombreComponente") String nombreComponente,
            @RequestParam(value = "tipoProgramas", required = false, defaultValue = "unico") String tipoProgramas,
            @RequestParam(value = "programa", required = false) String programaSeleccionado,
            @RequestParam(value = "programas", required = false) List<String> programasSeleccionados,
            @RequestParam(value = "nuevoPrograma", required = false) String nuevoPrograma,
            @RequestParam(value = "implementarProgramas", required = false) Boolean implementarProgramas,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        if (nombreComponente == null || nombreComponente.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El nombre del componente no puede estar vacío");
            return "redirect:/coordinador/componentes";
        }
        
        Usuario coordinador = (Usuario) session.getAttribute("usuario");
        if (coordinador == null) {
            return "redirect:/index";
        }
        
        // Verificar si ya existe un componente con ese nombre
        List<String> componentesExistentes = componenteServicio.obtenerNombresComponentesUnicos();
        if (componentesExistentes.contains(nombreComponente.trim())) {
            redirectAttributes.addFlashAttribute("error", "Ya existe un componente con ese nombre");
            return "redirect:/coordinador/componentes";
        }
        
        // Crear un nuevo grupo con el nombre del componente
        Grupo nuevoGrupo = new Grupo();
        nuevoGrupo.setNombre(nombreComponente.trim());
        nuevoGrupo.setCantidad(0);
        nuevoGrupo.setEstado("Activo");
        grupoServicio.guardar(nuevoGrupo);
        
        // Crear un registro en la tabla Componente asociado al coordinador
        Componente componente = new Componente();
        componente.setNombre(nombreComponente.trim());
        componente.setUsuario(coordinador);
        
        // Procesar la lista de programas según el tipo de selección
        List<String> nombresProgramas = new ArrayList<>();
        
        // Si se seleccionó un único programa
        if ("unico".equals(tipoProgramas) && programaSeleccionado != null && !programaSeleccionado.isEmpty()) {
            nombresProgramas.add(programaSeleccionado);
        }
        
        // Si se seleccionaron múltiples programas
        if ("multiple".equals(tipoProgramas) && programasSeleccionados != null && !programasSeleccionados.isEmpty()) {
            nombresProgramas.addAll(programasSeleccionados);
        }
        
        // Procesar el nuevo programa si se ha especificado
        if (nuevoPrograma != null && !nuevoPrograma.trim().isEmpty()) {
            // Verificar si ya existe el programa
            if (programaServicio.existePorNombre(nuevoPrograma.trim())) {
                redirectAttributes.addFlashAttribute("error", "El programa '" + nuevoPrograma.trim() + "' ya existe. Por favor, selecciónelo de la lista en lugar de crearlo nuevamente.");
                return "redirect:/coordinador/componentes";
            } else {
                // Crear nuevo programa
                Programa programa = new Programa();
                programa.setId(generarIdPrograma()); // Generar un ID único
                programa.setNombre(nuevoPrograma.trim());
                programaServicio.guardar(programa);
                
                // Añadir el nuevo programa a la lista
                nombresProgramas.add(nuevoPrograma.trim());
            }
        }
        
        // Asociar programas al componente
        if (!nombresProgramas.isEmpty()) {
            List<Programa> programas = new ArrayList<>();
            for (String nombrePrograma : nombresProgramas) {
                List<Programa> programasEncontrados = programaServicio.buscarPorNombre(nombrePrograma);
                if (!programasEncontrados.isEmpty()) {
                    programas.add(programasEncontrados.get(0));
                }
            }
            componente.setProgramas(programas);
        }
        
        componenteServicio.guardarComponente(componente);
        
        // Mensaje personalizado según la implementación
        String mensaje = "Componente creado exitosamente";
        if (Boolean.TRUE.equals(implementarProgramas) && !nombresProgramas.isEmpty()) {
            mensaje += " e implementado a los programas: " + String.join(", ", nombresProgramas);
        }
        
        redirectAttributes.addFlashAttribute("mensaje", mensaje);
        return "redirect:/coordinador/componentes";
    }
    
    // Método para generar un ID único para un nuevo programa
    private Long generarIdPrograma() {
        // Obtener el máximo ID existente y sumar 1
        return programaServicio.listarProgramas().stream()
                .mapToLong(Programa::getId)
                .max()
                .orElse(0) + 1;
    }

    @PostMapping("/coordinador/inactivar-usuario")
    public String inactivarUsuario(
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("accion") String accion,
            @RequestParam("motivo") String motivo,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Obtener el usuario
            Optional<Usuario> optUsuario = usuarioServicio.obtenerUsuarioPorId(idUsuario);
            if (!optUsuario.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/coordinador/usuarios";
            }
            
            Usuario usuario = optUsuario.get();
            
            // Actualizar el estado del usuario
            if ("inactivar".equals(accion)) {
                usuario.setEstado("Inactivo");
                redirectAttributes.addFlashAttribute("mensaje", "Usuario inactivado correctamente");
            } else {
                usuario.setEstado("Activo");
                redirectAttributes.addFlashAttribute("mensaje", "Usuario activado correctamente");
            }
            
            // Guardar el usuario
            usuarioServicio.guardarUsuario(usuario);
            
            // Enviar correo al usuario
            if (usuario.getCorreo() != null && !usuario.getCorreo().isEmpty()) {
                String nombreCompleto = usuario.getNombres() + " " + usuario.getPrimerA();
                
                try {
                    if ("inactivar".equals(accion)) {
                        correoServicio.sendUserInactivationEmail(usuario.getCorreo(), nombreCompleto, motivo);
                    } else {
                        correoServicio.sendUserActivationEmail(usuario.getCorreo(), nombreCompleto, motivo);
                    }
                } catch (Exception e) {
                    // Registrar error pero no interrumpir el flujo
                    System.err.println("Error al enviar correo: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }
        
        return "redirect:/coordinador/usuarios";
    }

    @GetMapping("/coordinador/asesorias/{asesoriaId}/aprendices")
    @ResponseBody
    public List<Map<String, Object>> obtenerAprendicesPorAsesoria(@PathVariable int asesoriaId) {
        // Obtener la asesoría
        Optional<Asesoria> optAsesoria = asesoriaServicio.obtenerPorId(asesoriaId);
        if (!optAsesoria.isPresent()) {
            return Collections.emptyList();
        }
        
        Asesoria asesoria = optAsesoria.get();
        Grupo grupo = asesoria.getGrupo();
        
        if (grupo == null || grupo.getAprendices() == null || grupo.getAprendices().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        for (GrupoAprendiz grupoAprendiz : grupo.getAprendices()) {
            Usuario aprendiz = grupoAprendiz.getAprendiz();
            if (aprendiz != null) {
                Map<String, Object> aprendizData = new HashMap<>();
                aprendizData.put("idUsuario", aprendiz.getIdUsuario());
                aprendizData.put("nombreCompleto", aprendiz.getNombres() + " " + aprendiz.getPrimerA() + " " + aprendiz.getSegundoA());
                
                // Buscar si el aprendiz asistió a la asesoría
                List<Asistencia> asistencias = asistenciaRepositorio.findByUsuarioIdUsuarioAndFecha(
                        aprendiz.getIdUsuario(), asesoria.getFecha());
                
                boolean asistio = !asistencias.isEmpty() && asistencias.stream()
                        .anyMatch(a -> a.getAsesoria().getId() == asesoriaId && Boolean.TRUE.equals(a.getAsistio()));
                
                aprendizData.put("asistio", asistio);
                resultado.add(aprendizData);
            }
        }
        
        return resultado;
    }
    
    @GetMapping("/coordinador/asesorias/{asesoriaId}/asistencia/pdf")
    public ResponseEntity<byte[]> generarPdfAsistencia(@PathVariable int asesoriaId) {
        try {
            // Obtener la asesoría
            Optional<Asesoria> optAsesoria = asesoriaServicio.obtenerPorId(asesoriaId);
            if (!optAsesoria.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Asesoria asesoria = optAsesoria.get();
            Grupo grupo = asesoria.getGrupo();
            
            if (grupo == null) {
                return ResponseEntity.badRequest().build();
            }
            
            // Crear el documento PDF
            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Título del reporte
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            Paragraph title = new Paragraph("Reporte de Asistencia", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Información de la asesoría
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Información de la Asesoría:", subtitleFont));
            document.add(new Paragraph("ID: " + asesoria.getId(), normalFont));
            document.add(new Paragraph("Grupo: " + grupo.getNombre(), normalFont));
            
            Usuario asesor = grupo.getAsesor();
            String nombreAsesor = asesor != null ? (asesor.getNombres() + " " + asesor.getPrimerA()) : "No asignado";
            document.add(new Paragraph("Asesor: " + nombreAsesor, normalFont));
            
            String fechaFormateada = asesoria.getFecha() != null ? asesoria.getFechaFormateada() : "N/A";
            document.add(new Paragraph("Fecha: " + fechaFormateada, normalFont));
            
            String horaFormateada = asesoria.getHora() != null ? asesoria.getHoraFormateada() : "N/A";
            document.add(new Paragraph("Hora: " + horaFormateada, normalFont));
            
            // Tabla de asistencia
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Asistencia de Aprendices:", subtitleFont));
            
            PdfPTable table = new PdfPTable(3); // 3 columnas
            table.setWidthPercentage(100);
            
            // Encabezados de la tabla
            PdfPCell cell = new PdfPCell(new Phrase("ID", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Nombre", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Asistencia", subtitleFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            // Obtener los aprendices y su asistencia
            if (grupo.getAprendices() != null && !grupo.getAprendices().isEmpty()) {
                for (GrupoAprendiz grupoAprendiz : grupo.getAprendices()) {
                    Usuario aprendiz = grupoAprendiz.getAprendiz();
                    if (aprendiz != null) {
                        table.addCell(String.valueOf(aprendiz.getIdUsuario()));
                        table.addCell(aprendiz.getNombres() + " " + aprendiz.getPrimerA() + " " + aprendiz.getSegundoA());
                        
                        // Buscar si el aprendiz asistió a la asesoría
                        List<Asistencia> asistencias = asistenciaRepositorio.findByUsuarioIdUsuarioAndFecha(
                                aprendiz.getIdUsuario(), asesoria.getFecha());
                        
                        boolean asistio = !asistencias.isEmpty() && asistencias.stream()
                                .anyMatch(a -> a.getAsesoria().getId() == asesoriaId && Boolean.TRUE.equals(a.getAsistio()));
                        
                        table.addCell(asistio ? "Asistió" : "No asistió");
                    }
                }
            } else {
                PdfPCell noDataCell = new PdfPCell(new Phrase("No hay aprendices asignados a este grupo", normalFont));
                noDataCell.setColspan(3);
                noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(noDataCell);
            }
            
            document.add(table);
            
            // Agregar pie de página con la fecha de generación
            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("Reporte generado: " + new Date().toString(), normalFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);
            
            document.close();
            
            // Configurar la respuesta HTTP con el PDF
            byte[] pdfBytes = baos.toByteArray();
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=asistencia_asesoria_" + asesoriaId + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (DocumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

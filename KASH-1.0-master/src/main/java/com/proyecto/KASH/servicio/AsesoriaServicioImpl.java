package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.AsesoriaRepositorio;
import com.proyecto.KASH.Repository.GrupoRepositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Usuario;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

@Service
public class AsesoriaServicioImpl implements AsesoriaServicio {

    @Autowired
    private AsesoriaRepositorio asesoriaRepositorio;

    @Autowired
    private EmailService correoServicio;

    @Autowired
    private GrupoRepositorio grupoRepositorio;

    @Autowired
    private EntityManager entityManager;

    public AsesoriaServicioImpl(AsesoriaRepositorio asesoriaRepositorio) {
        this.asesoriaRepositorio = asesoriaRepositorio;
    }

    @Override
    public void crearAsesoria(Asesoria asesoriaBase) {

        Grupo grupo = asesoriaBase.getGrupo();
        Usuario asesor = grupo.getAsesor();
        LocalDate fechaInicio = asesoriaBase.getFecha_inicio();
        int dias = asesoriaBase.getDias_asesoria();
        LocalTime hora = asesoriaBase.getHora();
        List<GrupoAprendiz> aprendices = grupo.getAprendices();

        List<Asesoria> asesoriasCreadas = new ArrayList<>();

        for (int i = 0; i < dias; i++) {
            LocalDate fechaActual = fechaInicio.plusDays(i * 8);

            boolean existeParaGrupo = asesoriaRepositorio.existsByGrupoAndFechaAndHora(grupo, fechaActual, hora);
            boolean existeParaAsesor = asesoriaRepositorio.existsByAsesorAndFechaAndHora(asesor, fechaActual, hora);

            if (!existeParaGrupo && !existeParaAsesor) {
                Asesoria nueva = new Asesoria();
                nueva.setGrupo(grupo);
                nueva.setFecha(fechaActual);
                nueva.setHora(hora);
                nueva.setLink(asesoriaBase.getLink());
                nueva.setDias_asesoria(dias);
                nueva.setFecha_inicio(fechaInicio);
                nueva.setEstado("Activa");
                nueva.setCompletada(false);

                asesoriaRepositorio.save(nueva);
                asesoriasCreadas.add(nueva);
            }
        }

        // Preparar resumen para enviar en un solo correo a todos los aprendices
        if (!asesoriasCreadas.isEmpty()) {
            String asunto = "Resumen de nuevas asesorías programadas";
            StringBuilder mensaje = new StringBuilder("Se han programado las siguientes asesorías:\n\n");

            for (Asesoria a : asesoriasCreadas) {
                mensaje.append("Fecha: ").append(a.getFecha())
                        .append(" (").append(a.getDiaAsesoriaNombre()).append(")")
                        .append("\nHora: ").append(a.getHora())
                        .append("\nLink: ").append(a.getLink())
                        .append("\n\n");
            }

            List<String> correosAprendices = aprendices.stream()
                    .map(GrupoAprendiz::getUsuario)
                    .filter(u -> u != null && u.getCorreo() != null && !u.getCorreo().isBlank())
                    .map(Usuario::getCorreo)
                    .collect(Collectors.toList());

            try {
                correoServicio.sendMassEmail(correosAprendices, asunto, mensaje.toString());
            } catch (MailException e) {
                System.out.println("Error al enviar correos de la asesoría: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void actualizarEstadoAsesorias() {
        List<Asesoria> asesorias = asesoriaRepositorio.findAll();
        LocalDate hoy = LocalDate.now();

        for (Asesoria asesoria : asesorias) {
            if (asesoria.getFecha_inicio() != null && asesoria.getDias_asesoria() > 0) {
                LocalDate fechaFin = asesoria.getFecha_inicio().plusDays(asesoria.getDias_asesoria());

                if (hoy.isAfter(fechaFin)) {
                    if (!"Inactiva".equals(asesoria.getEstado())) {
                        asesoria.setEstado("Inactiva");
                        asesoriaRepositorio.save(asesoria);
                    }
                } else {
                    if (!"Activa".equals(asesoria.getEstado())) {
                        asesoria.setEstado("Activa");
                        asesoriaRepositorio.save(asesoria);
                    }
                }
            }
        }

        // Ahora revisamos el estado de los grupos
        Map<Grupo, List<Asesoria>> asesoriasPorGrupo = asesorias.stream()
                .filter(a -> a.getGrupo() != null)
                .collect(Collectors.groupingBy(Asesoria::getGrupo));

        for (Map.Entry<Grupo, List<Asesoria>> entry : asesoriasPorGrupo.entrySet()) {
            Grupo grupo = entry.getKey();
            List<Asesoria> asesoriasGrupo = entry.getValue();

            boolean todasInactivas = asesoriasGrupo.stream()
                    .allMatch(a -> "Inactiva".equalsIgnoreCase(a.getEstado()));

            if (todasInactivas && !"Inactivo".equalsIgnoreCase(grupo.getEstado())) {
                grupo.setEstado("Inactivo");
                grupoRepositorio.save(grupo);
            } else if (!todasInactivas && !"Activo".equalsIgnoreCase(grupo.getEstado())) {
                grupo.setEstado("Activo");
                grupoRepositorio.save(grupo);
            }
        }
    }

    @Override
    public void marcarComoCompletada(int id) {
        Optional<Asesoria> opt = asesoriaRepositorio.findById(id);
        if (opt.isPresent()) {
            Asesoria asesoria = opt.get();
            asesoria.setCompletada(true);
            asesoriaRepositorio.save(asesoria);
        }
    }

    @Override
    public boolean existeConflicto(Grupo grupo, LocalDate fecha, LocalTime hora) {
        int idGrupo = grupo.getId();
        Long idAsesor = grupo.getAsesor().getIdUsuario();

        List<Asesoria> conflictos = asesoriaRepositorio.buscarConflictos(fecha, hora, idGrupo, idAsesor);

        return !conflictos.isEmpty();
    }

    @Override
    public Asesoria buscarPorId(int id) {
        return asesoriaRepositorio.findById(id).orElse(null);
    }

    @Override
    public Asesoria obtenerSiguienteAsesoria(int idGrupo, LocalDate fechaActual) {
        List<Asesoria> siguientes = asesoriaRepositorio.findSiguientesAsesorias(idGrupo, fechaActual);
        return siguientes.isEmpty() ? null : siguientes.get(0);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void actualizarAsesoriasAutomatica() {
        List<Asesoria> asesorias = asesoriaRepositorio.findAll();
        LocalDate hoy = LocalDate.now();
        boolean cambiosRealizados = false;

        for (Asesoria asesoria : asesorias) {
            // Verificar si la fecha de la asesoría ya pasó
            if (asesoria.getFecha() != null && asesoria.getFecha().isBefore(hoy)) {
                // Solo actualizar si no está ya marcada como inactiva y completada
                if (!"Inactiva".equals(asesoria.getEstado()) || !asesoria.isCompletada()) {
                    asesoria.setEstado("Inactiva");
                    asesoria.setCompletada(true);
                    cambiosRealizados = true;
                    
                    System.out.println("Asesoría ID " + asesoria.getId() + " marcada como inactiva y completada automáticamente");
                }
            }
        }
        
        if (cambiosRealizados) {
            asesoriaRepositorio.saveAll(asesorias);
            System.out.println("Actualización automática de asesorías completada: " + LocalDateTime.now());
        }
    }

    public void actualizarEstadoAsesoria(Long idAsesoria, String nuevoEstado) {
        Optional<Asesoria> optionalAsesoria = asesoriaRepositorio.findById(idAsesoria);

        if (optionalAsesoria.isPresent()) {
            Asesoria asesoria = optionalAsesoria.get();
            asesoria.setEstado(nuevoEstado);
            asesoriaRepositorio.save(asesoria);

            Grupo grupo = asesoria.getGrupo();
            if (grupo != null) {
                // Si se desactiva esta asesoría, revisa si quedan otras activas en el grupo
                List<Asesoria> asesoriasActivas = asesoriaRepositorio.findByGrupoAndEstado(grupo, "Activa");

                if (asesoriasActivas.isEmpty()) {
                    grupo.setEstado("Inactivo");
                    grupoRepositorio.save(grupo);
                }
            }
        }
    }

    @Transactional
    public void desvincularAsesorYEliminarAsesorias(int idGrupo) {
        Optional<Grupo> optionalGrupo = grupoRepositorio.findById(idGrupo);

        if (optionalGrupo.isPresent()) {
            Grupo grupo = optionalGrupo.get();

            // Eliminar asesorías del grupo
            List<Asesoria> asesorias = asesoriaRepositorio.findByGrupo(grupo);
            if (!asesorias.isEmpty()) {
                asesoriaRepositorio.deleteAll(asesorias);
            }

            // Desvincular asesor del grupo
            grupo.setAsesor(null);
            grupo.setEstado("Activo"); // opcional: vuelve disponible el grupo
            grupoRepositorio.save(grupo);
        }
    }

    @Override
    public List<Asesoria> obtenerAsesoriasPorAsesor(Long idAsesor) {
         return asesoriaRepositorio.findAsesoriasPorAsesor(idAsesor);
    }

    @Override
    public List<Asesoria> obtenerAsesoriasActivas() {
        return asesoriaRepositorio.findByEstado("Activa");
    }

    @Override
    public Optional<Asesoria> obtenerPorId(int id) {
        return asesoriaRepositorio.findById(id);
    }

    @Override
    public List<Asesoria> obtenerAsesoriasPorGrupo(Grupo grupo) {
        return asesoriaRepositorio.findByGrupo(grupo);
    }

    @Override
    public List<Asesoria> obtenerAsesoriasPorGrupo(int idGrupo) {
        // Buscar el grupo primero
        Optional<Grupo> grupoOpt = grupoRepositorio.findById(idGrupo);
        if (grupoOpt.isPresent()) {
            Grupo grupo = grupoOpt.get();
            return obtenerAsesoriasPorGrupo(grupo);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Asesoria> listarTodasLasAsesorias() {
        return asesoriaRepositorio.findAll();
    }

    @Override
    @Transactional
    public void eliminarAsesoria(int id) {
        asesoriaRepositorio.deleteById(id);
    }

}

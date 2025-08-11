package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.AsesorAsistenciaRepositorio;
import com.proyecto.KASH.Repository.AsesoriaRepositorio;
import com.proyecto.KASH.Repository.GrupoAprendizRepositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Asistencia;
import com.proyecto.KASH.entidad.Usuario;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsesorAsistenciaServicioImpl implements AsesorAsistenciaServicio {

    private final AsesorAsistenciaRepositorio asistenciaRepo;
    private final GrupoAprendizRepositorio grupoAprendizRepo;
    
    private final AsesoriaRepositorio asesoriaRepositorio;

    public AsesorAsistenciaServicioImpl(AsesorAsistenciaRepositorio asistenciaRepo, GrupoAprendizRepositorio grupoAprendizRepo,  AsesoriaRepositorio asesoriaRepositorio) {
        this.asistenciaRepo = asistenciaRepo;
        this.grupoAprendizRepo = grupoAprendizRepo;
        this.asesoriaRepositorio = asesoriaRepositorio;
    }

    @Override
    public void registrarAsistencia(Asesoria asesoria, Usuario usuario, LocalDate fecha, boolean asistio) {
        asistenciaRepo.findByAsesoriaAndUsuarioAndFecha(asesoria, usuario, fecha)
                .ifPresentOrElse(
                        asistencia -> {
                            asistencia.setAsistio(asistio);
                            asistenciaRepo.save(asistencia);
                        },
                        () -> {
                            Asistencia nueva = new Asistencia();
                            nueva.setAsesoria(asesoria);
                            nueva.setUsuario(usuario);
                            nueva.setFecha(fecha);
                            nueva.setAsistio(asistio);
                            asistenciaRepo.save(nueva);
                        }
                );
    }

    @Override
    public List<Asistencia> obtenerAsistenciasPorFecha(Asesoria asesoria, LocalDate fecha) {
          return asistenciaRepo.findByAsesoriaAndFecha(asesoria, fecha);
    }

    @Override
    public boolean esFechaValidaParaAsesoria(Asesoria asesoria, LocalDate fecha) {
        LocalDate inicio = asesoria.getFecha_inicio();
        LocalDate fin = inicio.plusDays(asesoria.getDias_asesoria() - 1);
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }

    @Override
    public List<Usuario> obtenerAprendicesPorGrupo(int idGrupo) {
        return grupoAprendizRepo.findUsuariosByGrupoId(idGrupo);
    }

    @Override
    public List<Asesoria> obtenerTodasLasAsesorias() {
        return asesoriaRepositorio.findAll();
    }

    @Override
    public Asesoria buscarPorId(int id) {
        return asesoriaRepositorio.findById(id).orElse(null);
    }

    @Override
    public void guardar(Asistencia asistencia) {
        asistenciaRepo.save(asistencia);
    }

    @Override
    public List<Asesoria> obtenerAsesoriasPorIdAsesor(Long idAsesor) {
        return asesoriaRepositorio.findByGrupo_Asesor_IdUsuario(idAsesor);
    }
    
    @Override
    public boolean existeAsistenciaRegistrada(Long idAsesoria) {
        return asistenciaRepo.existsByAsesoriaId(idAsesoria);
    }
    
    @Override
    public List<Asistencia> obtenerAsistenciasPorAsesoria(Long idAsesoria) {
        return asistenciaRepo.findByAsesoriaId(idAsesoria);
    }
    
    @Override
    public Asesoria obtenerSiguienteAsesoriaPorGrupo(int idGrupo) {
        LocalDate fechaActual = LocalDate.now();
        List<Asesoria> proximasAsesorias = asesoriaRepositorio.findProximasAsesoriasPorGrupo(idGrupo, fechaActual);
        return proximasAsesorias.isEmpty() ? null : proximasAsesorias.get(0);
    }
    
    @Override
    public Asesoria obtenerAsesoriaPorGrupoYFechaActual(int idGrupo) {
        LocalDate fechaActual = LocalDate.now();
        List<Asesoria> asesoriasHoy = asesoriaRepositorio.findAsesoriasPorGrupoYFecha(idGrupo, fechaActual);
        return asesoriasHoy.isEmpty() ? null : asesoriasHoy.get(0);
    }

    @Override
    public void eliminarAsistencias(List<Asistencia> asistencias) {
        asistenciaRepo.deleteAll(asistencias);
    }
}

package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Asistencia;
import com.proyecto.KASH.entidad.Usuario;
import java.time.LocalDate;
import java.util.List;

public interface AsesorAsistenciaServicio {
    void registrarAsistencia(Asesoria asesoria, Usuario usuario, LocalDate fecha, boolean asistio);

    List<Asistencia> obtenerAsistenciasPorFecha(Asesoria asesoria, LocalDate fecha);

    boolean esFechaValidaParaAsesoria(Asesoria asesoria, LocalDate fecha);

    List<Usuario> obtenerAprendicesPorGrupo(int idGrupo);
    
    List<Asesoria> obtenerTodasLasAsesorias();
    
    public Asesoria buscarPorId(int id);

    void guardar(Asistencia asistencia);

    public List<Asesoria> obtenerAsesoriasPorIdAsesor(Long idAsesor);
    
    boolean existeAsistenciaRegistrada(Long idAsesoria);
    
    List<Asistencia> obtenerAsistenciasPorAsesoria(Long idAsesoria);
    
    Asesoria obtenerSiguienteAsesoriaPorGrupo(int idGrupo);
    
    Asesoria obtenerAsesoriaPorGrupoYFechaActual(int idGrupo);

    void eliminarAsistencias(List<Asistencia> asistencias);
}

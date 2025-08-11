package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Grupo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AsesoriaServicio {
    
    void crearAsesoria(Asesoria asesoria);
    
    void actualizarEstadoAsesorias();
     
    public void marcarComoCompletada(int id);
    
    public boolean existeConflicto(Grupo grupo, LocalDate fecha, LocalTime hora);
    
    Asesoria buscarPorId(int id);

    public Asesoria obtenerSiguienteAsesoria(int idGrupo, LocalDate fechaActual);
    
    public void actualizarAsesoriasAutomatica();

    public List<Asesoria> obtenerAsesoriasPorAsesor(Long idAsesor);

    public List<Asesoria> obtenerAsesoriasActivas();
    
    public List<Asesoria> obtenerAsesoriasPorGrupo(Grupo grupo);
    
    public List<Asesoria> obtenerAsesoriasPorGrupo(int idGrupo);
   
    public List<Asesoria> listarTodasLasAsesorias();
    
    public void eliminarAsesoria(int id);

    Optional<Asesoria> obtenerPorId(int id);
}

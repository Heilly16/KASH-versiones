package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Asistencia;
import java.time.LocalDate;
import java.util.List;

public interface AprendizAsistenciaServicio {
    
    List<Asistencia> obtenerAsistenciasPorUsuario(Long idUsuario);
    
    List<Asistencia> getAsistenciaByFechaAndAprendiz(LocalDate fecha, Long idUsuario);
}

package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.AprendizAsistenciaRepositorio;
import com.proyecto.KASH.entidad.Asistencia;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AprendizAsistenciaServicioImpl implements AprendizAsistenciaServicio{
    
    private final AprendizAsistenciaRepositorio asistenciaRepositorio;
    
    public AprendizAsistenciaServicioImpl(AprendizAsistenciaRepositorio asistenciaRepositorio) {
        this.asistenciaRepositorio = asistenciaRepositorio;
    }


    @Override
    public List<Asistencia> obtenerAsistenciasPorUsuario(Long idUsuario) {
        return asistenciaRepositorio.findByUsuarioIdUsuario(idUsuario);
    }
    
    @Override
    public List<Asistencia> getAsistenciaByFechaAndAprendiz(LocalDate fecha, Long idUsuario) {
        return asistenciaRepositorio.findByUsuarioIdUsuarioAndFecha(idUsuario, fecha);
    }
    
}

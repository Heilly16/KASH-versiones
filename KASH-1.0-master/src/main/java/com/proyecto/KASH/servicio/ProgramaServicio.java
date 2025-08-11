package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Programa;
import java.util.List;
import java.util.Optional;

public interface ProgramaServicio {
    
    List<Programa> listarProgramas();
    
    List<String> listarNombresProgramas();
    
    Optional<Programa> buscarPorId(Long id);
    
    Programa guardar(Programa programa);
    
    boolean existePorNombre(String nombre);
    
    List<Programa> buscarPorNombre(String nombre);
} 
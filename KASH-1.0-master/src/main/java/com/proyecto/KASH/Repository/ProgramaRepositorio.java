package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Programa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramaRepositorio extends JpaRepository<Programa, Long> {
    
    List<Programa> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT p.nombre FROM Programa p ORDER BY p.nombre")
    List<String> findAllProgramNames();
    
    boolean existsByNombre(String nombre);
} 
package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Asistencia;
import com.proyecto.KASH.entidad.Usuario;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AsesorAsistenciaRepositorio extends JpaRepository<Asistencia, Integer> {
    Optional<Asistencia> findByAsesoriaAndUsuarioAndFecha(Asesoria asesoria, Usuario usuario, LocalDate fecha);
    List<Asistencia> findByAsesoriaAndFecha(Asesoria asesoria, LocalDate fecha);
    List<Asistencia> findByAsesoriaAndUsuario(Asesoria asesoria, Usuario usuario);
    
    @Query("SELECT COUNT(a) > 0 FROM Asistencia a WHERE a.asesoria.id = :idAsesoria")
    boolean existsByAsesoriaId(@Param("idAsesoria") Long idAsesoria);
    
    @Query("SELECT a FROM Asistencia a WHERE a.asesoria.id = :idAsesoria")
    List<Asistencia> findByAsesoriaId(@Param("idAsesoria") Long idAsesoria);
}

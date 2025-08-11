package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Componente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AprendizComponenteRepositorio extends JpaRepository<Componente, Long>{
    // Consulta para obtener los componentes únicos por nombre
    @Query("SELECT DISTINCT c.nombre FROM Componente c")
    List<String> findDistinctComponentNames();
    
    // Buscar componentes por usuario
    List<Componente> findByUsuario_IdUsuario(Long idUsuario);
    
    // Buscar componentes por nombre
    List<Componente> findByNombre(String nombre);
    
    // Buscar componentes por nombre y programa
    @Query("SELECT c FROM Componente c JOIN c.programas p WHERE c.nombre = :nombre AND p.nombre = :nombrePrograma")
    List<Componente> findByNombreAndPrograma(String nombre, String nombrePrograma);
    
    // Eliminar componente por ID y usuario
    void deleteByIdAndUsuario_IdUsuario(Long id, Long idUsuario);
    
    // Buscar componentes por programa
    @Query("SELECT c FROM Componente c JOIN c.programas p WHERE p.nombre = :programa")
    List<Componente> findByPrograma(@Param("programa") String programa);
    
    // Buscar componentes por aprendiz
    @Query("SELECT c FROM Componente c WHERE c.usuario.idUsuario = :idUsuario")
    List<Componente> findComponentesByAprendizId(@Param("idUsuario") Long idUsuario);
    
    // Buscar componentes no registrados por usuario y programa
    @Query("SELECT c FROM Componente c JOIN c.programas p WHERE p.nombre = :programa AND c.id NOT IN (SELECT comp.id FROM Componente comp WHERE comp.usuario.idUsuario = :idUsuario)")
    List<Componente> findComponentesNoRegistradosPorUsuarioYPrograma(@Param("idUsuario") Long idUsuario, @Param("programa") String programa);
}

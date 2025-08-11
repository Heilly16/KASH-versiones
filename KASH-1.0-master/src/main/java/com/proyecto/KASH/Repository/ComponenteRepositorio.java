package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Componente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponenteRepositorio extends JpaRepository<Componente, Integer> {
    
    Componente findByNombre(String nombre);
    
    @Query("SELECT c FROM Componente c JOIN c.programas p WHERE p.nombre = :programa")
    List<Componente> findByPrograma(@Param("programa") String programa);
    
    @Query("SELECT c FROM Componente c WHERE c.nombre LIKE %:nombre%")
    List<Componente> findByNombreContaining(@Param("nombre") String nombre);
    
    @Query("SELECT c FROM Componente c WHERE c.id NOT IN (SELECT comp.id FROM Componente comp WHERE comp.usuario.idUsuario = :idUsuario)")
    List<Componente> findComponentesNoRegistradosPorUsuario(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT c FROM Componente c JOIN c.programas p WHERE c.usuario.idUsuario = :idUsuario")
    List<Componente> findComponentesByAprendizId(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT c FROM Componente c JOIN c.programas p WHERE p.nombre = :programa AND c.id NOT IN (SELECT comp.id FROM Componente comp WHERE comp.usuario.idUsuario = :idUsuario)")
    List<Componente> findComponentesNoRegistradosPorUsuarioYPrograma(@Param("idUsuario") Long idUsuario, @Param("programa") String programa);
} 
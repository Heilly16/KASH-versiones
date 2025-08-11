package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Usuario;
import jakarta.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepositorio extends JpaRepository<Grupo, Integer> {

    @Procedure(name = "registrar_aprendiz_en_componente")
    void registrarAprendizEnComponente(@Param("p_idUsuario") Long idUsuario,
            @Param("p_nombreComponente") String nombreComponente);

    List<Grupo> findByAsesor_idUsuario(Long idUsuario);

    public Object findById(SingularAttribute<AbstractPersistable, Serializable> id);

    @Query("SELECT g FROM Grupo g "
            + "WHERE g.id NOT IN ("
            + "   SELECT ga.grupo.id FROM GrupoAprendiz ga WHERE ga.usuario.idUsuario = :idAprendiz) "
            + "AND g.id NOT IN ("
            + "   SELECT a.grupo.id FROM Asesoria a WHERE a.estado = 'activo')")
    List<Grupo> findGruposDisponiblesParaAprendiz(@Param("idAprendiz") Long idAprendiz);
    
    @Query("SELECT g FROM Grupo g "
            + "WHERE g.id NOT IN ("
            + "   SELECT ga.grupo.id FROM GrupoAprendiz ga WHERE ga.usuario.idUsuario = :idAprendiz) "
            + "AND g.nombre NOT IN ("
            + "   SELECT g2.nombre FROM Grupo g2 JOIN GrupoAprendiz ga ON g2.id = ga.grupo.id "
            + "   WHERE ga.usuario.idUsuario = :idAprendiz) "
            + "AND g.id NOT IN ("
            + "   SELECT a.grupo.id FROM Asesoria a WHERE a.estado = 'Activa')")
    List<Grupo> findGruposNoAsignadosYSinNombreDuplicado(@Param("idAprendiz") Long idAprendiz);

    @Query("SELECT g FROM Grupo g "
            + "WHERE g.estado = 'Activo' AND g.asesor IS NOT NULL "
            + "AND (:filtro IS NULL OR "
            + "LOWER(g.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(g.asesor.nombres) LIKE LOWER(CONCAT('%', :filtro, '%')))")
    List<Grupo> findGruposDisponiblesPorFiltro(String filtro);

    @Query("SELECT g FROM Grupo g WHERE g.asesor IS NULL AND g.nombre NOT IN " +
           "(SELECT DISTINCT g2.nombre FROM Grupo g2 JOIN Asesoria a ON g2.id = a.grupo.id WHERE a.estado = 'Activa')")
    List<Grupo> findGruposSinAsesor();

    public List<Grupo> findGruposByAsesor(Usuario idUsuario);

    List<Grupo> findByNombreIn(List<String> nombres);

}

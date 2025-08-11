package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoAprendizRepositorio extends JpaRepository<GrupoAprendiz, Integer> {

    @Query("SELECT g FROM Grupo g WHERE g.id IN (SELECT ga.grupo.id FROM GrupoAprendiz ga WHERE ga.usuario.idUsuario = :idAprendiz)")
    List<Grupo> findGruposByAprendiz(@Param("idAprendiz") Long idAprendiz);

    @Query("SELECT ga.usuario FROM GrupoAprendiz ga WHERE ga.grupo.id = :idGrupo")
        List<Usuario> findUsuariosByGrupoId(@Param("idGrupo") int idGrupo);

    public List<GrupoAprendiz> findByUsuarioId(Long idUsuario);
    
    List<GrupoAprendiz> findByGrupo_Id(Integer id);
    
    boolean existsByUsuarioIdUsuarioAndGrupoId(Long idUsuario, int idGrupo);
    
    void deleteByGrupo_IdAndUsuario_IdUsuario(Integer idGrupo, Long idUsuario);
}

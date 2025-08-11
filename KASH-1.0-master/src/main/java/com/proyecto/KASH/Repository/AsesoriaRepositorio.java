package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.Usuario;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AsesoriaRepositorio extends JpaRepository<Asesoria, Integer> {

    List<Asesoria> findByGrupo(Grupo grupo);

    @Query("SELECT COUNT(a) > 0 FROM Asesoria a WHERE a.grupo = :grupo AND a.fecha = :fecha AND a.hora = :hora")
    boolean existsByGrupoAndFechaAndHora(@Param(value = "grupo") Grupo grupo, @Param(value = "fecha") LocalDate fecha, @Param(value = "hora") LocalTime hora);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Asesoria a "
            + "WHERE a.grupo.asesor = :asesor AND a.fecha = :fecha AND a.hora = :hora")
    boolean existsByAsesorAndFechaAndHora(@Param("asesor") Usuario asesor,
            @Param("fecha") LocalDate fecha,
            @Param("hora") LocalTime hora);

    @Query("SELECT a FROM Asesoria a WHERE a.fecha = :fecha AND a.hora = :hora AND (a.grupo.id = :idGrupo OR a.grupo.asesor.id = :idAsesor)")
    List<Asesoria> buscarConflictos(@Param(value = "fecha") LocalDate fecha, @Param(value = "hora") LocalTime hora, @Param(value = "idGrupo") int idGrupo, @Param(value = "idAsesor") Long idAsesor);

    List<Asesoria> findByGrupo_Asesor_IdUsuario(Long idAsesor);

    @Query("SELECT a FROM Asesoria a WHERE a.grupo.id = :idGrupo AND a.fecha > :fechaActual ORDER BY a.fecha ASC")
    List<Asesoria> findSiguientesAsesorias(@Param("idGrupo") int idGrupo, @Param("fechaActual") LocalDate fechaActual);
    
    @Query("SELECT a FROM Asesoria a WHERE a.grupo.id = :idGrupo AND a.fecha >= :fechaActual ORDER BY a.fecha ASC")
    List<Asesoria> findProximasAsesoriasPorGrupo(@Param("idGrupo") int idGrupo, @Param("fechaActual") LocalDate fechaActual);
    
    @Query("SELECT a FROM Asesoria a WHERE a.grupo.id = :idGrupo AND a.fecha = :fechaActual ORDER BY a.hora ASC")
    List<Asesoria> findAsesoriasPorGrupoYFecha(@Param("idGrupo") int idGrupo, @Param("fechaActual") LocalDate fechaActual);

    List<Asesoria> findByGrupo_AsesorAndCompletadaFalse(Usuario asesor);

    @Query("""
    SELECT a
    FROM Asesoria a
    JOIN a.grupo g
    JOIN g.aprendices ga
    WHERE ga.usuario = :usuario
    AND a.completada = false
""")
    List<Asesoria> findAsesoriasActivasPorAprendiz(@Param("usuario") Usuario usuario);

    public List<Asesoria> findByEstado(String inactivo);

    List<Asesoria> findByGrupoAndEstado(Grupo grupo, String estado);

    public Optional<Asesoria> findById(Long idAsesoria);

    @Query("SELECT a FROM Asesoria a WHERE a.grupo.asesor.idUsuario = :idAsesor ORDER BY a.fecha ASC")
    List<Asesoria> findAsesoriasPorAsesor(@Param("idAsesor") Long idAsesor);

    @Query("SELECT DISTINCT a.grupo FROM Asesoria a WHERE a.grupo.asesor.idUsuario = :idAsesor")
    List<Grupo> findDistinctGrupoByAsesorId(@Param("idAsesor") Long idAsesor);

    List<Asesoria> findByFechaBefore(LocalDate fecha);
    
    List<Asesoria> findByFechaBeforeAndEstadoNotAndCompletadaFalse(LocalDate fecha, String estado);

}

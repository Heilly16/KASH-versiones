package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Asistencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AprendizAsesoriaRepositorio extends JpaRepository<Asistencia, Long> {

    @Query("SELECT a FROM Asesoria a "
            + "JOIN a.grupo g "
            + "JOIN GrupoAprendiz ga ON ga.grupo = g "
            + "WHERE ga.usuario.id = :idAprendiz")
    List<Asesoria> findAsesoriasPorAprendiz(@Param("idAprendiz") Long idAprendiz);



}

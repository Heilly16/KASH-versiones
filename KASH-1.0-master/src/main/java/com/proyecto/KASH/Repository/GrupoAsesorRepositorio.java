package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Grupo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GrupoAsesorRepositorio extends JpaRepository<Grupo, Integer> {

    List<Grupo> findByNombreIn(List<String> nombres);

    @Query("SELECT g FROM Grupo g WHERE g.asesor.idUsuario = :idAsesor")
    List<Grupo> findGruposByAsesor(@Param("idAsesor") Long idAsesor);

}

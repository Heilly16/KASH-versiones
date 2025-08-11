package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.foro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface foroRepositorio extends JpaRepository<foro, Long> {

    foro findById(long id);

    @Query("SELECT f FROM foro f WHERE "
            + "LOWER(f.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(f.tema) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "STR(f.fecha) LIKE CONCAT('%', :filtro, '%') OR "
            + "CAST(f.idUsuario AS string) LIKE CONCAT('%', :filtro, '%')"
            + " OR CAST(f.trimestre AS string) LIKE CONCAT('%', :filtro, '%') ")
    List<foro> buscarPorFiltro(@Param("filtro") String filtro);
}

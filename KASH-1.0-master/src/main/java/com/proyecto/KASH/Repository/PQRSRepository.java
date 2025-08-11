
package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.PQRS;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PQRSRepository extends JpaRepository<PQRS, Long> {

    @Query("SELECT p FROM PQRS p WHERE "
            + "LOWER(p.nombre) LIKE CONCAT('%', :filtro, '%') OR "
            + "LOWER(p.email) LIKE CONCAT('%', :filtro, '%') OR "
            + "LOWER(p.tipo) LIKE CONCAT('%', :filtro, '%') OR "
            + "LOWER(p.detalles) LIKE CONCAT('%', :filtro, '%') OR "
            + "STR(p.estado) = UPPER(:filtro)")
    List<PQRS> buscarPorCriterio(@Param("filtro") String filtro);

}

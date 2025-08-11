package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.Prueba;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PruebaRepositorio extends JpaRepository<Prueba, Long>  {
    List<Prueba> findByGrupoId(Long grupoId);

    public List<Prueba> findByGrupo(Grupo grupo);
}

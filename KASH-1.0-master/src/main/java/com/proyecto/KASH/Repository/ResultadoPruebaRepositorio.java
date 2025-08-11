package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.ResultadoPrueba;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ResultadoPruebaRepositorio extends JpaRepository<ResultadoPrueba, Long> {
   
    Optional<ResultadoPrueba> findByAprendizIdAndPruebaId(Long aprendizId, Long pruebaId);


}

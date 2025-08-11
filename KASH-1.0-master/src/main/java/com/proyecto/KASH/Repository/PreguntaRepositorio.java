package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Pregunta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreguntaRepositorio extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByPruebaId(Long pruebaId);   
}

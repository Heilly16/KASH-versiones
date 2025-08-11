package com.proyecto.KASH.Repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.KASH.entidad.Respuesta;
import java.util.List;

@Repository
public interface RespuestaRepositorio extends JpaRepository<Respuesta, Long>{
   List<Respuesta> findByPreguntaId(Long preguntaId);
}

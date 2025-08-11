package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Comentario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepositorio extends JpaRepository<Comentario, Long> {
    List<Comentario> findByIdForo(int idForo);
}

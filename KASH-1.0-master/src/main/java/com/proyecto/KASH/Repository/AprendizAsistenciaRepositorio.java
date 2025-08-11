package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Asistencia;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AprendizAsistenciaRepositorio extends JpaRepository<Asistencia, Long>{
    List<Asistencia> findByUsuarioIdUsuario(Long idUsuario);
    List<Asistencia> findByUsuarioIdUsuarioAndFecha(Long idUsuario, LocalDate fecha);

    public List<Asistencia> findByUsuarioIdUsuarioAndAsesoriaId(Long idUsuario, int id);

}

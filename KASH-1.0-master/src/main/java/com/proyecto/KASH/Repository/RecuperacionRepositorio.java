package com.proyecto.KASH.Repository;


import com.proyecto.KASH.entidad.Recuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecuperacionRepositorio extends JpaRepository<Recuperacion, Long> {
    Optional<Recuperacion> findByCorreo(String correo);
    void deleteByCorreo(String correo);
}

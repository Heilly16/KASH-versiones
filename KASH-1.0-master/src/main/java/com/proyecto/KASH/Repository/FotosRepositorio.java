
package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.fotos;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author USER
 */
public interface FotosRepositorio extends JpaRepository<fotos,Integer>{
    fotos findByIdUsuario(Long idusuario);
}

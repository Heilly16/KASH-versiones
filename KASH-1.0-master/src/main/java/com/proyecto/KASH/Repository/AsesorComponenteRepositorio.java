package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AsesorComponenteRepositorio extends JpaRepository<Componente, Long>{
      // Consulta para obtener los componentes únicos por nombre
    @Query("SELECT DISTINCT c.nombre FROM Componente c")
    List<String> findDistinctComponentNames();
    
    // Buscar componentes por usuario
    List<Componente> findByUsuario_IdUsuario(Long idUsuario);
    
   void deleteByIdAndUsuario_IdUsuario(Long id, Long idUsuario);

    public List<Componente> findByUsuario(Usuario usuario);
}

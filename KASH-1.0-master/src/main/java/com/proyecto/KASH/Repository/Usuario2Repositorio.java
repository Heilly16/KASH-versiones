package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Usuario;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface Usuario2Repositorio extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdUsuario(Long idUsuario);

    // Agregar este método para buscar por usuario y contraseña
    Optional<Usuario> findByUsuarioAndPass(String usuario, String pass);

    List<Usuario> findByRolSeleccionado(String rolSeleccionado);

    boolean existsByUsuario(String usuario);

    Optional<Usuario> findByCorreo(String correo);

    public boolean existsByCorreo(String correo);

    @Query("SELECT u FROM Usuario u WHERE "
            + "CAST(u.idUsuario AS string) LIKE %:filtro% OR "
            + "LOWER(u.nombres) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.primerA) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.segundoA) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.tDocumento) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.trimestre) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.correo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.numero) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.usuario) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.rolSeleccionado) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.estado) LIKE LOWER(CONCAT('%', :filtro, '%')) OR "
            + "LOWER(u.etapa) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    List<Usuario> buscarPorFiltro(@Param("filtro") String filtro);

    @Query("SELECT DISTINCT r.aprendiz FROM RespuestaSeleccionada r WHERE r.prueba.id = :pruebaId")
    List<Usuario> findAprendicesQueRespondieronPrueba(@Param("pruebaId") Long pruebaId);

    Optional<Usuario> findByUsuario(String usuario);

    @Query("SELECT DISTINCT g.nombre FROM Grupo g")
    List<String> findDistinctComponentes();

    @Query("SELECT g.nombre, COUNT(g) FROM Grupo g GROUP BY g.nombre")
    List<Object[]> countGruposPorComponente();

}

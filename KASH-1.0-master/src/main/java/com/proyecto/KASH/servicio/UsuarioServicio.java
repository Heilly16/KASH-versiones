package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import java.util.Optional;  
import java.util.Map;


public interface UsuarioServicio {
    List<Usuario> ListarUsuario();
    Optional<Usuario> buscarPorIdUsuario(Long idUsuario);

    public void eliminarUsuario(Long idUsuario);

    public void actualizarUsuario(Usuario usuario);

    public List<Usuario> obtenerTodosLosUsuarios();

    public void cambiarContrasena(String correo, String nuevaContrasena);

    public Optional<Usuario> buscarPorRol(String coordinador);

    public Optional<Usuario> obtenerPorCorreo(String correo);
    
    Optional<Usuario> obtenerUsuarioPorCorreo(String correo);

    List<Usuario> buscarUsuariosPorGrupo(Long idGrupo);
    
    List<String> listarComponentes();
    
    Map<String, Integer> contarGruposPorComponente();
}

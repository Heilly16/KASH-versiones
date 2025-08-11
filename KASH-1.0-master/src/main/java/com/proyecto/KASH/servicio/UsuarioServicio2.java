package com.proyecto.KASH.servicio;

import com.itextpdf.text.DocumentException;
import com.proyecto.KASH.entidad.Usuario;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface UsuarioServicio2 {

    Optional<Usuario> findByUsuarioAndPass(String usuario, String pass);

    public ByteArrayInputStream generarReporteUsuarios(List<Usuario> usuarios) throws Exception;

    public Usuario buscarPorId(Long id);

    public ByteArrayInputStream generarReporteUsuarioIndividual(Usuario usuario) throws Exception;

    public List<Usuario> cargarUsuariosDesdeCSV(MultipartFile archivo) throws IOException;

    Optional<Usuario> findByUsuario(String usuario);

    public ByteArrayInputStream generarPdfPersonalizado(List<Usuario> usuarios, List<String> campos) throws DocumentException;

    List<Usuario> obtenerTodosLosUsuarios();
    
    List<Usuario> listarUsuarios();
    
    List<Usuario> buscarUsuarioPorFiltro(String filtro);
    
    List<Usuario> obtenerUsuariosPorRol(String rol);
    
    Optional<Usuario> obtenerUsuarioPorId(Long idUsuario);
    
    void guardarUsuario(Usuario usuario);
}

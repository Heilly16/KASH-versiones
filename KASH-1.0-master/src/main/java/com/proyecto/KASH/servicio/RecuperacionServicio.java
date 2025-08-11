package com.proyecto.KASH.servicio;

public interface RecuperacionServicio {
    String generarCodigo();
    boolean enviarCodigoRecuperacion(String correo);
    boolean validarCodigo(String correo, String codigo);
    void eliminarCodigo(String correo);
}

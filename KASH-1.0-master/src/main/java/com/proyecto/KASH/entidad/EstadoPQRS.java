package com.proyecto.KASH.entidad;


public enum EstadoPQRS {
    Activo,
    Inactivo;

    static {
        System.out.println("EstadoPQRS.Activo ordinal: " + Activo.ordinal());
        System.out.println("EstadoPQRS.Inactivo ordinal: " + Inactivo.ordinal());
    }
}

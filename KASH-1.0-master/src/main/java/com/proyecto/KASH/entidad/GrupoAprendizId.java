package com.proyecto.KASH.entidad;

import java.util.Objects;


 public class GrupoAprendizId {
    
    private int grupo;
    private Long usuario;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.grupo;
        hash = 53 * hash + Objects.hashCode(this.usuario);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GrupoAprendizId other = (GrupoAprendizId) obj;
        if (this.grupo != other.grupo) {
            return false;
        }
        return Objects.equals(this.usuario, other.usuario);
    }
    
    
    
}

package com.proyecto.KASH.entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="grupo_aprendiz")
@IdClass(GrupoAprendizId.class)
public class GrupoAprendiz {
    
    @Id
    @ManyToOne
    @JoinColumn(name="idgrupo")
    private Grupo grupo;
    
    @Id
    @ManyToOne
    @JoinColumn(name="idUsuario")
    private Usuario usuario;

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getIdUsuario() {
        return usuario != null ? usuario.getIdUsuario() : null;
    }
    
    public Usuario getAprendiz() {
        return usuario;
    }
    
}

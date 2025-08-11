package com.proyecto.KASH.entidad;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "programa")
public class Programa {

    @Id
    private Long id;

    @Column(name = "nomber", length = 30)
    private String nombre;
    
    @ManyToMany(mappedBy = "programas")
    private List<Componente> componentes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Componente> getComponentes() {
        return componentes;
    }

    public void setComponentes(List<Componente> componentes) {
        this.componentes = componentes;
    }
} 
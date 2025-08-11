package com.proyecto.KASH.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "rol")
public class Rol {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDl")
    private Long ID;

    @Column(name = "Nombre", nullable = false, unique = true)
    private String Nombre;

 
    //Getters and Setters

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }
    
}

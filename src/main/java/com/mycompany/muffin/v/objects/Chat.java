/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muffin.v.objects;

/**
 *
 * @author Aitor Piñeiro
 */
public class Chat {
    int id;
    String descripcion;
    User moderador;
    
    public Chat(int id, String descripcion, User moderador){
        this.id = id;
        this.descripcion = descripcion;
        this.moderador = moderador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getId() {
        return id;
    }

    public User getModerador() {
        return moderador;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setModerador(User moderador) {
        this.moderador = moderador;
    }
    
    
}

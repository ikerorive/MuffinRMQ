/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muffin.v.objects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aitor Piñeiro
 */
public class Chat {
    int id;
    int eventoID;
    String descripcion;
    String nombre;
    int moderador;

    List<Message> mensajes;
    
    public Chat(int id,int eventoID, String descripcion, int moderador){
        this.id = id;
        this.eventoID = eventoID;
        this.descripcion = descripcion;
        this.moderador = moderador;
        mensajes = new ArrayList<>();
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public String getNombre(){
        return nombre;
    }
    public void setMensajes(List<Message> mensajes) {
        this.mensajes = mensajes;
    }

    public List<Message> getMensajes() {
        return mensajes;
    }

    public void addMenssage(Message msg){
        mensajes.add(msg);
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setEventoID(int eventoID){
        this.eventoID = eventoID;
    }
    public int getEventoID(){
        return eventoID;
    }

    public int getId() {
        return id;
    }

    public int getModerador() {
        return moderador;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setModerador(int moderador) {
        this.moderador = moderador;
    }
    
    
}

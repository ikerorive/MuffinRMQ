/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muffin.v.objects;

import java.sql.Date;

/**
 *
 * @author Aitor Piñeiro
 */
public class Message {
    int id;
    Date dateMensaje;
    String mensaje;
    User user;
    Chat chat;
    
    public Message(int id, Chat chat, User user, Date dateMensaje, String mensaje){
        this.id = id;
        this.chat = chat;
        this.user = user;
        this.dateMensaje = dateMensaje;
        this.mensaje = mensaje;
    }

    public Chat getChat() {
        return chat;
    }

    public Date getDateMensaje() {
        return dateMensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setDateMensaje(Date dateMensaje) {
        this.dateMensaje = dateMensaje;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setUser(User user) {
        this.user = user;
    }
    

    @Override
    public String toString() {
        return this.user.getUsername() + ": "+this.mensaje;
    }
   
}

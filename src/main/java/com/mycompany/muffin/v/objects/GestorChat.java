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
public class GestorChat {
    List<Chat> listaChats;
    List<User> listaUsuarios;
    GestorBBDD conexion;
    
    
    public GestorChat(GestorBBDD conexion){
        listaChats = new ArrayList<Chat>();
        listaUsuarios =  new ArrayList<User>();
        this.conexion = conexion;
    }
    public void addChat(Chat c){
        listaChats.add(c);
    }
    public void deleteChat(Chat c){
        listaChats.remove(c);
    }
    
    public void addUser(User u){
        listaUsuarios.add(u);
    }
    public void deleteUser(User u){
        listaUsuarios.remove(u);
    }
    public User getUser(int index){
        return listaUsuarios.get(index);
    }
    public Chat getChat(int index){
        return listaChats.get(index);
    }
    public User getUserFromID(int id){
        for(User u: listaUsuarios){
            if(u.getId() == id) return u;
        }
        return null;
    }
    public Chat getChatFromID(int id){
        for(Chat c : listaChats){
            if(c.getId() == id) return c;
        }
        return null;
    }
    //leer de la bbdd

    public List<Chat> getListaChats() {
        return listaChats;
    }

    public void setListaChats(List<Chat> listaChats) {
        this.listaChats = listaChats;
    }

    public List<User> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<User> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
    
}

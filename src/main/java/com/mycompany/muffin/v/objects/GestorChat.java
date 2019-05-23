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
    GestorBBDD conexion;
    
    public GestorChat(){
        listaChats = new ArrayList<Chat>();
        conexion = new GestorBBDD();
    }
    
    //leer de la bbdd

    public List<Chat> getListaChats() {
        return listaChats;
    }

    public void setListaChats(List<Chat> listaChats) {
        this.listaChats = listaChats;
    }
    
}

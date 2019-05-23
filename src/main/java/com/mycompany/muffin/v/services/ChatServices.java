/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muffin.v.services;

import com.mycompany.muffin.v.objects.Chat;
import com.mycompany.muffin.v.objects.User;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Aitor Piñeiro
 */
@Path("generic")
public class ChatServices {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ChatServices
     */
    public ChatServices() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.muffin.v.services.ChatServices
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ChatServices
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
    //conexiones de la base de datos, add chat, getListaChats, gelListaMSG, addMSG, getListaUsuarios
    
    
    @GET
    @Produces("application/json")
    public List<Chat> obtenerChatsJSON() {
       // return Ciudades.instance.mostrar();
       return null;
    }
    
    @GET
    @Produces("application/json")
    public List<Chat> obtenerMensajesJSON() {
       // return Ciudades.instance.mostrar();
       return null;
    }
    
    @GET
    @Produces("application/xml")
    public List<Chat> obtenerChatsXML() {
       // return Ciudades.instance.mostrar();
          return null;
    }
    @GET
    @Produces("application/xml")
    public List<Chat> obtenerMensajesXML() {
       // return Ciudades.instance.mostrar();
          return null;
    }
    
    
    @PUT
    @Path("crear")
    @Consumes("text/plain")
    public void createChat(@PathParam("id") int id, @PathParam("descripcion") 
            String descripcion, @PathParam("user") User user) {
        
    }
}

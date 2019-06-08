/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import static com.google.protobuf.Any.parser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Date;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import jdk.nashorn.internal.parser.JSONParser;
import net.sf.json.JSONArray;
import objects.*;
import sun.misc.IOUtils;

/**
 * REST Web Service
 *
 * @author Aitor Piñeiro
 */
@Path("chat")
public class ChatResources {

    static App main;
    static GestorBBDD database;
    static ExecutorService poolWrite;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ChatResources
     */
    public ChatResources() {
        main = new App();
        main.loadDataFromDB();
        main.initializeMiddleware();
        database = main.getDatabaseConnection();
        poolWrite = main.getPoolWrite();
    }

    @POST
    @Path("/crearEvento")
    @Produces("text/plain")
    public void crearEvento(@QueryParam("creador") String creador,
            @QueryParam("description") String description, @QueryParam("name") String name,
            @QueryParam("imgUrl") String imgUrl, @QueryParam("maxSize") String maxSize,
            @QueryParam("date") String date, @QueryParam("latitude") String latitude, @QueryParam("longitude") String longitude, @QueryParam("eventType") int eventType) {

        Evento e = new Evento(creador, description, name, imgUrl, maxSize, date, latitude, longitude, eventType);
        main.getChats().addEvento(e);

        //insertar evento en bbdd
        String query = "INSERT INTO EVENTO" + "(CREADOR, DESCRIPTION, NAME, IMGURL, MAXSIZE, DATE, LATITUDE, LONGITUDE, EVENTTYPE) VALUES" + "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        poolWrite.submit(new Escritor(database, creador, description, name, imgUrl, Integer.parseInt(maxSize), date, Double.parseDouble(latitude), Double.parseDouble(longitude), eventType, query));
        //cargar de la bbdd
        main.leerEventos();
        //Añadir un chat
        Evento lastEvent = main.getChats().getLastEvent();
        Chat c = new Chat(lastEvent.getId(), lastEvent.getId(), lastEvent.getDescription(), 1);
        main.getChats().addChat(c);
        //insertar chat en bbdd
        String query2 = "INSERT INTO CHAT" + "(EVENTO, DESCRIPTION, MODERADO) VALUES" + "(?, ?, ?)";
        poolWrite.submit(new Escritor(database, c.getEventoID(), c.getDescripcion(), c.getModerador(), query2));
        //return "Evento insertado, chat creado y actualizado";
    }

    @GET
    @Path("/getListaEventos")
    @Produces("text/plain")

    public List<Evento> getListaEventos() {
        main.leerEventos();
        System.out.println(main.getChats().getListaEventos());
        return main.getChats().getListaEventos();
    }

    @GET
    @Path("/getEventoById")
    @Produces("text/plain")
    public Evento getEventoById(@QueryParam("id") int id) {
        main.leerEventos();
        return main.getChats().getEventoById(id);
    }

    @GET
    @Path("/getEventoByCreador")
    @Produces("text/plain")
    public Evento getEventoByCreator(@QueryParam("creator") String creator) {
        main.leerEventos();
        return main.getChats().getEventoByCreator(creator);
    }

    @GET
    @Path("/enviarMensaje")
    @Produces(MediaType.APPLICATION_JSON)
    public Response enviarMensaje(@QueryParam("userId") int userId, @QueryParam("username") String username, @QueryParam("mensaje") String mensaje, @QueryParam("chatId") int chatId) {

        List<Chat> listaChat = main.getChats().getListaChats();

        main.getPoolPusblishers().submit(new Publisher(userId, username, listaChat, mensaje, chatId));
        //  System.out.println("aaaaaaaaaaaaaaaaaaaa   "+main.getChats().getChat(chatId).getMensajes());
        List<Message> lm = main.getChats().getChat(chatId).getMensajes();
        ArrayList<String> enviar = main.getChats().enviarMensajeWebArray(chatId);
        System.out.println("aaaaaaaaaaaaaaaaaaaa   " + enviar);
        //return main.getChats().enviarMensajeWeb(chatId);
        // Response.ok(main.getChats().enviarMensajeWeb(chatId)).header("Access-Control-Allow-Origin", "*").build();
        // GenericEntity<List<String>> entity = new GenericEntity<List<String>>(enviar) {};
        GenericEntity<List<String>> entity = new GenericEntity<List<String>>(enviar) {
        };
        // return Response.ok(entity).header("Access-Control-Allow-Origin", "*").build();
        RespuestaMsg rm = new RespuestaMsg();
        rm.setArray(enviar);
        return Response.ok(rm).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/getMensajeInicio")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMensajeInicio(@QueryParam("chatId") int chatId) {

        List<Chat> listaChat = main.getChats().getListaChats();

        // main.getPoolPusblishers().submit(new Publisher(userId, username, listaChat, mensaje, chatId));
        //  System.out.println("aaaaaaaaaaaaaaaaaaaa   "+main.getChats().getChat(chatId).getMensajes());
        List<Message> lm = main.getChats().getChat(chatId).getMensajes();
        ArrayList<String> enviar = main.getChats().enviarMensajeWebArray(chatId);
        //System.out.println("aaaaaaaaaaaaaaaaaaaa   "+enviar);
        //return main.getChats().enviarMensajeWeb(chatId);
        // Response.ok(main.getChats().enviarMensajeWeb(chatId)).header("Access-Control-Allow-Origin", "*").build();
        // GenericEntity<List<String>> entity = new GenericEntity<List<String>>(enviar) {};
        GenericEntity<List<String>> entity = new GenericEntity<List<String>>(enviar) {
        };
        // return Response.ok(entity).header("Access-Control-Allow-Origin", "*").build();
        RespuestaMsg rm = new RespuestaMsg();
        rm.setArray(enviar);
        return Response.ok(rm).header("Access-Control-Allow-Origin", "*").build();
    }

    //eventos-> añadirEvento, getListaEventos, getEventoById, getEventoByCreator
//Chats -> añadirMensaje (rmq)
//Usuarios ->  getUsuarioDetails(usuario y passw), añadirAvatar
    @GET
    @Path("/copiaSeguridad")
    @Produces(MediaType.APPLICATION_JSON)
    public MensajesCopia copiaSeguridad(@QueryParam("username") String username) {
        String filename = "C:\\Users\\Public\\Muffin\\"+username+".json";
        Gson gson = new Gson();
        MensajesCopia data = null;
        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(filename));
             data = gson.fromJson(reader, MensajesCopia.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ChatResources.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

}

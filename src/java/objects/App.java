/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

/**
 *
 * @author Aitor Piñeiro
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Aitor Piñeiro
 */
@XmlRootElement
public class App {

    GestorBBDD database;
    GestorChat chats;
    ExecutorService poolWrite, poolRead, poolPublishers, poolSuscribers;
    ArrayList<Future<ResultSet>> listaFutures;
    List<String> exhangers;

    ConnectionFactory factory;

    public  App() {
        database = new GestorBBDD();
        chats = new GestorChat(database);
        poolWrite = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4);
        poolRead = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4);
        poolPublishers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4);
        poolSuscribers = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4);
        factory = new ConnectionFactory();
    }

    public void loadDataFromDB() {
        listaFutures = new ArrayList<>();
        ResultSet rs = null;
        Future<ResultSet> future = poolRead.submit(new Lector(database, "select * from chat"));

        try {
            rs = future.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }

        try {
            System.out.println("LEYENDO CHATS");
            while (rs.next()) {
                Chat c = new Chat(rs.getInt("id"), rs.getInt("evento"), rs.getString("description"), rs.getInt("moderado"));
                chats.addChat(c);
            }
            System.out.println("Chats cargados OK");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        rs = null;
        listaFutures.clear();
        System.out.println("nombres");
        for (Chat c : chats.getListaChats()) {
            future = poolRead.submit(new Lector(database, c.getEventoID(), "select * from evento where id=?"));
            try {
                ResultSet rss = future.get();
                while (rss.next()) {
                    String nombre = rss.getString("name");
                    System.out.println(nombre);
                    c.setNombre(nombre);
                }
            } catch (InterruptedException e) {
            } catch (SQLException e) {
            } catch (ExecutionException e) {
            }
        }

        rs = null;
        listaFutures.clear();

        try {
            System.out.println("Cargando mensajes...");
            List<Message> mensajes = new ArrayList<>();
           
            for (int i = 0; i < chats.getListaChats().size(); i++) {
                future = poolRead.submit(new Lector(database, chats.getChat(i).getId(), "select * from mensaje inner join chat on mensaje.chat = chat.id where chat.id= ?"));
                listaFutures.add(future);
            }
            for (int i = 0; i < listaFutures.size(); i++) {
                rs = listaFutures.get(i).get();
                while (rs.next()) {
                    Message m = new Message(rs.getInt("chat"), rs.getInt("user"), rs.getDate("dateMensaje"), rs.getString("mensaje"));
                    
                    Future<ResultSet> future2 = poolRead.submit(new Lector(database, m.getUserId(), "select * from user where id=?"));
                    User u = null;
                    ResultSet rs2 = null;
                    try {
                        rs2 = future2.get();
                        while (rs2.next()) {
                            u = new User(rs2.getInt("id"), rs2.getString("username"));
                            m.setUser(u);
                        }
                    } catch (InterruptedException e) {
                    } catch (ExecutionException e) {
                    } catch (SQLException e) {
                    }
                    m.setChat(chats.getChat(i));
                    chats.getChat(i).addMenssage(m);
                }
            }
            System.out.println("Mensajes OK");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        
        leerUsuarios();
        leerEventos();
        leerMsgCopia();
/*
        for (Chat c : chats.getListaChats()) {
            System.out.println("Chat: " + c.getId());
            System.out.println("   Mensajes: ");
            for (Message m : c.getMensajes()) {
                System.out.println(m.getMensaje() + ", escrito por " + m.getUser().getUsername());
            }
        }*/

    }
    
    public void leerMsgCopia(){
       List<MsgCopiaSeguridad> msg = new ArrayList<>();
       
        for(Chat c : chats.getListaChats()){
            for(Message m : c.getMensajes()){
                MsgCopiaSeguridad me = new MsgCopiaSeguridad(c.getNombre(), m.getDateMensaje(), m.getUser().getUsername(), m.getMensaje());
                c.getMensajesC().add(me);
            }
            
        }
        
        for(Chat c : chats.getListaChats()){
            System.out.println("Evento: "+c.getNombre());
            for(MsgCopiaSeguridad m : c.getMensajesC()){
                System.out.println("        "+m);
            }
        }
         
    }

    public void leerUsuarios() {
        Future<ResultSet> future = poolRead.submit(new Lector(database, "select * from user"));
        ResultSet rs;
        listaFutures.add(future);
        List<User> usuarios = new ArrayList<>();
        try {
            System.out.println("Cargando usuarios...");
            rs = future.get();
            if (rs != null) {
                while (rs.next()) {
                    User u = new User(rs.getInt("id"), rs.getString("username"));
                    usuarios.add(u);
                }
              
            }
            System.out.println("Usuarios cargados");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        
        chats.setListaUsuarios(usuarios);
      
        try{
            for(Chat c : chats.getListaChats()){
            rs = null;
            future = poolRead.submit(new Lector(database, c.getId(), 1, "select * from userevento where evento=? and interes=?"));
            rs = future.get();
            while(rs.next()){
                int id = rs.getInt("user");
                System.out.println("interesado: "+id+", en el chat" + c.getId());
            
                 c.getUsuariosSuscritos().add(id);
            }
         
           
        }   
        }catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
       
         
    }

    public void leerEventos() {
        Future<ResultSet> future = null;
        ResultSet rs = null;
        future = poolRead.submit(new Lector(database, "select * from evento"));
        listaFutures.add(future);
        List<Evento> listaEventos = new ArrayList<>();
        try {
            System.out.println("cargando eventos...");
            rs = future.get();
            while (rs.next()) {
                Evento e = new Evento(rs.getInt("id"), rs.getString("creador"), rs.getString("description"),
                        rs.getString("name"), rs.getString("imgUrl"), rs.getString("maxSize"), rs.getString("date"),
                        rs.getString("latitude"), rs.getString("longitude"), rs.getInt("eventType"));
                listaEventos.add(e);
            }
            chats.setListaEventos(listaEventos);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        for (Evento e : listaEventos) {
            System.out.println(e.getCreador());
        }
    }

    public void initializeMiddleware() {

        //Crear exchangers de tipo fanout. 1 exchanger por chat
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            System.out.println("exchanger");
            for (Chat c : chats.getListaChats()) {
                System.out.println(c.getNombre());
                channel.exchangeDeclare(c.getNombre(), "fanout");

            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

        /*Crear un suscriber por Usuario. 1 cola por usuario. Solo esta operativo el usuario 1 que inserta
        en la bbdd los mensajes. Se hacen varios suscribers para futuras implementaciones como por ejemplo 
        enviar mensajes directos de un usuario a otro
        */
        for (User u : chats.getListaUsuarios()) {
            poolSuscribers.submit(new Suscriber(u, chats, this));
        }
        
        //poolPublishers.submit(new Publisher(1, "admin", chats.getListaChats(), "preba", 1));
    }

    public GestorChat getChats() {
        return chats;
    }

    public ExecutorService getPoolWrite() {
        return poolWrite;
    }

    public ExecutorService getPoolPusblishers() {
        return poolPublishers;
    }

    public GestorBBDD getDatabaseConnection() {
        return database;
    }

    public ExecutorService getPoolRead(){
        return poolRead;
    }
    /*
    public static void main(String[] args){
        App main = new App();
        main.loadDataFromDB();
        main.initializeMiddleware();
    }*/
}


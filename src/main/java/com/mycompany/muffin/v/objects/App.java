package com.mycompany.muffin.v.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class App {
    GestorBBDD database;
    GestorChat chats;
    ExecutorService poolWrite, poolRead;
    ArrayList<Future<ResultSet>> listaFutures;
    
    List<String> exhangers;
    Map<String, String> colas;
    
    ConnectionFactory factory;
    
    public App(){
        database = new GestorBBDD();
        chats = new GestorChat(database);
        poolWrite = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/2);
        poolRead  = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/2);
    }
    
    public void loadDataFromDB(){
        listaFutures = new ArrayList<>();
        ResultSet rs = null;
        Future<ResultSet> future = poolRead.submit(new Lector(database,"select * from chat"));
        listaFutures.add(future);
        
        try{
              rs = listaFutures.get(0).get();
        } catch(InterruptedException e){
            
        }catch(ExecutionException e){
            
        }
       
        //No tienen aun la lista de mensajes ni los usuarios suscritos a ese chat
        if(rs!=null) {
            try {
                while(rs.next())  {
                        Chat c = new Chat(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4));
                        System.out.println("Id: "+c.getId());
                        chats.addChat(c);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
             System.out.println("Chats cargados");
	}
        
        rs = null;
        listaFutures.clear();
        for(Chat c: chats.getListaChats()){
            future = poolRead.submit(new Lector(database,c.getEventoID(),"select name from evento where id=?"));
            try{
                String nombre = future.get().getString(0);
                c.setNombre(nombre);
            }catch(InterruptedException e){
                
            } catch(SQLException e){
                
            } catch(ExecutionException e){
                
            }
            
            
        }
        
        
        //Leer lista de mensajes por cada chat
        rs = null;
        listaFutures.clear();
        try {
            System.out.println("Cargando mensajes...");
            List<Message> mensajes = new ArrayList<>();
            for(int i = 0; i < chats.getListaChats().size(); i++){
                future = poolRead.submit(new Lector(database, chats.getChat(i).getId(), "select * from mensaje inner join chat on mensaje.chat = chat.id where chat.id= ?"));
                listaFutures.add(future);
            }
            
            for(int i = 0; i < listaFutures.size();i++){
                rs = listaFutures.get(i).get();
                if(rs!=null){
                    while(rs.next()){
                    Message m = new Message(rs.getInt(0), rs.getInt(1), rs.getInt(2), rs.getDate(3), rs.getString(4));
                    addUser(m);
                    m.setChat(chats.getChat(i));
                    mensajes.add(m);    
                }
                chats.getChat(i).setMensajes(mensajes);
                mensajes.clear();
                }
   
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            
        } catch (ExecutionException e){
            
        }
        System.out.println("Mensajes cargados");
        
        //Leer lista de usuarios que hay por cada chat
        listaFutures.clear();
        future = null;
        rs = null;
        future = poolRead.submit(new Lector(database,"select * from user"));
        listaFutures.add(future);
        List<User> usuarios = new ArrayList<>();
        try{
             System.out.println("Cargando usuarios...");
            rs = future.get();
            if(rs!=null){
                while(rs.next()){
                User u = new User(rs.getInt(0), rs.getString(2));
                usuarios.add(u);
            }
            chats.setListaUsuarios(usuarios);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            
        } catch (ExecutionException e){
            
        }
        System.out.println("Usuarios cargados");  
    }
    
    public void initializeMiddleware(){
        exhangers = new ArrayList<>();
        colas = new HashMap();
        Connection connection = null;
        Channel channel = null;
        
        //Crear un exchanger por chat
        for(Chat c : chats.getListaChats()){
            try {
                connection = factory.newConnection();
                channel = connection.createChannel();
                channel.exchangeDeclare(c.getNombre(), "direct");
            } catch(IOException e){
            } catch(TimeoutException e){
            }
        }
        //Si quisieramos meter nuevos exhangers, fanout o asi aqui.
        
        //Crear una cola por usuario y bindearlo con los exchangers
        for(User u : chats.getListaUsuarios()){
              try {
                    String queueName = channel.queueDeclare().getQueue();
                    for(Chat c : chats.getListaChats()){
                         channel.queueBind( queueName,c.getNombre(), u.getUsername());
                    }
            } catch(IOException e){
            } 
            
        }
    }
    
    public class MiConsumer extends DefaultConsumer {
        public MiConsumer(Channel channel) {
                super(channel);
        }
        public void handleDelivery(String consumerTag, Envelope envelope,
         AMQP.BasicProperties properties, byte[] body) throws IOException {

                String message = new String(body, "UTF-8");
                System.out.println("Recibido: "+message);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
		
    }
 
    public void addUser(Message m){
        Future<ResultSet> future = poolRead.submit(new Lector(database,m.getUserId(),"select * from user where id=?"));
        ResultSet rs = null;
        User u = null;
        try{
            rs = future.get();
            u = new User(rs.getInt(0), rs.getString(2));
        }catch (InterruptedException e){
            
        } catch (ExecutionException e){
            
        }catch (SQLException e){
            
        }
        m.setUser(u);        
    }
    
    public static void main(String[] args) {
        App main = new App();
        main.loadDataFromDB();
        main.initializeMiddleware();
    }

}

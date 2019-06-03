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
	    ExecutorService poolWrite, poolRead, poolPublishers, poolSuscribers;
	    ArrayList<Future<ResultSet>> listaFutures;
	    
	    List<String> exhangers;
	    
	    ConnectionFactory factory;
	    
	    public App(){
	        database = new GestorBBDD();
	        chats = new GestorChat(database);
	        poolWrite = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/4);
	        poolRead  = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/4);
	        poolPublishers  = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/4);
	        poolSuscribers  = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()/4);
	        factory = new ConnectionFactory();
	    }
	    
	    public void loadDataFromDB(){
	        listaFutures = new ArrayList<>();
	        ResultSet rs = null;
	        Future<ResultSet> future = poolRead.submit(new Lector(database,"select * from chat"));
	           
	        try{
	              rs = future.get();
	        } catch(InterruptedException e){
	            
	        }catch(ExecutionException e){
	            
	        }
	       
	        //No tienen aun la lista de mensajes ni los usuarios suscritos a ese chat
	        
	        try {
	               System.out.println("LEYENDO CHATS");
	            while(rs.next())  {
	                System.out.println(rs.getInt("id"));
	                    Chat c = new Chat(rs.getInt("id"), rs.getInt("evento"), rs.getString("description"), rs.getInt("moderado"));
	                    chats.addChat(c);
	            }
	              System.out.println("Chats cargados OK");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	       
		
	         //CORREGIDO Y FUNCIONANDO
	        rs = null;
	        listaFutures.clear();
	        for(Chat c: chats.getListaChats()){
	            System.out.println(c.getEventoID());
	            future = poolRead.submit(new Lector(database,c.getEventoID(),"select * from evento where id=?"));
	            try{
	                ResultSet rss = future.get();
	                while(rss.next()){
	                    String nombre = rss.getString("name");
	                     c.setNombre(nombre);
	                }
	            }catch(InterruptedException e){
	            } catch(SQLException e){
	            } catch(ExecutionException e){
	            } 
	        }
	        
	        
	        //Leer lista de mensajes por cada chat
	        rs = null;
	        listaFutures.clear();
	        
	        //MENSAJES CORREGIDO Y FUNCIONANDO
	        try {
	            System.out.println("Cargando mensajes...");
	            List<Message> mensajes = new ArrayList<>();
	            for(int i = 0; i < chats.getListaChats().size(); i++){
	                future = poolRead.submit(new Lector(database, chats.getChat(i).getId(), "select * from mensaje inner join chat on mensaje.chat = chat.id where chat.id= ?"));
	                listaFutures.add(future);
	            }
	            
	            for(int i = 0; i < listaFutures.size();i++){
	                rs = listaFutures.get(i).get();  
	                while(rs.next()){
	                    Message m = new Message(rs.getInt("id"), rs.getInt("chat"), rs.getInt("user"), rs.getDate("dateMensaje"), rs.getString("mensaje"));
	                    Future<ResultSet> future2 = poolRead.submit(new Lector(database,m.getUserId(),"select * from user where id=?"));
	                    User u = null;
	                    ResultSet rs2 = null;
	                    try{
	        	            rs2 = future2.get();
	        	            while(rs2.next()) {
	        	            	u = new User(rs2.getInt("id"), rs2.getString("username"));
	  	        	            m.setUser(u);
	        	            }   
	        	        }catch (InterruptedException e){
	        	            
	        	        } catch (ExecutionException e){
	        	            
	        	        }catch (SQLException e){
	        	            
	        	        }
	                    m.setChat(chats.getChat(i));    
	                    chats.getChat(i).addMenssage(m);;
	                }        
	            }
	            System.out.println("Mensajes OK");
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e){
	        } catch (ExecutionException e){
	        }
	        
	        // CORREGIDO
 
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
	                User u = new User(rs.getInt("id"), rs.getString("username"));
	                usuarios.add(u);
	            }
	            chats.setListaUsuarios(usuarios);
	            }
	            System.out.println("Usuarios cargados");  
	        }catch (SQLException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e){         
	        } catch (ExecutionException e){        
	        }
	        
	        for(Chat c: chats.getListaChats()){
	            System.out.println("Chat: "+c.getId());
	            System.out.println("   Mensajes: ");
	            for(Message m : c.getMensajes()){
	                System.out.println("   "+m.getId() +", "+ m.getMensaje()+ ", escrito por "+m.getUser().getUsername());
	            }
	            System.out.println("   "+"Usuarios suscritos (no suscritos por chats)");
	            for(User u: chats.getListaUsuarios()) {
	            	System.out.println("   "+"Id "+u.getId() +", Nombre: "+u.getUsername());
	            }
	        }
	        
	    }
	    
	    public void initializeMiddleware(){
	    	
	    	try(Connection connection = factory.newConnection()){
				Channel channel = connection.createChannel();
				for(Chat c : chats.getListaChats()) {
					channel.exchangeDeclare(c.getNombre(), "fanout");
				}
	    	} catch (IOException | TimeoutException e) {
				e.printStackTrace();
			} 
	    	
	    	//Prueba, funciona
	    	/*
	    	Publisher publish1 = new Publisher(chats.getListaUsuarios().get(0).getId(), 
	    			chats.getListaUsuarios().get(0).getUsername(), chats.getListaChats());
	    	
	    	Suscriber suscr = new Suscriber(chats.getListaUsuarios().get(1).getId(), 
	    			chats.getListaUsuarios().get(1).getUsername(), chats.getListaChats());
	    	
	    	publish1.publish(1, "sdfghjkl");*/
	    	
	    	for(User u : chats.getListaUsuarios()) {
	    		poolPublishers.submit(new Publisher(u.getId(), u.getUsername(), chats.getListaChats()));
	    		poolPublishers.submit(new Suscriber(u.getId(), u.getUsername(), chats.getListaChats()));
	    	}
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	/*
	        exhangers = new ArrayList<>();
	        colas = new HashMap();
	        Channel channel = null;
	        Connection connection;
	        
	        //Crear un exchanger por chat
	        for(Chat c : chats.getListaChats()){
	            try {
	            	 connection = factory.newConnection();
	                channel = connection.createChannel();
	                channel.exchangeDeclare(c.getNombre(), "direct");
	            } catch(IOException | TimeoutException e){
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
	            
	        }*/
	    }
	    
	    
	    
	    public static void main(String[] args) {
	        App main = new App();
	        main.loadDataFromDB();
	        main.initializeMiddleware();
	    }
}
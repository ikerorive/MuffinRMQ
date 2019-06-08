/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Date;



/**
 *
 * @author Aitor Piñeiro
 */
public class Suscriber extends Thread {
    int id; //userID
    String username, queueName;
    ConnectionFactory factory;
    GestorChat chats;
  
    Channel channel = null;
    ExecutorService poolWrite;
    App main;
    User u;

    public Suscriber(User u, GestorChat chats, App main) {
        this. u = u;
        this.id = u.getId();
        this.username = u.getUsername();

        this.chats = chats;
        this.main = main;
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        suscribe();
    }


    public synchronized void suscribe() {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            queueName = channel.queueDeclare(username, false, false, false, null).getQueue();
            //bindear esta cola con los chat suscritos. el nombre del exchanger = nombre de chat
            
            //Usuario 1 suscrito a todos, usaurio admin para guardar en bbdd
             System.out.println("Usuario para bindear: "+u.getId());
            if (u.getId() == 1) {
                for (Chat c : chats.getListaChats()) {
                    channel.queueBind(queueName, c.getNombre(), "");
                }
            } else {
                for (Chat c : chats.getListaChats()) {
                   System.out.println("Id del chat: "+c.getId());
                    System.out.println("ousuarios suscritos:");
                    for(int h : c.getUsuariosSuscritos()){
                        System.out.println(h);
                    }
                    if(c.getUsuariosSuscritos().contains(u.getId())){
                        System.out.println("Usuario: "+c.getId()+" bindeado con exchanger: "+c.getNombre());
                        channel.queueBind(queueName, c.getNombre(), "");
                    }
                    
                }
            }
            
            MiConsumer consumer = new MiConsumer(channel, main, u, chats);
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            if (channel != null) {
                try {
                    channel.close();
                    if (connection != null)  connection.close();
                } catch (IOException | TimeoutException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public class MiConsumer extends DefaultConsumer {
        App main;
        User user;
        GestorChat chats;
        final String path = "C:/Users/Public/muffin/";

        public MiConsumer(Channel channel, App main, User user, GestorChat chats) {
            super(channel);
            this.main = main;
            this.user = user;
            this.chats = chats;
        }
        public synchronized void handleDelivery(String consumerTag, Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {

            String message = new String(body, "UTF-8");

            String[] a = message.split("-");
            int chatId = Integer.parseInt(a[0]);
            int userId = Integer.parseInt(a[1]);
            String txt = a[2];
            java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            
            if (this.user.getId() == 1) {
               // System.out.println(Thread.currentThread().getId());
                System.out.println("Recibido-----> ");
                System.out.println("Numero chat: " + chatId + ", Numero usuario: " + userId + ", mensaje: " + txt);
                
                String query = "INSERT INTO MENSAJE" + "(CHAT, USER, DATEMENSAJE, MENSAJE) VALUES" + "(?, ?, ?, ?)";
                GestorBBDD database = main.getDatabaseConnection();
                 main.getChats().getChatFromID(chatId).addMenssage(new Message(chatId, userId, date, txt));
                main.getPoolWrite().submit(new Escritor(database, chatId, userId, date, txt, query)); 
            }
            else{
                List<MsgCopiaSeguridad> mensajesC = new ArrayList<>();
                List<Integer> suscritoAChat = new ArrayList<>();
                //meter en una  lista todos los chats a los k esta suscrito
                for(Chat c : chats.getListaChats()){
                    for(int u : c.getUsuariosSuscritos()){
                        if(u == user.getId()){
                            suscritoAChat.add(c.getId());
                        }
                    }
                }
                //copiar todos los mensajes de los chats en los k este suscrito
                for(Chat c : chats.getListaChats()){
                    if(suscritoAChat.contains(c.getId())){
                        for(MsgCopiaSeguridad msg : c.getMensajesC()){
                            mensajesC.add(msg);
                        }
                    }
                }
                
                if(mensajesC!=null && mensajesC.size()!= 0){
                    MensajesCopia mensajes = new MensajesCopia(mensajesC);
                    //String chat, Date date, String username, String message
                    //mensajes.getMsgCopiaSeguridad().add(new MsgCopiaSeguridad(chatId,));
                    
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(mensajes);
                    System.out.println(json);
                    
                     
	        BufferedWriter writer;
                    String file = path + user.getUsername() + ".json";
                    try {
                            writer = new BufferedWriter(new FileWriter(file));
                            writer.write(json);
                            writer.close();
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
     
                }
    
            }

        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}


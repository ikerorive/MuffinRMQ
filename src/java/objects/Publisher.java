/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 *
 * @author Aitor Piñeiro
 */
public class Publisher extends Thread{
    Scanner teclado;
    int id, chatId;
    String username, queueName, mensaje;
    ConnectionFactory factory;
    List<Chat> chats;
    Channel channel = null;

    public Publisher(int id, String username, List<Chat> chats, String mensaje, int chatId) {
        teclado = new Scanner(System.in);
        this.id = id;
        this.username = username;
        this.chats = chats;
        this.mensaje = mensaje;
        this.chatId = chatId;
        factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            Connection connection = factory.newConnection();
            channel = connection.createChannel();
            publish(chatId, mensaje);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public synchronized void publish(int chatId, String message) {
        String chat = "";
        String mensaje = chatId + "-" + this.id + "-" + message;

        for (Chat c : chats) {
            if (c.getId() == chatId) {
                chat = c.getNombre();
            }

        }
        System.out.println(chat);
        try {
            channel.basicPublish(chat, "", null, mensaje.getBytes());
            System.out.println("enviado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

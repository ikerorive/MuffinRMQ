/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muffin.v.objects;

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
	int id;
	String username;
	ConnectionFactory factory;
	List<Chat> chats;
	Channel channel = null;
	 String queueName ;
	
	public Publisher(int id, String username, List<Chat> chats) {
		teclado = new Scanner(System.in);
		this.id = id;
		this.username = username;
		this.chats = chats;
		factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		try{
			Connection connection = factory.newConnection();
			channel = connection.createChannel();
			/*queueName = channel.queueDeclare().getQueue();
             for(Chat c : chats){
                  channel.queueBind( queueName,c.getNombre(), username);
             }*/	
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

	public void setChats(List<Chat> chats) {
		this.chats = chats;
		/*for(Chat c : chats){
            try {
				channel.queueBind( queueName,c.getNombre(), username);
			} catch (IOException e) {
				e.printStackTrace();
			}
       }*/
	}

	public synchronized void publish(int chatId, String message) {
		String chat = "";
		String mensaje = username + ", " + message;
		
		for(Chat c : chats) {
			if(c.getId() == chatId) chat = c.getNombre();
		}
		try {
			channel.basicPublish(chat,this.username, null, mensaje.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

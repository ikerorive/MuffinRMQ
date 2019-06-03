/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muffin.v.objects;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


/**
 *
 * @author Aitor Piñeiro
 */
public class Suscriber extends Thread {
    int id; //userID
	String username;
	ConnectionFactory factory;
	List<Chat> chats;
	Channel channel = null;
	String queueName;
	
	
	public Suscriber(int id, String username, List<Chat> chats) {
		this.id = id;
		this.username = username;
		this.chats = chats;
		
		factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		suscribe();
	}
	public void setChats(List<Chat> chats) {
		this.chats = chats;
		for(Chat c : chats){
            try {
				channel.queueBind( queueName,c.getNombre(), username);
			} catch (IOException e) {
				e.printStackTrace();
			}
       }
	}
	
	public synchronized void suscribe() {
		Connection connection = null;
		Channel channel = null;
		try{
			connection = factory.newConnection();
			channel = connection.createChannel();
			for(Chat c : chats) {
				channel.exchangeDeclare(c.getNombre(), "fanout");
				queueName = channel.queueDeclare().getQueue();
				channel.queueBind(queueName, c.getNombre(), c.getNombre());
			}
			
			  
		    MiConsumer consumer = new MiConsumer (channel);
		    channel.basicConsume(queueName, true, consumer);
	} catch (IOException | TimeoutException e) {
			
			e.printStackTrace();
			
			if (channel != null) {
				try {
					channel.close();
					if (connection != null) connection.close();
				} catch (IOException | TimeoutException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		
		}
	}
	
	public class MiConsumer extends DefaultConsumer {
		
		
		public MiConsumer(Channel channel) {
			super(channel);
			
			
		}
		public synchronized void handleDelivery(String consumerTag, Envelope envelope,
                 AMQP.BasicProperties properties, byte[] body) throws IOException {
			
			String message = new String(body, "UTF-8");
			System.out.println("Recibido: "+message);
			
		}
		
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}

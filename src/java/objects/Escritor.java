/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;


import DTO.UserCaracteristics;
import java.sql.Date;
import java.sql.SQLException;


/**
 *
 * @author Aitor Piñeiro
 */
public class Escritor extends Thread{
   
    GestorBBDD connection;
    int queryType;
    String query, valueString;
    int valueInt;

    //Mensaje
    int chatId, userId;
    Date date;
    String message;
    //Evento
    String creador, description, name, imgUrl, datee;
    double latitude, longitude;
    int maxSize, eventType;
    //chat
    int evento, moderado;
    //user
    int tipoUsuario;
    String username, password, salt, email;
    //  Blob avatar;


    //putUsuario
    UserCaracteristics user;
    float resultado;
    
    //putResto
    int id;
    int idCategoria;
    float porcentaje;
    
    //añadirEventoUser
    int idEvento;
    int idInteres;
    
    public Escritor(GestorBBDD connection, String query) {
        this.connection = connection;
        this.query = query;
        queryType = 0;
    }

    public Escritor(GestorBBDD connection, int valueInt, String query) {
        this.connection = connection;
        this.query = query;
        this.valueInt = valueInt;
        queryType = 1;
    }
    
    
    public Escritor(GestorBBDD connection, String valueString, String query) {
        this.connection = connection;
        this.query = query;
        this.valueString = valueString;
        queryType = 2;
    }
   
    //Para insertar mensajes en la bbdd. Esto se gestiona con rmq
    public Escritor(GestorBBDD connection, int chatId, int userId, Date date, String message, String query) {
        this.connection = connection;
        this.query = query;
        queryType = 3;
        this.userId = userId;
        this.chatId = chatId;
        this.date = date;
        this.message = message;

    }
    
    //Para insertar eventos en la bbdd
    public Escritor(GestorBBDD connection, String creador, String description, String name, String imgUrl, int maxSize, String datee, double latitude, double longitude, int eventType, String query) {
        this.connection = connection;
        this.query = query;
        queryType = 4;
        this.creador = creador;
        this.description = description;
        this.name = name;
        this.imgUrl = imgUrl;
        this.maxSize = maxSize;
        this.datee = datee;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventType = eventType;

    }
    
    //Para insertar chats en la bbdd
    public Escritor(GestorBBDD connection, int evento, String description, int moderado, String query) {
        this.connection = connection;
        this.evento = evento;
        this.description = description;
        this.moderado = moderado;
        this.query = query;
        queryType = 5;
    }
    
    //Para insertar usuarios en la bbdd
    public Escritor(GestorBBDD connection, int tipoUsuario, String username, String password, String email, String salt, String query) {
        this.connection = connection;
        this.tipoUsuario = tipoUsuario;
        this.username = username;
        this.password = password;
        this.email = email;
        this.salt = salt;
        // this.avatar = avatar;
        this.query = query;
        queryType = 6;
    }  
    
    //Para update usuario en la bbdd
    public Escritor(GestorBBDD connection, float resultado, UserCaracteristics user, String query) {
        this.connection = connection;
        this.resultado = resultado;
        this.user = user;
        this.query = query;
        queryType = 7;
    }  
    
    
    //Para update usuarios en la bbdd
    public Escritor(GestorBBDD connection, float porcentaje, int id, int idCategoria, String query) {
        this.connection = connection;
        this.porcentaje = porcentaje;
        this.id = id;
        this.idCategoria = idCategoria;
        this.query = query;
        queryType = 8;
    }  
   
    //Para update usuarios en la bbdd
    public Escritor(GestorBBDD connection, int id, int idCategoria,float porcentaje, String query) {
        this.connection = connection;
        this.porcentaje = porcentaje;
        this.id = id;
        this.idCategoria = idCategoria;
        this.query = query;
        queryType = 9;
    }  
    
    //Para update usuarios en la bbdd
    public Escritor(GestorBBDD connection, int idEvento, int id,int idInteres, String query) {
        this.connection = connection;
        this.idEvento = idEvento;
        this.id = id;
        this.idInteres = idInteres;
        this.query = query;
        queryType = 10;
    } 
    
    @Override
    public void run() {
        try {
            switch (queryType) {
                case 0:
                    connection.escribir(query);
                    break;
                case 1:
                    connection.escribir(valueInt, query);
                    break;
                case 2:
                    connection.escribir(valueString, query);
                    break;
                case 3:
                    connection.escribirMensaje(chatId, userId, date, message, query);
                    break;
                case 4:
                    connection.escribirEvento(creador, description, name, imgUrl, maxSize, datee, latitude, longitude, eventType, query);
                    break;
                case 5:
                    connection.escribirChat(evento, description, moderado, query);
                    break;
                case 6:
                    connection.escribirUser(tipoUsuario, username, password, email, salt, query);
                    break;
                case 7:
                    connection.escribir(resultado,user, query);
                    break;
                case 8:
                    connection.escribir(porcentaje,id,idCategoria,query);
                    break;
                case 9:
                    connection.escribir(id,idCategoria,porcentaje,query);
                    break;
                case 10:
                    connection.escribir(idEvento,id,idInteres,query);
                    break;
                default:
                    break;
            }
        } catch (SQLException a) {
        } catch (InterruptedException e) {
        }

    }
    
}


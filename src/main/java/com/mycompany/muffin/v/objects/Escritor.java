/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.muffin.v.objects;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Aitor Piñeiro
 */
public class Escritor extends Thread{
      
    GestorBBDD connection;
    int queryType; //0 only query; 1 integer and query; 2 String and query
    
    String query;
    int valueInt;
    String valueString;
    
    public Escritor(GestorBBDD connection, String query){
        this.connection = connection;
        this.query = query;
        queryType = 0;
    }
    public Escritor(GestorBBDD connection, int valueInt, String query){
        this.connection = connection;
        this.query = query;
        this.valueInt = valueInt;
        queryType = 1;
    }
    public Escritor(GestorBBDD connection, String valueString, String query){
        this.connection = connection;
        this.query = query;
        this.valueString = valueString;
        queryType = 2;
    }
    @Override
    public void run() {
        try{
            switch(queryType){
            case 0: connection.escribir(query);break;
            case 1: connection.escribir(valueInt, query); break;
            case 2: connection.escribir(valueString, query); break;
            default: break;
            }
        }catch(SQLException a){    
        }catch(InterruptedException e){      
        }
        
    }
    
}

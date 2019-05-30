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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Aitor Piñeiro
 * Hay que hacer de esto un callable para devolver el ResultSet de la consulta. Return resultSet 
 */
public class Lector implements Callable<ResultSet>{
 
    GestorBBDD connection;
    int queryType; //0 only query; 1 integer and query; 2 String and query
    
    String query;
    int valueInt;
    String valueString;
    
    public Lector(GestorBBDD connection, String query){
        this.connection = connection;
        this.query = query;
        queryType = 0;
    }
    public Lector(GestorBBDD connection, int valueInt, String query){
        this.connection = connection;
        this.query = query;
        this.valueInt = valueInt;
        queryType = 1;
    }
    public Lector(GestorBBDD connection, String valueString, String query){
        this.connection = connection;
        this.query = query;
        this.valueString = valueString;
        queryType = 2;
    }

    public ResultSet makeQuery(){
        ResultSet rs = null;
        System.out.println("Tipo de query: "+queryType);
         try{
            switch(queryType){
            case 0: rs = connection.leer(query);break;
            case 1: rs = connection.leer(valueInt, query); break;
            case 2: rs = connection.leer(valueString, query); break;
            default: break;
            }
        }catch(SQLException a){
            
        }catch(InterruptedException e){
            
        }
         return rs;
    }
    
    @Override
    public ResultSet call() throws Exception {
       return makeQuery();
    }
  
}

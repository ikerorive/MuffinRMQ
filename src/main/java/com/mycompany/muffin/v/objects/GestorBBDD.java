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
import java.util.concurrent.Semaphore;

/**
 *
 * @author Aitor Piñeiro
 */
public class GestorBBDD {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    Connection connection;
    Semaphore candado;
    
    public GestorBBDD(){
        candado = new Semaphore(1);
        this.openConnection();
    }
    
    public ResultSet leer(int value, String query) throws InterruptedException, SQLException{
        candado.acquire();
        PreparedStatement  ps = prepareStatement2(value,query);        
        ResultSet rs = ps.executeQuery();
        candado.release();
        return rs;
    }
    
    public ResultSet leer(String value, String query) throws InterruptedException, SQLException{
        candado.acquire();
        PreparedStatement  ps = prepareStatement2(value,query);
        ResultSet rs = ps.executeQuery();   
        candado.release();
        return rs;
     }
    
    public ResultSet leer(String query) throws InterruptedException, SQLException{
        candado.acquire();
        
        System.out.println("Haciendo query"+query);
        PreparedStatement  ps = prepareStatement2(query);
        
        ResultSet rs = ps.executeQuery();
        candado.release();
        return rs;
    }
    
    public void escribir(String query) throws InterruptedException, SQLException{
        candado.acquire();
        
        PreparedStatement  ps = prepareStatement2(query);
        ps.executeQuery(query);
        
        candado.release();
    }
    
    public void escribir(int value, String query) throws InterruptedException, SQLException{
        candado.acquire();
        
        PreparedStatement  ps = prepareStatement2(value,query);
        ps.executeQuery(query);
        
        candado.release();
    }
    
    public void escribir(String value, String query) throws InterruptedException, SQLException{
        candado.acquire();
        
        PreparedStatement  ps = prepareStatement2(value, query);
        ps.executeQuery(query);
        
        candado.release();
    }
   
    public PreparedStatement prepareStatement2(int value, String query) throws SQLException{
        PreparedStatement  ps = connection.prepareStatement(query);
        ps.setInt(1, value);
        return ps;    
    }
    
    public PreparedStatement prepareStatement2(String value, String query) throws SQLException{
        PreparedStatement  ps = connection.prepareStatement(query);
        ps.setString(1, value);
        return ps;  
    }
    
     public PreparedStatement prepareStatement2(String query) throws SQLException{
        return connection.prepareStatement(query);
    }
    
    private void openConnection() {
        try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(  
                "jdbc:mysql://localhost:3306/muffin?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","1234");
                System.out.println("Connection opened");
        } catch (SQLException e) {
                e.printStackTrace();
        } catch (ClassNotFoundException e) {
                e.printStackTrace();
        } 
    }
    
    private void closeConnection() {
        try {
                connection.close();
        } catch (SQLException e) {
                e.printStackTrace();
        } 
    }
}

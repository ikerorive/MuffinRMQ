/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import DTO.*;
import java.sql.Connection;
import java.sql.Date;
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

    public GestorBBDD() {
        candado = new Semaphore(1);
        this.openConnection();
    }

    public ResultSet leer(int value, String query) throws InterruptedException, SQLException {
        candado.acquire();
        PreparedStatement ps = prepareStatement2(value, query);
        ResultSet rs = ps.executeQuery();
        candado.release();
        return rs;
    }

    public ResultSet leer(String value, String query) throws InterruptedException, SQLException {
        candado.acquire();
        PreparedStatement ps = prepareStatement2(value, query);
        ResultSet rs = ps.executeQuery();
        candado.release();
        return rs;
    }

    public ResultSet leer(String query) throws InterruptedException, SQLException {
        candado.acquire();

        System.out.println("Haciendo query" + query);
        PreparedStatement ps = prepareStatement2(query);

        ResultSet rs = ps.executeQuery();
        candado.release();
        return rs;
    }

        
    public ResultSet leer(int id, int idCategoria, String query) throws InterruptedException, SQLException {
        candado.acquire();
       
        PreparedStatement ps = prepareStatement2(id, idCategoria, query);
        
        ResultSet rs = ps.executeQuery();
        candado.release();
        return rs;
    }
    
    public void escribir(String query) throws InterruptedException, SQLException {
        candado.acquire();

        PreparedStatement ps = prepareStatement2(query);
        ps.executeUpdate();

        candado.release();
    }

    public void escribir(int value, String query) throws InterruptedException, SQLException {
        candado.acquire();

        PreparedStatement ps = prepareStatement2(value, query);
        ps.executeUpdate();

        candado.release();
    }

    public void escribir(String value, String query) throws InterruptedException, SQLException {
        candado.acquire();

        PreparedStatement ps = prepareStatement2(value, query);
        ps.executeUpdate();

        candado.release();
    }

    public void escribirMensaje(int chatId, int userId, Date date,
            String message, String query) throws InterruptedException, SQLException {
        candado.acquire();
        PreparedStatement ps = prepareStatement2(chatId, userId, date, message, query);
        ps.executeUpdate();
        candado.release();
        System.out.println("MENSAJE GUARDADO EN LA BBDD");

    }

    public void escribirEvento(String creador, String description, String name, String imgUrl,
            int maxSize, String datee, double latitude, double longitude, int eventType, String query) throws InterruptedException, SQLException {
        candado.acquire();
        PreparedStatement ps = prepareStatement2(creador, description, name, imgUrl, maxSize, datee, latitude, longitude, eventType, query);
        ps.executeUpdate();
        candado.release();
    }

    public void escribirChat(int evento, String description, int moderado, String query) throws InterruptedException, SQLException {
        candado.acquire();
        PreparedStatement ps = prepareStatement2(evento, description, moderado, query);
        ps.executeUpdate();
        candado.release();
    }

    public void escribirUser(int tipoUsuario, String username, String password, String email,
            String salt, String query) throws InterruptedException, SQLException {
        candado.acquire();
        PreparedStatement ps = prepareStatement2(tipoUsuario, username, password, email, salt, query);
        ps.executeUpdate();
        candado.release();
    }

    public void escribir(float resultado, UserCaracteristics user,String query) throws InterruptedException, SQLException {
        candado.acquire();

        PreparedStatement ps = prepareStatement2(resultado,user,query);
        ps.executeUpdate();

        candado.release();
    }
    
    public void escribir(float porcentaje, int id, int idCategoria,String query) throws InterruptedException, SQLException {
        candado.acquire();

        PreparedStatement ps = prepareStatement2(porcentaje,id,idCategoria,query);
        ps.executeUpdate();

        candado.release();
    }
    
    public void escribir(int id, int idCategoria,float porcentaje, String query) throws InterruptedException, SQLException {
        candado.acquire();

        PreparedStatement ps = prepareStatement2(id,idCategoria,porcentaje,query);
        ps.executeUpdate();

        candado.release();
    }
     
    public void escribir(int idEvento, int id,int idInteres, String query) throws InterruptedException, SQLException {
        candado.acquire();

        PreparedStatement ps = prepareStatement2(idEvento,id,idInteres,query);
        ps.executeUpdate();

        candado.release();
    }
    
    public PreparedStatement prepareStatement2(int evento, String description, int moderado, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, evento);
        ps.setString(2, description);
        ps.setInt(3, moderado);
        return ps;
    }

    public PreparedStatement prepareStatement2(String creador, String description, String name, String imgUrl,
            int maxSize, String datee, double latitude, double longitude, int eventType, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, creador);
        ps.setString(2, description);
        ps.setString(3, name);
        ps.setString(4, imgUrl);
        ps.setInt(5, maxSize);
        ps.setString(6, datee);
        ps.setDouble(7, latitude);
        ps.setDouble(8, longitude);
        ps.setInt(9, eventType);
        return ps;
    }

    public PreparedStatement prepareStatement2(int tipoUsuario, String username, String password, String email,
            String salt, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, tipoUsuario);
        ps.setString(2, username);
        ps.setString(3, password);
        ps.setString(4, email);
        ps.setString(5, salt);
        // ps.setBlob(6, avatar);
        return ps;
    }

    public PreparedStatement prepareStatement2(int value, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, value);
        return ps;
    }

    public PreparedStatement prepareStatement2(int value1, int value2, Date value3, String value4, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, value1);
        ps.setInt(2, value2);
        ps.setDate(3, value3);
        ps.setString(4, value4);
        return ps;
    }

    public PreparedStatement prepareStatement2(String value, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, value);
        return ps;
    }

    public PreparedStatement prepareStatement2(String query) throws SQLException {
        return connection.prepareStatement(query);
    }

    
    public PreparedStatement prepareStatement2(int id, int idCategoria, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.setInt(2, idCategoria);

        return ps;
    }
    
      public PreparedStatement prepareStatement2(float resultado, UserCaracteristics user, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setFloat(1, resultado);
        ps.setInt(2, user.getIdUser());
        ps.setInt(3, user.getIdCategoria());

        return ps;
    }

      
    public PreparedStatement prepareStatement2(float porcentaje,int id, int idCategoria, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setFloat(1, porcentaje);
        ps.setInt(2, id);
        ps.setInt(3, idCategoria);

        return ps;
    }

    public PreparedStatement prepareStatement2(int id, int idCategoria,float porcentaje, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.setInt(2, idCategoria);
        ps.setFloat(3, porcentaje);
        
        return ps;
    }
    
    public PreparedStatement prepareStatement2(int idEvento, int id,int idInteres, String query) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, idEvento);
        ps.setInt(2, id);
        ps.setInt(3, idInteres);
        
        return ps;
    }
    
    private void openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/muffin?useUnicode=true?autoReconnect=true&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "admin");
            System.out.println("Connection opened");
            
        } catch (SQLException e) {
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

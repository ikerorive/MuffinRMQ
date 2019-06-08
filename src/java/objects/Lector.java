/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 *
 * @author Aitor Piñeiro
 */
public class Lector implements Callable<ResultSet>{
 
     GestorBBDD connection;
    int queryType; //0 only query; 1 integer and query; 2 String and query

    String query;
    int valueInt;
    String valueString;

        
    //getCaracteristicaUsuario
    int id,idCategoria,idEvento;
    
    public Lector(GestorBBDD connection, String query) {
        this.connection = connection;
        this.query = query;
        queryType = 0;
    }

    public Lector(GestorBBDD connection, int valueInt, String query) {
        this.connection = connection;
        this.query = query;
        this.valueInt = valueInt;
        queryType = 1;
    }

    public Lector(GestorBBDD connection, String valueString, String query) {
        this.connection = connection;
        this.query = query;
        this.valueString = valueString;
        queryType = 2;
    }
    
    
    //Leer caracteristicaUser
    public Lector(GestorBBDD connection, int id, int idCategoria, String query){
        this.connection = connection;
        this.query = query;
        this.id = id;
        this.idCategoria = idCategoria;
        queryType = 3;
    }
/*
    //Leer interes
    public Lector(GestorBBDD connection, int idEvento, int id, String query){
        this.connection = connection;
        this.query = query;
        this.idEvento = idEvento;
        this.id = id;
        queryType = 4;
    }*/
    
    public ResultSet makeQuery() {
        try {
            switch (queryType) {
                case 0:
                    return connection.leer(query);
                case 1:
                    return connection.leer(valueInt, query);
                case 2:
                    return connection.leer(valueString, query);
                case 3:
                    return connection.leer(id, idCategoria, query);
                default:
                    break;
            }
        } catch (SQLException a) {
        } catch (InterruptedException e) {
        }
        return null;
    }

    @Override
    public ResultSet call() throws Exception {
        return makeQuery();
    }
}

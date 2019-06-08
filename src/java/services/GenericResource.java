/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import DTO.Caracteristica;
import DTO.Query;
import DTO.ResposeList;
import DTO.UserCaracteristics;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import objects.App;
import objects.Escritor;
import objects.GestorBBDD;
import objects.Lector;
import org.apache.commons.lang3.Range;
import static services.ChatResources.database;
import static services.ChatResources.main;

/**
 * REST Web Service
 *
 * @author Ander
 */
@Path("generic")
public class GenericResource {

    static App main;
    static GestorBBDD database;
    static ExecutorService poolWrite,poolRead;
     
    @Context
    private UriInfo context;    
    
    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
         
        main = new App();
        main.loadDataFromDB();
        database = main.getDatabaseConnection();
        poolWrite = main.getPoolWrite();
        poolRead = main.getPoolRead();
        
    }

    /**
     * Retrieves representation of an instance of service.GenericResource
     * @return an instance of java.lang.String
     */
   
    @GET
    @Path("/getUser")
    @Produces(MediaType.APPLICATION_JSON)
    public UserCaracteristics getCaracteristicaUsuario(@QueryParam("idUser") int id , @QueryParam("idCategoria") int idCategoria) throws ClassNotFoundException, SQLException, InterruptedException, ExecutionException{
        
        float porcentaje=0;
        ResultSet rs = null;
        String query = "SELECT * FROM muffin.userusercaracteristics WHERE user=? AND userCaracteristics=?;";
        
        Future<ResultSet> future = poolRead.submit(new Lector(database,id,idCategoria,query));
        
        rs = future.get();
         
        while(rs.next()){ 
            porcentaje=rs.getFloat("percentage");
        }
        
        UserCaracteristics model = new UserCaracteristics();
        
        model.setIdCategoria(idCategoria);
        model.setIdUser(id);
        model.setPorcentaje(porcentaje);
        
        return model;
    }
    
    
    @GET
    @Path("/getResto")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<UserCaracteristics> getCaracteristicaResto(@QueryParam("idUser") int id , @QueryParam("idCategoria") int idCategoria) throws ClassNotFoundException, SQLException, ExecutionException, InterruptedException{
 
        ArrayList<UserCaracteristics> datos = new ArrayList<>();
        float porcentaje=0;
        ResultSet rs = null;
        int idCarac=0;
        
        String query = "SELECT * FROM muffin.userusercaracteristics WHERE user=? AND NOT userCaracteristics=?;";
        
        Future<ResultSet> future = poolRead.submit(new Lector(database,id,idCategoria,query));
        
        rs = future.get();
       
        while(rs.next()){
            UserCaracteristics tm = new UserCaracteristics();
            tm.setIdUser(id);
            tm.setIdCategoria(rs.getInt("userCaracteristics"));
            tm.setPorcentaje(rs.getFloat("percentage"));
            datos.add(tm);
        }
        
        return datos;
    }


    
    @PUT
    @Path("/actualizarUsuario")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putSeleccionado(UserCaracteristics user) throws ClassNotFoundException, SQLException {

        String query="";
 
        System.out.println("Categoria: "+user.getIdCategoria()+" User: "+user.getIdUser()+" Porcentaje: "+user.getPorcentaje());
        
        float min;
        float max;
        float resultado = 0;
        
        HashMap<Range, Integer> rangos = new HashMap<Range, Integer>();
        rangos.put(Range.between(0, 29),50);
        rangos.put(Range.between(30, 49),60);
        rangos.put(Range.between(50, 64),70);
        rangos.put(Range.between(65, 74),80);
        rangos.put(Range.between(75, 84),90);
        rangos.put(Range.between(85, 89),95);
        rangos.put(Range.between(90, 94),100);
        rangos.put(Range.between(95, 100),100); 
        
        for (Map.Entry<Range, Integer> entry : rangos.entrySet()) {
            String maximo = entry.getKey().getMaximum().toString();
            String minimo = entry.getKey().getMinimum().toString();
            max = Float.valueOf(maximo);
            min = Float.valueOf(minimo);
            if((user.getPorcentaje()>min) && (max>=user.getPorcentaje())){
                resultado=(user.getPorcentaje()+entry.getValue())/2;
            } 
        }
      
        System.out.println("resultado: "+resultado);  
    
        query = query+"UPDATE `muffin`.`userusercaracteristics` SET `percentage`=? WHERE `user`=? and`userCaracteristics`=?;\n";
      
        poolWrite.submit(new Escritor(database,resultado,user,query));
               
    }
    
   
     
    @PUT
    @Path("/actualizarResto")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putResto(ArrayList<UserCaracteristics> lista) throws ClassNotFoundException, SQLException {
      
        HashMap<Integer, Float> listaResultados = new HashMap<Integer, Float>();
        
        int id = lista.get(0).getIdUser();
        
        float min;
        float max;
        float resultado = 0;
        
        HashMap<Range, Integer> rangosBajar = new HashMap<Range, Integer>();
        rangosBajar.put(Range.between(0, 29),0);
        rangosBajar.put(Range.between(30, 49),20);
        rangosBajar.put(Range.between(50, 64),45);
        rangosBajar.put(Range.between(65, 74),60);
        rangosBajar.put(Range.between(75, 84),70);
        rangosBajar.put(Range.between(85, 89),80);
        rangosBajar.put(Range.between(90, 94),85);
        rangosBajar.put(Range.between(95, 100),90);    
      
        for(UserCaracteristics user:lista){
            System.out.println(user);
        }
        
        for(UserCaracteristics user:lista){
            for (Map.Entry<Range, Integer> entry : rangosBajar.entrySet()) {
                String maximo = entry.getKey().getMaximum().toString();
                String minimo = entry.getKey().getMinimum().toString();
                max = Float.valueOf(maximo);
                min = Float.valueOf(minimo);
                if((user.getPorcentaje()>=min) && (max>=user.getPorcentaje())){
                    resultado=(user.getPorcentaje()+entry.getValue())/2;
                    System.out.println(resultado);
                    System.out.println(user.getIdCategoria());
                    listaResultados.put(user.getIdCategoria(), resultado);
                } 
            }
        }
        
        ArrayList<Query> listaQuery = new ArrayList<>();
        
        for (Map.Entry<Integer,Float> entry2 : listaResultados.entrySet()) {
      
            Query query = new Query();
      
            query.setConsulta("UPDATE `muffin`.`userusercaracteristics` SET `percentage`=? WHERE `user`=? AND `userCaracteristics`=?;");
            query.setPorcentaje(entry2.getValue());
            query.setIdCategoria(entry2.getKey());
            
            listaQuery.add(query);
            System.out.println(query);
        }
        
        
        for(Query q : listaQuery){
        
            poolWrite.submit(new Escritor(database,q.getPorcentaje(),id,q.getIdCategoria(),q.getConsulta()));
        
        }
        
    }
    
    @POST
    @Path("/insertarCaracteristicas")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putCaracteristicas(ArrayList<UserCaracteristics> lista) throws ClassNotFoundException, SQLException{
        
        Connection con = null;
        String username="root";
        String password=""; 
        int id=0;
        int categoria;
        float porcentaje;
        ArrayList<Query> listaQuery = new ArrayList<>();
        
        for(UserCaracteristics user: lista){
            id=user.getIdUser();
            
            Query query = new Query();
            
            query.setConsulta("INSERT INTO `muffin`.`userusercaracteristics` (`user`, `userCaracteristics`, `percentage`) VALUES (?, ? , ?);");
            query.setPorcentaje(user.getPorcentaje());
            query.setIdCategoria(user.getIdCategoria());
      
            //query = "INSERT INTO `muffin`.`userusercaracteristics` (`user`, `userCaracteristics`, `percentage`) VALUES ('" + id+"', '"+ categoria+ "' , '" + porcentaje+"');";
            listaQuery.add(query);
            System.out.println(query);
        
        }
        
        for(Query q : listaQuery){
        
            poolWrite.submit(new Escritor(database,id,q.getIdCategoria(),q.getPorcentaje(),q.getConsulta()));
        
        }
    }
    
    
    @GET
    @Path("/ordenar")
    @Produces(MediaType.APPLICATION_JSON)
    public ResposeList getDataIJSONById(@QueryParam("id") int id) throws ClassNotFoundException, SQLException, InterruptedException, ExecutionException{
        
        ArrayList<Caracteristica> datos = new ArrayList<>();
        ResultSet rs = null;    
        String query = "SELECT * FROM muffin.userusercaracteristics WHERE user=?;";
        Future<ResultSet> future = poolRead.submit(new Lector(database,id,query));
        
        rs = future.get();
    
        HashMap<Integer, Float> map = new HashMap<Integer, Float>();
        
        while(rs.next()){
            Caracteristica caracteristica = new Caracteristica();
            
            caracteristica.setIdUser(rs.getInt("user"));
            int idCarac = rs.getInt("userCaracteristics");
            caracteristica.setIdCaracteristica(idCarac);
            float porcentaje =rs.getFloat("percentage");
            caracteristica.setPercentage(porcentaje);
            
            map.put(idCarac, porcentaje);
            
            datos.add(caracteristica);
        }  
        
        System.out.println("LISTA SIN ORDENAR -----------------------------------");
        for (Map.Entry<Integer, Float> entry : map.entrySet()) {
            System.out.println("clave=" + entry.getKey() + ", valor=" + entry.getValue());
        }
        
        // Create a list from elements of HashMap 
        List<Map.Entry<Integer, Float> > list = 
               new LinkedList<Map.Entry<Integer, Float> >(map.entrySet()); 
  

        // Sort the list 
        Collections.sort(list, (new Comparator<Map.Entry<Integer, Float> >() { 
            public int compare(Map.Entry<Integer, Float> o1,  
                               Map.Entry<Integer, Float> o2) 
            { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }).reversed()); 
        
          
        // put data from sorted list to hashmap  
        HashMap<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>(); 
        for (Map.Entry<Integer, Float> aa : list) { 
            sortedMap.put(aa.getKey(), aa.getValue()); 
        } 
        
        
        ArrayList<Integer> orden = new ArrayList<>();
        
        
        System.out.println("LISTA ORDENADA -----------------------------------");
        for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
            System.out.println("clave=" + entry.getKey() + ", valor=" + entry.getValue());
            orden.add(entry.getKey());
        }
        
        ResposeList ordenRespose=new ResposeList();
        ordenRespose.setList(orden);
        
        return ordenRespose;
    }
       
    @POST
    @Path("/anadirUserEvento")
    @Consumes(MediaType.APPLICATION_JSON)
    public void putSeleccionado(@QueryParam("idUser") int id,@QueryParam("evento") int idEvento,@QueryParam("interes") int idInteres) throws ClassNotFoundException, SQLException {

        String query="";

        query = query+"INSERT INTO `muffin`.`userevento` (`evento`, `user`, `interes`) VALUES (?, ?, ?);";
      
        poolWrite.submit(new Escritor(database,idEvento,id,idInteres,query));
               
    }
    
    @GET
    @Path("/getInteres")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getInteresUsuario(@QueryParam("idUser") int id , @QueryParam("evento") int idEvento) throws ClassNotFoundException, SQLException, InterruptedException, ExecutionException{
        
        int interes=0;
        ResultSet rs = null;
        String query = "SELECT * FROM muffin.userevento WHERE evento=? AND user=?;";
        
        Future<ResultSet> future = poolRead.submit(new Lector(database,id,idEvento,query));
        
        rs = future.get();
         
        while(rs.next()){ 
            interes=rs.getInt("interes");
        }
        
        System.out.println("HOLAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+interes);
       
        return Response.ok(""+interes).header("Access-Control-Allow-Origin", "*").build();
    }
   
    
    
    @GET
    @Path("/getCantidadEvento")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNumeroEvento(@QueryParam("evento") int idEvento) throws ClassNotFoundException, SQLException, InterruptedException, ExecutionException{
        
        int i=0;
        ResultSet rs = null;
        String query = "SELECT * FROM muffin.userevento WHERE evento=?;";
        
        Future<ResultSet> future = poolRead.submit(new Lector(database,idEvento,query));
        
        rs = future.get();
         
        while(rs.next()){ 
            i++;
        }
       
        return ""+i;
    }
    
    

    
    
    
}

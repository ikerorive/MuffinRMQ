/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.Set;
import javax.ws.rs.core.Application;
import objects.App;

/**
 *
 * @author Aitor Piñeiro
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {
   

    public ApplicationConfig() {
        /*App main = new App();
        main.loadDataFromDB();
        main.initializeMiddleware();*/
        //ChatResources servicio = new ChatResources();
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(services.ChatResources.class);
        resources.add(services.GenericResource.class);
    }
    
}

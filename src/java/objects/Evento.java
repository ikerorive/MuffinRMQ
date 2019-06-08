/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

/**
 *
 * @author Aitor Piñeiro
 */
public class Evento {
    int id, eventType;
    String creador, description, name, imgUrl, maxSize, date, longitude, latitude;
    
    public Evento(){
        
    }
    
    public Evento(int id, String creador, String description, String name, String imgUrl, String maxSize,
            String date, String longitude, String latitude, int eventType){
        this.id = id;
        this.creador = creador;
        this.description = description;
        this.name =name;
        this.imgUrl = imgUrl;
        this.maxSize = maxSize;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.eventType = eventType;
    }

    public Evento(String creador, String description, String name, String imgUrl, String maxSize,
            String date, String longitude, String latitude, int eventType){
        this.creador = creador;
        this.description = description;
        this.name =name;
        this.imgUrl = imgUrl;
        this.maxSize = maxSize;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
    
    
    public String getCreador() {
        return creador;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getMaxSize() {
        return maxSize;
    }

    public String getName() {
        return name;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}

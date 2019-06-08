/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.util.ArrayList;
import java.util.List;

public class User {
    int id;
    String username;
    int tipoUsuario;
    String salt;
    String password;
   // Blob avatar;
    String email;


    
    
    public User(int id, String username){
       this.id = id;
       this.username = username;
      
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

  /*  public Blob getAvatar() {
        return avatar;
    }
*/
    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public int getTipoUsuario() {
        return tipoUsuario;
    }
/*
    public void setAvatar(Blob avatar) {
        this.avatar = avatar;
    }
*/
    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setTipoUsuario(int tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
  public User(int tipoUsuario, String username, String password, String email, String salt ){
    
      this.tipoUsuario = tipoUsuario;
      this.username = username;
      this.password = password;
      this.email = email;
      this.salt = salt;
   
      //this.avatar = avatar;
  }
}


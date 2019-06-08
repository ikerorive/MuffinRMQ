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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aitor Piñeiro
 */
public class GestorChat {

    List<Chat> listaChats;
    List<User> listaUsuarios;
    List<Evento> listaEventos;
    GestorBBDD conexion;

    public GestorChat(GestorBBDD conexion) {
        listaChats = new ArrayList<Chat>();
        listaUsuarios = new ArrayList<User>();
        listaEventos = new ArrayList<Evento>();
        this.conexion = conexion;
    }

    public List<Evento> getListaEventos() {
        return listaEventos;
    }

    public void setListaEventos(List<Evento> listaEventos) {
        this.listaEventos = listaEventos;
    }

    public void addEvento(Evento e) {
        listaEventos.add(e);
    }

    public Evento getEventoById(int id) {
        for (Evento e : listaEventos) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public void addChat(Chat c) {
        listaChats.add(c);
    }

    public void deleteChat(Chat c) {
        listaChats.remove(c);
    }

    public void addUser(User u) {
        listaUsuarios.add(u);
    }

    public void deleteUser(User u) {
        listaUsuarios.remove(u);
    }

    public User getUser(int index) {
        return listaUsuarios.get(index);
    }

    public Chat getChat(int index) {
        return listaChats.get(index);
    }

    public User getUserFromID(int id) {
        for (User u : listaUsuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public Chat getChatFromID(int id) {
        for (Chat c : listaChats) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }
    //leer de la bbdd

    public List<Chat> getListaChats() {
        return listaChats;
    }

    public void setListaChats(List<Chat> listaChats) {
        this.listaChats = listaChats;
    }

    public List<User> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<User> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public Evento getLastEvent() {
        Evento a = listaEventos.get(0);
        for (Evento e : listaEventos) {
            if (e.getId() > a.getId()) {
                a = e;
            }
        }
        return a;

    }

    public Evento getEventoByCreator(String c) {
        for (Evento e : listaEventos) {
            if (e.getCreador().equals(c)) {
                return e;
            }
        }
        return null;
    }

    public String enviarMensajeWeb(int id) {
        Chat c = null;
        for (Chat e : listaChats) {
            if (e.getId() == id) {
                c = e;
            }
        }
        String response = "";
        String username = "";

        for (Message m : c.getMensajes()) {
            int userid = m.getUserId();
            for (User u : listaUsuarios) {
                if (u.getId() == userid) {
                    username = u.getUsername();
                }
            }
            response = response + username + ";" + m.getMensaje() + ";" + String.valueOf(m.getDateMensaje()) + ",";
        }
        System.out.println(response);
        return response;
    }

    public ArrayList<String> enviarMensajeWebArray(int id) {
        ArrayList<String> array = new ArrayList<>();
        Chat c = null;
        for (Chat e : listaChats) {
            if (e.getId() == id) {
                c = e;
            }
        }
        String response = "";
        String username = "";

        for (Message m : c.getMensajes()) {
            int userid = m.getUserId();
            for (User u : listaUsuarios) {
                if (u.getId() == userid) {
                    username = u.getUsername();
                }
            }
            array.add(username + ";" + m.getMensaje() + ";" + String.valueOf(m.getDateMensaje()));
        }
        System.out.println(response);
        return array;
    }

}

package com.omaressam.chatapp.Models;

import java.io.Serializable;

public class ChatList implements Serializable {

    public String id ;

    public  User user;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

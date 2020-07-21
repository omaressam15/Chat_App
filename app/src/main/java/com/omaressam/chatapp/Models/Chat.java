package com.omaressam.chatapp.Models;

import java.io.Serializable;

public class Chat implements Serializable {
    private String sender;
    private String receiver;
    private String message;
    private String Time;
    private String Data;
    private boolean isseen;
    private User user;

    public Chat(String sender, String receiver, String message, String Time, boolean isSeen, User user,String Data) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.Time = Time;
        this.Data = Data;
        this.isseen = isSeen;
        this.user = user;
    }

    public Chat() {

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}

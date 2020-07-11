package com.omaressam.chatapp.Notifications;

public class Data  {

    private String user;
    private int icon ;
    private String body;
    private String title ;
    private String sendted;

    public Data(String user, int icon, String body, String title, String sendted) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sendted = sendted;
    }

    public Data () {

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSendted() {
        return sendted;
    }

    public void setSendted(String sendted) {
        this.sendted = sendted;
    }
}

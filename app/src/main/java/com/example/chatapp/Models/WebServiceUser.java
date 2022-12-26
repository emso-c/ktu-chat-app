package com.example.chatapp.Models;

public class WebServiceUser {
    public int id;
    public String username;
    public String password;
    public String firebaseUid;
    public String lastSeen;

    public WebServiceUser(){}
    public WebServiceUser(int id, String username, String password, String firebaseUid, String lastSeen) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firebaseUid = firebaseUid;
        this.lastSeen = lastSeen;
    }
}

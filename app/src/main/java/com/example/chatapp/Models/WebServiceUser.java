package com.example.chatapp.Models;

public class WebServiceUser {
    public int id;
    public String username;
    public String password;
    public String firebaseUid;
    public String lastSeen;
    public String photoUrl;
    public Boolean isOnline;
    public Boolean isTyping;
    public String status;

    public WebServiceUser(){}

    public WebServiceUser(int id, String username, String password, String firebaseUid, String lastSeen, String photoUrl, Boolean isOnline, Boolean isTyping, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firebaseUid = firebaseUid;
        this.lastSeen = lastSeen;
        this.photoUrl = photoUrl;
        this.isOnline = isOnline;
        this.isTyping = isTyping;
        this.status = status;
    }
}

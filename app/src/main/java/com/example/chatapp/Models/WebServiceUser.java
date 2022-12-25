package com.example.chatapp.Models;

public class WebServiceUser {
    public int id;
    public String username;
    public String password;
    public String firebase_uid;

    public WebServiceUser(){}
    public WebServiceUser(int id, String username, String password, String firebase_uid) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firebase_uid = firebase_uid;
    }
}

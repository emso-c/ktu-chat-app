package com.example.chatapp.Models;

public class WebServiceStatus {
    public int id;
    public String user_id;
    public String image_url;
    public String date;

    public WebServiceStatus(){}

    public WebServiceStatus(int id, String user_id, String image_url, String date) {
        this.id = id;
        this.user_id = user_id;
        this.image_url = image_url;
        this.date = date;
    }
}

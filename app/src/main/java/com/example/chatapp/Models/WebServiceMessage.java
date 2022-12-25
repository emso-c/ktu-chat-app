package com.example.chatapp.Models;

public class WebServiceMessage {
    public int id;
    public int fromID;
    public int toID;
    public String content;
    public String date;

    public WebServiceMessage(){}
    public WebServiceMessage(int id, int fromID, int toID, String content, String date) {
        this.id = id;
        this.fromID = fromID;
        this.toID = toID;
        this.content = content;
        this.date = date;
    }
}

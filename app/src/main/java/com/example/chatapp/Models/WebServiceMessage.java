package com.example.chatapp.Models;

import java.io.ObjectStreamClass;
import java.io.Serializable;

public class WebServiceMessage implements Serializable {
    private static final long serialVersionUID = ObjectStreamClass.lookup(WebServiceMessage.class).getSerialVersionUID();
    public int id;
    public int fromID;
    public int toID;
    public String content;
    public String date;
    public Boolean seen;

    public WebServiceMessage(){}
    public WebServiceMessage(int id, int fromID, int toID, String content, String date, Boolean seen) {
        this.id = id;
        this.fromID = fromID;
        this.toID = toID;
        this.content = content;
        this.date = date;
        this.seen = seen;
    }
}

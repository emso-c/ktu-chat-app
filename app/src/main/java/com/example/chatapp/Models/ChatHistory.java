package com.example.chatapp.Models;

import java.util.ArrayList;

public class ChatHistory {
    public ArrayList<WebServiceMessage> messages;
    public ChatItem chatInfo;

    public ChatHistory(ArrayList<WebServiceMessage> messages, ChatItem chatInfo) {
        this.messages = messages;
        this.chatInfo = chatInfo;
    }
}

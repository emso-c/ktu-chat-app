package com.example.chatapp.Models;

public class User {
    public String id, uuid, profilePic, name, lastMessage, lastMessageDate, unseenMessages;

    public User(String id, String uuid, String profile_pic, String name, String last_message, String last_message_date, String unseenMessages) {
        this.id = id;
        this.uuid = uuid;
        this.profilePic = profile_pic;
        this.name = name;
        this.lastMessage = last_message;
        this.lastMessageDate = last_message_date;
        this.unseenMessages = unseenMessages;
    }
}

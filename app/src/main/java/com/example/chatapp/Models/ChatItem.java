package com.example.chatapp.Models;

public class ChatItem {
    public String id, uuid, profilePic, name, lastMessage, lastMessageDate, unseenMessages, lastSeen;

    public ChatItem(String id, String uuid, String profile_pic, String name, String lastMessage, String lastMessageDate, String unseenMessages, String lastSeen) {
        this.id = id;
        this.uuid = uuid;
        this.profilePic = profile_pic;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
        this.unseenMessages = unseenMessages;
        this.lastSeen = lastSeen;
    }
}

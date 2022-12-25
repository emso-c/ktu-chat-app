package com.example.chatapp.Classes;

import com.example.chatapp.Models.ChatHistory;

import java.util.Comparator;

public class ChatHistoryComparator implements Comparator<ChatHistory> {
    @Override
    public int compare(ChatHistory o1, ChatHistory o2) {
        return Helpers.compareDates(o1.chatInfo.lastMessageDate, o2.chatInfo.lastMessageDate);
    }
}

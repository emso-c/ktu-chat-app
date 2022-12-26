package com.example.chatapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Classes.Helpers;
import com.example.chatapp.Models.FirebaseUserInstance;
import com.example.chatapp.Models.WebServiceMessage;
import com.example.chatapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final List<WebServiceMessage> chatMessages;
    private final Context context;
    ArrayList<Boolean> selected = new ArrayList<>(Collections.nCopies(1000, false)); // Huge issue

    private static final int VIEW_TYPE_MESSAGE_RIGHT = 1;
    private static final int VIEW_TYPE_MESSAGE_LEFT = 2;

    public MessageAdapter(List<WebServiceMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case VIEW_TYPE_MESSAGE_LEFT:
                layout = R.layout.message_item_left;
                break;
            case VIEW_TYPE_MESSAGE_RIGHT:
                layout = R.layout.message_item_right;
                break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
            return new ViewHolder(v);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (selected.get(position)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.blue_ultra_light));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.blue_ultra_dark));
        }
        WebServiceMessage webServiceMessage = chatMessages.get(position);
        holder.bind(webServiceMessage);

    }

     class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private final TextView messageTextView;
        private final TextView messageDateTextView;
        private final TextView seenInfoTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
            messageDateTextView = itemView.findViewById(R.id.message_date);
            seenInfoTextView = itemView.findViewById(R.id.seen_info);
            itemView.setOnLongClickListener(this);
        }

        void bind(WebServiceMessage webServiceMessage) {
            messageTextView.setText(webServiceMessage.content);
            messageDateTextView.setText(Helpers.parseMessageDate(webServiceMessage.date));
            seenInfoTextView.setText(Helpers.parseSeenText(webServiceMessage.seen));
        }

         @Override
         public boolean onLongClick(View view) {
             selected.set(getAdapterPosition(), !selected.get(getAdapterPosition()));
             notifyItemChanged(getAdapterPosition());
             return true;
         }
    }
    
    @Override
    public int getItemViewType(int position) {
        WebServiceMessage message = chatMessages.get(position);

        if (String.valueOf(message.fromID).equals(FirebaseUserInstance.getInstance().id)) {
            return VIEW_TYPE_MESSAGE_RIGHT;
        } else {
            return VIEW_TYPE_MESSAGE_LEFT;
        }
    }
}
package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.Classes.Helpers;
import com.example.chatapp.Models.ChatItem;
import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatItemAdapter extends RecyclerView.Adapter<ChatItemAdapter.ViewHolder> {

    ArrayList<ChatItem> chatItemList;
    Context context;

    public ChatItemAdapter(ArrayList<ChatItem> chatItem_list, Context context) {
        this.chatItemList = chatItem_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem chatItem = chatItemList.get(position);
        Picasso.get()
                .load(chatItem.profilePic)
                .placeholder(com.firebase.ui.auth.R.drawable.fui_ic_anonymous_white_24dp)
                .into(holder.image);
        holder.name.setText(chatItem.name);
        holder.last_message.setText(chatItem.lastMessage);
        holder.last_message_date.setText(Helpers.parseDate(chatItem.lastMessageDate));
        if (!chatItem.unseenMessages.equals("0")){
            holder.unseen_messages.setText(chatItem.unseenMessages);
        } else {
            holder.unseen_messages.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return chatItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView name, last_message, last_message_date, unseen_messages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            image = itemView.findViewById(R.id.chat_menu_profile_picture);
            name = itemView.findViewById(R.id.user_name);
            last_message = itemView.findViewById(R.id.last_message);
            last_message_date = itemView.findViewById(R.id.last_message_date);
            unseen_messages = itemView.findViewById(R.id.unseen_messages);
        }

        @Override
        public void onClick(View v) {
            TextView textView = (TextView) v.findViewById(R.id.user_name);
            String user_name = textView.getText().toString();

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("username", user_name);
            context.startActivity(intent);
        }

    }
}

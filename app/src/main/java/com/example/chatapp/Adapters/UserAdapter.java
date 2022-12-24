package com.example.chatapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Models.User;
import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<User> userList;
    Context context;

    public UserAdapter(ArrayList<User> user_list, Context context) {
        this.userList = user_list;
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
        User user = userList.get(position);
        Picasso.get()
                .load(user.profilePic)
                .placeholder(com.firebase.ui.auth.R.drawable.fui_ic_anonymous_white_24dp)
                .into(holder.image);
        holder.name.setText(user.name);
        holder.last_message.setText(user.lastMessage);
        holder.last_message_date.setText(user.lastMessageDate);
        if (!user.unseenMessages.equals("0")){
            holder.unseen_messages.setText(user.unseenMessages);
        } else {
            holder.unseen_messages.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView name, last_message, last_message_date, unseen_messages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            image = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.user_name);
            last_message = itemView.findViewById(R.id.last_message);
            last_message_date = itemView.findViewById(R.id.last_message_date);
            unseen_messages = itemView.findViewById(R.id.unseen_messages);

        }

        @Override
        public void onClick(View v) {
            // TODO handle
        }

    }
}

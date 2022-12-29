package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.Classes.WebService;
import com.example.chatapp.Models.FirebaseUserInstance;
import com.example.chatapp.Models.WebServiceUser;
import com.example.chatapp.R;

import java.util.ArrayList;

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.ViewHolder> {

    ArrayList<WebServiceUser> userList;
    Context context;
    ArrayList<Boolean> selected = new ArrayList<>();

    public UserItemAdapter(ArrayList<WebServiceUser> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    public void clearFilter(ArrayList<WebServiceUser> userListFull) {
        userList.clear();
        userList.addAll(userListFull);
        notifyDataSetChanged();
    }

    public void filter(String query, ArrayList<WebServiceUser> userListFull) {
        userListFull.clear();
        query = query.toLowerCase();
        if (query.isEmpty()) {
            userList.addAll(userListFull);
        } else {
            for (WebServiceUser webServiceUser : userListFull) {
                if (webServiceUser.username.toLowerCase().contains(query)) {
                    userList.add(webServiceUser);
                }
            }
        }
        notifyDataSetChanged();
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
        if (selected.get(position)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.blue));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.blue_ultra_dark));
        }
        WebServiceUser user = userList.get(position);
        WebService webService = new WebService(
                context.getString(R.string.hostname),
                context.getString(R.string.port),
                FirebaseUserInstance.getInstance(),
                context
        );
        WebService.putProfilePicture(holder.imageViewProfilePhoto, user.firebaseUid);

        holder.textViewUsername.setText(user.username);

        if(user.status.isEmpty()){
            holder.textViewStatus.setText("Merhaba, ben KTUChat kullanıyorum.");
        } else{
            holder.textViewStatus.setText(user.status);
        }

        if (user.isOnline.equals(true)) {
            holder.imageViewOnlineStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_active));
        } else {
            holder.imageViewOnlineStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_inactive));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView imageViewProfilePhoto, imageViewOnlineStatus;
        TextView textViewUsername, textViewStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProfilePhoto = itemView.findViewById(R.id.chat_menu_profile_picture);
            imageViewOnlineStatus = itemView.findViewById(R.id.online_status);
            textViewUsername = itemView.findViewById(R.id.user_name);
            textViewStatus = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            selected.add(false);
        }

        @Override
        public void onClick(View v) {
            String user_name = textViewUsername.getText().toString();

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("username", user_name);
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            // Toggle the selected state of the item
            selected.set(getAdapterPosition(), !selected.get(getAdapterPosition()));
            // Notify the adapter of the change
            notifyItemChanged(getAdapterPosition());
            return true;
        }
    }
}

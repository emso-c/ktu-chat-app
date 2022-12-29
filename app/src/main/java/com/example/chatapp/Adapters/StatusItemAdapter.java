package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Classes.Helpers;
import com.example.chatapp.Classes.WebService;
import com.example.chatapp.FullscreenImageActivity;
import com.example.chatapp.Models.FirebaseUserInstance;
import com.example.chatapp.Models.WebServiceStatus;
import com.example.chatapp.Models.WebServiceUser;
import com.example.chatapp.R;

import java.util.ArrayList;

public class StatusItemAdapter extends RecyclerView.Adapter<StatusItemAdapter.ViewHolder> {

    ArrayList<WebServiceStatus> statusList;
    Context context;
    ArrayList<Boolean> selected = new ArrayList<>();

    public StatusItemAdapter(ArrayList<WebServiceStatus> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.status_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (selected.get(position)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.blue));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.blue_ultra_dark));
        }
        WebServiceStatus status = statusList.get(position);
        WebService webService = new WebService(
                context.getString(R.string.hostname),
                context.getString(R.string.port),
                FirebaseUserInstance.getInstance(),
                context
        );
        WebServiceUser user = webService.getUserById(status.user_id);
        WebService.putProfilePicture(holder.imageViewProfilePhoto, user.firebaseUid);
        holder.textViewUsername.setText(user.username);
        holder.textViewDate.setText(Helpers.parseLastSeen(status.date));
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView imageViewProfilePhoto;
        TextView textViewUsername, textViewDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewProfilePhoto = itemView.findViewById(R.id.image_view_profile_pic_status);
            textViewUsername = itemView.findViewById(R.id.text_view_username_status);
            textViewDate = itemView.findViewById(R.id.text_view_date_status);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            selected.add(false);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, FullscreenImageActivity.class);
            intent.putExtra("folder", "statuses");
            intent.putExtra("image_url", statusList.get(getAdapterPosition()).image_url);
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

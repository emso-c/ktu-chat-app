package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.Classes.Helpers;
import com.example.chatapp.Classes.WebService;
import com.example.chatapp.Models.ChatHistory;
import com.example.chatapp.Models.UserManager;
import com.example.chatapp.Models.WebServiceMessage;
import com.example.chatapp.Models.WebServiceUser;
import com.example.chatapp.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private WebServiceUser user;
    private WebService webService;
    ArrayList<WebServiceMessage> chatMessages = new ArrayList<>();

    MessageAdapter messageAdapter;
    RecyclerView recyclerView;

    public ChatActivity(){}
    public ChatActivity(ActivityChatBinding binding) {
        this.binding = binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        webService = new WebService(
                getString(R.string.hostname),
                getString(R.string.port),
                UserManager.getInstance(),
                this
        );

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        user = webService.getUserByUsername(username);

        //ImageView profilePic = findViewById(R.id.chat_profile_picture);
        //profilePic.setImageResource(R.drawable.ic_default_avatar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(user.username);
        TextView subTitle = findViewById(R.id.toolbar_subtitle);
        if(user.isOnline){
            subTitle.setText("Online");
        } else {
            subTitle.setText(Helpers.parseLastSeen(user.lastSeen));
        }
        toolbar.setNavigationOnClickListener(v -> finish());


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(chatMessages, this);
        recyclerView.setAdapter(messageAdapter);

        Button sendButton = findViewById(R.id.send_button);
        EditText msgInputEditText = findViewById(R.id.message_input);
        msgInputEditText.setOnFocusChangeListener((view, b) -> recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1));

        sendButton.setOnClickListener(view -> {
            String text = String.valueOf(msgInputEditText.getText());
            if (text.isEmpty())
                return;
            webService.sendMessage(String.valueOf(user.id), text);
            msgInputEditText.setText("");
            renderChatUI();
        });

        renderChatUI();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void renderChatUI(){
        ChatHistory chatHistory = webService.getChatHistory(String.valueOf(user.id));
        chatMessages.clear();
        chatMessages.addAll(chatHistory.messages);
        messageAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

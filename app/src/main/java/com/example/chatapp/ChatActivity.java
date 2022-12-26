package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Adapters.MessageAdapter;
import com.example.chatapp.Classes.Helpers;
import com.example.chatapp.Classes.SoundManager;
import com.example.chatapp.Classes.WebService;
import com.example.chatapp.Models.ChatHistory;
import com.example.chatapp.Models.FirebaseUserInstance;
import com.example.chatapp.Models.WebServiceMessage;
import com.example.chatapp.Models.WebServiceUser;

import java.util.ArrayList;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private static final int UI_UPDATE_INTERVAL = 3000; // 1 second

    private WebServiceUser user;
    private WebService webService;
    ArrayList<WebServiceMessage> chatMessages = new ArrayList<>();

    private Handler handler;
    private Runnable updateUITask;

    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    TextView title;
    TextView subTitle;

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
                FirebaseUserInstance.getInstance(),
                this
        );

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        user = webService.getUserByUsername(username);
        title = findViewById(R.id.toolbar_title);
        subTitle = findViewById(R.id.toolbar_subtitle);

        //ImageView profilePic = findViewById(R.id.chat_profile_picture);
        //profilePic.setImageResource(R.drawable.ic_default_avatar);
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
            SoundManager.makeSound(this);
            renderChatUI();
        });

        handler = new Handler();
        updateUITask = new Runnable() {
            @Override
            public void run() {
                user = webService.getUserByUsername(username);
                renderAppbar(user, this);
                fetchMessages();
            }
        };
        renderAppbar(user, updateUITask);
        fetchMessages();
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }

    private void renderAppbar(WebServiceUser user, Runnable runnable){
        title.setText(user.username);
        if(user.isOnline){
            subTitle.setText("Online");
        } else {
            subTitle.setText(Helpers.parseLastSeen(user.lastSeen));
        }
        handler.postDelayed(runnable, UI_UPDATE_INTERVAL);
    }

    private void fetchMessages(){
        ChatHistory chatHistory = webService.getChatHistory(String.valueOf(user.id));
        int oldChatMessageSize = chatMessages.size();
        chatMessages.clear();
        for (WebServiceMessage message: chatHistory.messages){
            if(message.fromID != webService.webServiceUser.id && !message.seen)
                webService.setSeen(message.id);
            chatMessages.add(message);
        }
        if (chatMessages.size() > oldChatMessageSize){
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        }
        messageAdapter.notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void renderChatUI(){
        fetchMessages();
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(updateUITask);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateUITask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateUITask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(updateUITask, UI_UPDATE_INTERVAL);
    }
}

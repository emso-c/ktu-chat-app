package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chatapp.Classes.Helpers;
import com.example.chatapp.Classes.WebService;
import com.example.chatapp.Models.UserManager;
import com.example.chatapp.Models.WebServiceUser;
import com.example.chatapp.databinding.ActivityChatBinding;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private WebServiceUser user;
    private WebService webService;

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
        //webService.getChatHistory();

        //ImageView profilePic = findViewById(R.id.chat_profile_picture);
        //profilePic.setImageResource(R.drawable.ic_default_avatar);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(user.username);
        TextView subTitle = findViewById(R.id.toolbar_subtitle);
        subTitle.setText(Helpers.parseLastSeen(user.lastSeen));

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

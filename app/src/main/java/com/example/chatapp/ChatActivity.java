package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private static final int UI_UPDATE_INTERVAL = 3000; // 1 second

    private WebServiceUser user;
    private WebService webService;
    int searchIndex = 0;
    ArrayList<WebServiceMessage> chatMessages = new ArrayList<>();
    ArrayList<WebServiceMessage> foundMessages = new ArrayList<>();

    private Handler handler;
    private Runnable updateUITask;

    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    TextView title;
    TextView subTitle;
    SearchView searchView;
    LinearLayout userInfoGroupLayout;
    ImageView searchBarUpButton;
    ImageView searchBarDownButton;
    Button photoButton;
    Button sendButton;

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
        ImageView imageView = findViewById(R.id.chat_profile_picture);
        WebService.putProfilePicture(imageView, webService.getUserByUsername(username).firebaseUid);
        toolbar.setNavigationOnClickListener(v -> finish());


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(chatMessages, this);
        recyclerView.setAdapter(messageAdapter);

        sendButton = findViewById(R.id.send_button);
        photoButton = findViewById(R.id.photo_button);
        EditText msgInputEditText = findViewById(R.id.message_input);

        msgInputEditText.setOnFocusChangeListener((view, b) -> recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1));
        msgInputEditText.addTextChangedListener(new TextWatcher() {
            boolean lock = false;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                webService.setTyping(true);
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (lock) return;
                lock = true;
                new Handler().postDelayed(() -> {
                    lock = false;
                    webService.setTyping(false);
                }, 1000); // delay by 1 second
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
        sendButton.setOnClickListener(view -> {
            String text = String.valueOf(msgInputEditText.getText());
            if (text.isEmpty())
                return;
            webService.sendMessage(String.valueOf(user.id), text);
            msgInputEditText.setText("");
            SoundManager.makeSound(this);
            renderChatUI();
        });
        photoButton.setOnClickListener(view -> openImageActivityResult());

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

        searchBarUpButton = findViewById(R.id.search_bar_up_button);
        searchBarDownButton = findViewById(R.id.search_bar_down_button);

        searchView = findViewById(R.id.search_chat_menu);
        userInfoGroupLayout = findViewById(R.id.user_info_group_layout);
        searchView.setOnSearchClickListener(view -> {
            userInfoGroupLayout.setVisibility(View.GONE);
            searchBarUpButton.setVisibility(View.VISIBLE);
            searchBarDownButton.setVisibility(View.VISIBLE);
        });
        searchView.setOnCloseListener(() -> {
            userInfoGroupLayout.setVisibility(View.VISIBLE);
            searchBarUpButton.setVisibility(View.GONE);
            searchBarDownButton.setVisibility(View.GONE);
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    foundMessages = findMessages(query);
                    if(foundMessages.size() > 0){
                        searchIndex = foundMessages.size()-1;
                        recyclerView.smoothScrollToPosition(findChatMessageIndex());
                    } else {
                        Toast.makeText(ChatActivity.this, "No matching messages found", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchBarDownButton.setOnClickListener(view -> {
            if(foundMessages.size() <= 0 )
                return;
            if(searchIndex == foundMessages.size()-1){
                Toast.makeText(this, "No matching messages below", Toast.LENGTH_SHORT).show();
                return;
            }
            searchIndex += 1;
            recyclerView.smoothScrollToPosition(findChatMessageIndex());
        });
        searchBarUpButton.setOnClickListener(view -> {
            if(foundMessages.size() <= 0 )
                return;
            if(searchIndex == 0){
                Toast.makeText(this, "No matching messages above", Toast.LENGTH_SHORT).show();
                return;
            }
            searchIndex -= 1;
            recyclerView.smoothScrollToPosition(findChatMessageIndex());
        });
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }
    public void openImageActivityResult() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imageActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri selectedImageUri = data.getData();
                    String randomUUID = UUID.randomUUID().toString();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference ref = storageRef.child("images/"+randomUUID+".jpg");

                    UploadTask uploadTask = ref.putFile(selectedImageUri);
                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads here
                    }).addOnSuccessListener(taskSnapshot -> {
                        String text = taskSnapshot.getMetadata().getName();
                        webService.sendMessage(String.valueOf(user.id), randomUUID+".jpg");
                        SoundManager.makeSound(this);
                        renderChatUI();
                    });
                }
            });

    private int findChatMessageIndex(){
        WebServiceMessage msg = foundMessages.get(searchIndex);
        for(int i = 0; i < chatMessages.size(); i++){
            if(chatMessages.get(i).id == msg.id)
                return i;
        }
        return -1;
    }

    private ArrayList<WebServiceMessage> findMessages(String query){
        ArrayList<WebServiceMessage> foundmessages = new ArrayList<>();
        for (WebServiceMessage message: chatMessages){
            if(message.content.contains(query)){
                foundmessages.add(message);
            }
        }
        return foundmessages;
    }

    private void renderAppbar(WebServiceUser user, Runnable runnable){
        title.setText(user.username);
        if(user.isOnline){
            if(user.isTyping){
                subTitle.setText("Typing...");
            }
            else{
                subTitle.setText("Online");
            }
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
            messageAdapter.notifyDataSetChanged();
        }
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

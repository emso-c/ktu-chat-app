package com.example.chatapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapp.Adapters.ChatItemAdapter;
import com.example.chatapp.Classes.ChatHistoryComparator;
import com.example.chatapp.Classes.WebService;
import com.example.chatapp.Models.ChatHistory;
import com.example.chatapp.Models.ChatItem;
import com.example.chatapp.Models.UserManager;
import com.example.chatapp.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.Collections;


public class ChatFragment extends Fragment {

    FragmentChatBinding binding;
    ArrayList<ChatItem> chatItemArrayList = new ArrayList<>();
    WebService webService;

    ChatItemAdapter adapter;
    LinearLayoutManager layoutManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        adapter = new ChatItemAdapter(chatItemArrayList, getContext());
        binding.chatRecyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        webService = new WebService(
                getString(R.string.hostname),
                getString(R.string.port),
                UserManager.getInstance(),
                getContext()
        );

        this.renderChatMenuUI();
        webService.listen_messages(requireContext());

        IntentFilter filter = new IntentFilter("new-message");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // WebServiceMessage message = (WebServiceMessage) intent.getSerializableExtra("message");
                renderChatMenuUI();
            }
        };
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter);

        return binding.getRoot();
    }

    public void renderChatMenuUI(){
        chatItemArrayList.clear();
        ArrayList<ChatHistory> chatHistoryArrayList = webService.getChatHistory();
        Collections.sort(chatHistoryArrayList, new ChatHistoryComparator());
        for (ChatHistory chatHistory: chatHistoryArrayList){
            chatItemArrayList.add(
                    new ChatItem(
                            chatHistory.chatInfo.id,
                            chatHistory.chatInfo.uuid,
                            "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg",
                            chatHistory.chatInfo.name,
                            chatHistory.chatInfo.lastMessage,
                            chatHistory.chatInfo.lastMessageDate,
                            chatHistory.chatInfo.unseenMessages
                    )
            );
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}

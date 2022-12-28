package com.example.chatapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chatapp.Adapters.UserItemAdapter;
import com.example.chatapp.Classes.WebService;
import com.example.chatapp.Models.FirebaseUserInstance;
import com.example.chatapp.Models.WebServiceUser;
import com.example.chatapp.databinding.FragmentStatusBinding;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    FragmentStatusBinding binding;
    ArrayList<WebServiceUser> webServiceUserArrayList = new ArrayList<>();
    ArrayList<WebServiceUser> webServiceUserArrayListFull = new ArrayList<>();
    WebService webService;

    UserItemAdapter adapter;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatusBinding.inflate(inflater, container, false);

        adapter = new UserItemAdapter(webServiceUserArrayList, getContext());
        binding.userRecyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        binding.userRecyclerView.setLayoutManager(layoutManager);

        webService = new WebService(
                getString(R.string.hostname),
                getString(R.string.port),
                FirebaseUserInstance.getInstance(),
                getContext()
        );

        renderUserMenuUI();

        swipeRefreshLayout = binding.getRoot().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::renderUserMenuUI);

        /*
        // Doesn't work + clashes with chats view listener
        searchView = getActivity().findViewById(R.id.search_chat_menu);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("CONTACTS", "typing");
                if (newText.isEmpty()) {
                    adapter.clearFilter(webServiceUserArrayListFull);
                } else {
                    adapter.filter(newText, webServiceUserArrayListFull);
                }
                return false;
            }
        });
         */
        return binding.getRoot();
    }

    public void renderUserMenuUI(){
        webServiceUserArrayList.clear();
        webServiceUserArrayListFull.clear();
        for(WebServiceUser user: webService.getContacts()){
            webServiceUserArrayList.add(user);
        }
        // TODO sort by username
        webServiceUserArrayListFull.addAll(webServiceUserArrayList);
        adapter.notifyDataSetChanged();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}

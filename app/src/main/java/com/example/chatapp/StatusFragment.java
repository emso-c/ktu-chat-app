package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chatapp.Adapters.StatusItemAdapter;
import com.example.chatapp.Classes.Helpers;
import com.example.chatapp.Classes.SoundManager;
import com.example.chatapp.Classes.WebService;
import com.example.chatapp.Models.FirebaseUserInstance;
import com.example.chatapp.Models.WebServiceStatus;
import com.example.chatapp.Models.WebServiceUser;
import com.example.chatapp.databinding.FragmentStatusBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

public class StatusFragment extends Fragment {

    FragmentStatusBinding binding;
    ArrayList<WebServiceStatus> webServiceStatusArrayList = new ArrayList<>();
    WebService webService;

    StatusItemAdapter adapter;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout myStatusLayout;
    ImageView imageViewProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatusBinding.inflate(inflater, container, false);

        adapter = new StatusItemAdapter(webServiceStatusArrayList, getContext());
        binding.statusRecyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getContext());
        binding.statusRecyclerView.setLayoutManager(layoutManager);

        webService = new WebService(
                getString(R.string.hostname),
                getString(R.string.port),
                FirebaseUserInstance.getInstance(),
                getContext()
        );
        imageViewProfile = binding.getRoot().findViewById(R.id.image_view_curr_user_profile_pic_status);
        webService.putProfilePicture(imageViewProfile);

        swipeRefreshLayout = binding.getRoot().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::renderStatusMenuUI);

        myStatusLayout = binding.getRoot().findViewById(R.id.my_status_layout);
        myStatusLayout.setOnClickListener(view -> openImageActivityResult());
        renderStatusMenuUI();
        return binding.getRoot();
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
                    StorageReference ref = storageRef.child("statuses/"+randomUUID+".jpg");

                    UploadTask uploadTask = ref.putFile(selectedImageUri);
                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads here
                    }).addOnSuccessListener(taskSnapshot -> {
                        webService.addStatus(String.valueOf(webService.webServiceUser.id), ""+randomUUID+".jpg");
                        renderStatusMenuUI();
                    });
                }
            });

    public void renderStatusMenuUI(){
        webServiceStatusArrayList.clear();
        for(WebServiceStatus status: webService.getStatuses()){
            if(Helpers.getHoursPassed(status.date) > 24){
                webService.deleteStatus(String.valueOf(status.id));
                continue;
            }
            webServiceStatusArrayList.add(status);
        }
        adapter.notifyDataSetChanged();
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}

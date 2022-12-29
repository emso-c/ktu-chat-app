package com.example.chatapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Classes.WebService;

public class FullscreenImageActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        String imageUrl = getIntent().getStringExtra("image_url");
        String folder = getIntent().getStringExtra("folder");
        imageView = findViewById(R.id.fullscreen_image_view);

        if (folder.equals("statuses")){
            WebService.putStatus(imageView, imageUrl);
        }
        if (folder.equals("images")){
            WebService.putImageMessage(imageView, imageUrl);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
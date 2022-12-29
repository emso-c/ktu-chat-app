package com.example.chatapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Classes.WebService;

public class FullscreenImageActivity extends AppCompatActivity {

    private final int TIMER_LENGTH_IN_MILLISECONDS = 8000;
    private final int TIMER_INTERVAL_IN_MILLISECONDS = 10;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        String imageUrl = getIntent().getStringExtra("image_url");
        String folder = getIntent().getStringExtra("folder");
        ImageView imageView = findViewById(R.id.fullscreen_image_view);
        ProgressBar bar = findViewById(R.id.progress_bar);

        if (folder.equals("statuses")){
            WebService.putStatus(imageView, imageUrl);
            bar.setVisibility(View.VISIBLE);
            bar.setMax(TIMER_LENGTH_IN_MILLISECONDS);
            timer = new CountDownTimer(TIMER_LENGTH_IN_MILLISECONDS, TIMER_INTERVAL_IN_MILLISECONDS) {
                @Override
                public void onTick(long millisUntilFinished) {
                    bar.setProgress(TIMER_LENGTH_IN_MILLISECONDS - (int) millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    finish();
                }
            };
            timer.start();

        }
        if (folder.equals("images")){
            WebService.putImageMessage(imageView, imageUrl);
            bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
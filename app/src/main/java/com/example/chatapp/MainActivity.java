package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.chatapp.Classes.WebService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Models.UserManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.chatapp.ui.main.SectionsPagerAdapter;
import com.example.chatapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabTextColors(
                getResources().getColor(R.color.bright_grey),
                getResources().getColor(R.color.blue_ultra_light)
        );
        tabLayout.setSelectedTabIndicatorColor(
                getResources().getColor(R.color.blue_ultra_light)
        );
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_option:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logoutOptionSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_option){
            logout();
        }
    }

    public void logout(){
        UserManager manager = UserManager.getInstance();
        manager.user = null;
        WebService webService = new WebService(
                getString(R.string.hostname),
                getString(R.string.port),
                UserManager.getInstance(),
                this
        );
        webService.logout();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("EE", "MAINACTIVITY DESTROYED");
        logout();
    }
}
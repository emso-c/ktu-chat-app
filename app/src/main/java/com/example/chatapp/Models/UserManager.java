package com.example.chatapp.Models;

import com.google.firebase.auth.FirebaseUser;



public class UserManager {
    private static volatile UserManager INSTANCE = null;
    public FirebaseUser user = null;
    public String id = "";
    public String uid = "";
    public String profilePic = "";
    public String username = "";
    public String password = "";
    public String email = "";
    public String phoneNumber = "";

    private UserManager() {}

    public static UserManager getInstance() {
        if(INSTANCE == null) {
            synchronized (UserManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserManager();
                }
            }
        }
        return INSTANCE;
    }
}
package com.example.chatapp.Classes;

import com.google.firebase.auth.FirebaseUser;



public class UserManager {
    private static volatile UserManager INSTANCE = null;
    public FirebaseUser user = null;

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
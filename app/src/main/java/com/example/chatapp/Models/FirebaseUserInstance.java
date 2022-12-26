package com.example.chatapp.Models;

import com.google.firebase.auth.FirebaseUser;



public class FirebaseUserInstance {
    private static volatile FirebaseUserInstance INSTANCE = null;
    public FirebaseUser user = null;
    public String id = "";
    public String uid = "";
    public String profilePic = "";
    public String username = "";
    public String password = "";
    public String email = "";
    public String phoneNumber = "";

    private FirebaseUserInstance() {}

    public static FirebaseUserInstance getInstance() {
        if(INSTANCE == null) {
            synchronized (FirebaseUserInstance.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FirebaseUserInstance();
                }
            }
        }
        return INSTANCE;
    }
}
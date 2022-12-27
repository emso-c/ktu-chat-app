package com.example.chatapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Models.FirebaseUserInstance;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilPasswordAgain;

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordAgain;

    FirebaseUserInstance manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.e("REGISTER", "create");

        manager = FirebaseUserInstance.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tilName = findViewById(R.id.text_input_username_register);
        tilEmail = findViewById(R.id.text_input_email_register);
        tilPassword = findViewById(R.id.text_input_password_register);
        tilPasswordAgain = findViewById(R.id.text_input_password_again_register);

        Button btnSignUpEmailPass = findViewById(R.id.btn_register);

        editTextEmail = findViewById(R.id.edit_text_email_register);
        editTextPassword = findViewById(R.id.edit_text_password_register);
        editTextPasswordAgain = findViewById(R.id.edit_text_password_again_register);
        editTextName = findViewById(R.id.edit_text_username_register);


        btnSignUpEmailPass.setOnClickListener(view -> {
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String passwordAgain = editTextPasswordAgain.getText().toString();

            if (name.isEmpty()){
                tilEmail.setError("Name cannot be empty");
                return;
            }
            if (email.isEmpty()){
                tilEmail.setError("Email cannot be empty");
                return;
            }
            if (password.isEmpty()) {
                tilPassword.setError("Password cannot be empty");
                return;
            }
            if (password.length() < 6) {
                tilPassword.setError("Password length should be at least 6");
                return;
            }
            if (!password.equals(passwordAgain)){
                tilPasswordAgain.setError("Passwords should match");
                return;
            }
            createAccountWithEmailAndPassword(email, password);
        });

        editTextName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilName.setError(null);
            }
        });
        editTextEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilEmail.setError(null);
            }
        });
        editTextPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilPassword.setError(null);
            }
        });
        editTextPasswordAgain.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilPasswordAgain.setError(null);
            }
        });
    }

    private void createAccountWithEmailAndPassword(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(RegisterActivity.this, "Created Account",
                                Toast.LENGTH_SHORT).show();
                        manager.user = mAuth.getCurrentUser();
                        manager.username = editTextName.getText().toString();
                        login();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Couldn't Create Account.",
                                Toast.LENGTH_SHORT).show();
                        editTextName.setText("");
                        editTextEmail.setText("");
                        editTextPassword.setText("");
                        editTextPasswordAgain.setText("");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
        editTextPasswordAgain.setText("");
        finish();
    }

    public void login(){
        if (manager.user == null)
            return;

        manager.uid = manager.user.getUid();
        manager.profilePic = String.valueOf(manager.user.getPhotoUrl());
        manager.password = "1234";
        manager.email = manager.user.getEmail();
        manager.phoneNumber = manager.user.getPhoneNumber();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
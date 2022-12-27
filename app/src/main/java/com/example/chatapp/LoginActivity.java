package com.example.chatapp;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapp.Models.FirebaseUserInstance;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    FirebaseUserInstance manager = FirebaseUserInstance.getInstance();
    private FirebaseAuth mAuth;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("LOGIN", "create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tilEmail = findViewById(R.id.text_input_email);
        tilPassword = findViewById(R.id.text_input_password);

        Button btnSignIn = findViewById(R.id.btn_sign_in);
        Button btnLoginEmailPass = findViewById(R.id.btn_login_email_pass);
        Button btnSignUpEmailPass = findViewById(R.id.btn_sign_up_email_pass);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);


        btnSignIn.setOnClickListener(view -> {
            if (manager.user == null){
                createSignInIntent();
            } else{
                Toast.makeText(
                        getApplicationContext(),
                        "Already logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnLoginEmailPass.setOnClickListener(view -> {
            if (manager.user == null){
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

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
                signInWithEmailAndPassword(email, password);
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Already logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUpEmailPass.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
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
    }

    private void signInWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "signInUserWithEmail:success");
                        manager.user = mAuth.getCurrentUser();
                        login();
                    } else {
                        Log.w(TAG, "signInUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        editTextEmail.setText("");
                        editTextPassword.setText("");
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO consider storing login info in sqlite
        if(manager.user != null){
            login();
        }
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            manager.user = FirebaseAuth.getInstance().getCurrentUser();
            login();
        }
    }

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    public void login(){
        if (manager.user == null)
            return;

        manager.uid = manager.user.getUid();
        manager.profilePic = String.valueOf(manager.user.getPhotoUrl());
        manager.username = manager.user.getDisplayName();
        manager.password = "1234";
        manager.email = manager.user.getEmail();
        manager.phoneNumber = manager.user.getPhoneNumber();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
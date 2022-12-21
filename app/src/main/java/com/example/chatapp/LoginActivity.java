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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(view -> {
            if (user == null){
                createSignInIntent();
            } else{
                Toast.makeText(
                        getApplicationContext(),
                        "Already logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button btnInfo = findViewById(R.id.btn_usr_info);
        btnInfo.setOnClickListener(view -> {
            if (user != null){
                Toast.makeText(
                        getApplicationContext(),
                        user.getUid(),
                        Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(
                        getApplicationContext(),
                        "User not logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button btnSingOut = findViewById(R.id.btn_signout);
        btnSingOut.setOnClickListener(view -> {
            if (user != null){
                FirebaseAuth.getInstance().signOut();
                user = null;
                //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Toast.makeText(
                        getApplicationContext(),
                        "Logged out",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Not logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // EMAIL AND PASS AUTH
        Button btnLoginEmailPass = findViewById(R.id.btn_login_email_pass);
        btnLoginEmailPass.setOnClickListener(view -> {
            if (user == null){
                EditText editTextEmail = findViewById(R.id.editTextEmail);
                EditText editTextPassword = findViewById(R.id.editTextPassword);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                signInWithEmailAndPassword(email, password);
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Already logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button btnSignUpEmailPass = findViewById(R.id.btn_sign_up_email_pass);
        btnSignUpEmailPass.setOnClickListener(view -> {
            if (user == null){
                EditText editTextEmail = findViewById(R.id.editTextEmail);
                EditText editTextPassword = findViewById(R.id.editTextPassword);
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                createAccountWithEmailAndPassword(email, password);
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Already logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createAccountWithEmailAndPassword(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Created Account",
                                Toast.LENGTH_SHORT).show();
                        // TODO redirect user
                        // updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Couldn't Create Account.",
                                Toast.LENGTH_SHORT).show();
                        // TODO handle create user
                        // updateUI(null);
                    }
                });
    }

    private void signInWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "signInUserWithEmail:success");
                        user = mAuth.getCurrentUser();
                    } else {
                        Log.w(TAG, "signInUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        // TODO handle sign-in fail
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            // TODO user already logged in, redirect
        }
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            user = FirebaseAuth.getInstance().getCurrentUser();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
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
}
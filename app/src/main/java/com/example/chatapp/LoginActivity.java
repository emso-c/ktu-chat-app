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

import com.example.chatapp.Models.UserManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    UserManager manager = UserManager.getInstance();
    private FirebaseAuth mAuth;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        tilEmail = findViewById(R.id.text_input_email);
        tilPassword = findViewById(R.id.text_input_password);

        Button btnSignIn = findViewById(R.id.btn_sign_in);
        Button btnInfo = findViewById(R.id.btn_usr_info);
        Button btnSignOut = findViewById(R.id.btn_signout);
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

        btnInfo.setOnClickListener(view -> {
            if (manager.user != null){
                Toast.makeText(
                        getApplicationContext(),
                        manager.user.getUid(),
                        Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(
                        getApplicationContext(),
                        "User not logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnSignOut.setOnClickListener(view -> {
            if (manager.user != null){
                FirebaseAuth.getInstance().signOut();
                manager.user = null;
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
                login("0");
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Already logged in",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUpEmailPass.setOnClickListener(view -> {
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
                createAccountWithEmailAndPassword(email, password);
                login("0");
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Already logged in",
                        Toast.LENGTH_SHORT).show();
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
                        manager.user = mAuth.getCurrentUser();
                        login();
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
        if(manager.user != null){
            login();
        }
        // TODO TEMP REMOVE THIS LINE
        // TODO consider storing login info in sqlite
        login("0");
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            manager.user = FirebaseAuth.getInstance().getCurrentUser();
            login();
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

    public void login(String id){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // TODO get id programmatically
        intent.putExtra("user_id", id);
        startActivity(intent);
    }
}
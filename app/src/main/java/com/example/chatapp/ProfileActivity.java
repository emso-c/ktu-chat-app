package com.example.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Classes.WebService;
import com.example.chatapp.Models.FirebaseUserInstance;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private TextInputLayout tilStatus;
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private EditText editTextStatus;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private ImageView imageViewPhoto;
    private ImageView imageViewUpdate;

    private TextView textViewStatus;
    private TextView textViewUsername;

    Button btnUpdate;
    Button btnDeleteAcc;

    String photoUrl = "";

    WebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tilStatus = findViewById(R.id.text_input_status);
        tilName = findViewById(R.id.text_input_username_register);
        tilEmail = findViewById(R.id.text_input_email_register);
        tilPassword = findViewById(R.id.text_input_password_register);


        editTextStatus = findViewById(R.id.edit_text_status);
        editTextName = findViewById(R.id.edit_text_username_register);
        editTextEmail = findViewById(R.id.edit_text_email_register);
        editTextPassword = findViewById(R.id.edit_text_password_register);

        btnUpdate = findViewById(R.id.btn_register);
        btnDeleteAcc = findViewById(R.id.btn_del_acc);

        imageViewPhoto = findViewById(R.id.image_view_profile_pic);
        imageViewUpdate = findViewById(R.id.image_view_update);

        textViewStatus = findViewById(R.id.text_view_status);
        textViewUsername = findViewById(R.id.text_view_username);

        btnUpdate.setOnClickListener(view -> {
            String status = editTextStatus.getText().toString();
            String name = editTextName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (status.isEmpty()){
                status = "";
            }
            if (name.isEmpty()){
                name = "";
            }
            if (email.isEmpty()){
                email = "";
            }
            else{
                Toast.makeText(this, "Email update is temporarily disabled", Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.isEmpty()){
                password = "";
            }
            else{
                Toast.makeText(this, "Password update is temporarily disabled", Toast.LENGTH_SHORT).show();
                return;
            }

            webService.updateUser(status, name, password, "");
            recreate();
        });
        imageViewUpdate.setOnClickListener(view -> openSomeActivityForResult());

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
        editTextStatus.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tilStatus.setError(null);
            }
        });



        webService = new WebService(
                getString(R.string.hostname),
                getString(R.string.port),
                FirebaseUserInstance.getInstance(),
                this
        );
        textViewUsername.setText(webService.webServiceUser.username);
        textViewStatus.setText(webService.webServiceUser.status);
        webService.putProfilePicture(imageViewPhoto);
    }

    public void openSomeActivityForResult() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri selectedImageUri = data.getData();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference ref = storageRef.child("profile_photos/"+webService.webServiceUser.firebaseUid);

                    UploadTask uploadTask = ref.putFile(selectedImageUri);
                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads here
                    }).addOnSuccessListener(taskSnapshot -> {
                        webService.putProfilePicture(imageViewPhoto);
                    });
                }
            });

    @Override
    public void onBackPressed() {
        finish();
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}
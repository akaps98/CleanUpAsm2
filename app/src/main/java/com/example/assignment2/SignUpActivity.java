package com.example.assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.assignment2.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = SignUpActivity.class.getName();
    EditText inputEmail, inputPassword, inputCheckPassword;
    Button registerButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.register_email);
        inputPassword = findViewById(R.id.register_password);
        inputCheckPassword = findViewById(R.id.register_checkPassword);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String checkPassword = inputCheckPassword.getText().toString();

                if (!email.equals("") && !password.equals("")) {
                    if(password.length() >= 6) {
                        if(password.equals(checkPassword)) {
                            saveUser(email, password);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Please double-check password", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Password length must exceed 6", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Please input email and password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    writeNewUser(email, password);

                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(SignUpActivity.this, "Success to register!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Already exists email address.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void writeNewUser(String email, String password) {
        User user = new User(email, password);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("User").add(user)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
    }

    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
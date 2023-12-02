package com.example.assignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
    EditText emailText, passwordText;
    Button signInButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sign In");

        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.input_email);
        passwordText = findViewById(R.id.input_password);
        signInButton = findViewById(R.id.signIn_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(LogInActivity.this, "Log in Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LogInActivity.this, UserDashboardActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LogInActivity.this, "Login failed!\nCheck your email & password again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
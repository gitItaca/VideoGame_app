package com.example.videogame_app.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videogame_app.MainActivity;
import com.example.videogame_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText myEmail, myPass;
    Button myLoginButton;
    TextView myToRegister;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myEmail = findViewById(R.id.emailLogin);
        myPass = findViewById(R.id.passwordLogin);
        myLoginButton = findViewById(R.id.loginButton);
        myToRegister = findViewById(R.id.toRegister);
        progressBar = findViewById(R.id.progressBarLogin);
        firebaseAuth = FirebaseAuth.getInstance();

        myLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = myEmail.getText().toString().trim();
                String pass = myPass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    myEmail.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    myPass.setError("Password is required.");
                }
                if(pass.length() < 6){
                    myPass.setError("Password minimun length must be 6 characters.");
                }

                progressBar.setVisibility(View.VISIBLE);

                //Authenticate user
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "User logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Login.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        myToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}
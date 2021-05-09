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

public class Register extends AppCompatActivity {
    EditText myPersonName, myEmailAddress, myPassword, myPasswordRepeat;
    Button myRegisterButton;
    TextView myToLogin;
    ProgressBar myProgressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myPersonName = findViewById(R.id.personName);
        myEmailAddress = findViewById(R.id.emailLogin);
        myPassword = findViewById(R.id.passwordLogin);
        myRegisterButton = findViewById(R.id.registerButton);
        myToLogin = findViewById(R.id.toRegister);

        myProgressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        myRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = myEmailAddress.getText().toString().trim();
                String pass = myPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    myEmailAddress.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    myPassword.setError("Password is required.");
                }
                if(pass.length() < 6){
                    myPassword.setError("Password minimun length must be 6 characters.");
                }

                myProgressBar.setVisibility(View.VISIBLE);

                //Register user in firebase
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        myToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }


}
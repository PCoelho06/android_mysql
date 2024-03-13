package com.example.mysqlapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginOkActivity extends AppCompatActivity {

    private TextView id, username, email;
    private Button editUsername, editPassword, editEmail, logoutButton;
    private String userId, userUsername, userEmail;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ok);

        id = findViewById(R.id.userId);
        username = findViewById(R.id.userUsername);
        email = findViewById(R.id.userEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editEmail = findViewById(R.id.editEmail);
        logoutButton = findViewById(R.id.logoutButton);

        sessionManager = new SessionManager(this);

        userId = sessionManager.getId();
        userUsername = sessionManager.getUsername();
        userEmail = sessionManager.getEmail();
        id.setText(userId);
        username.setText(userUsername);
        email.setText(userEmail);

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditUsernameActivity.class);
                startActivity(i);
                finish();
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditPasswordActivity.class);
                startActivity(i);
                finish();
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditEmailActivity.class);
                startActivity(i);
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
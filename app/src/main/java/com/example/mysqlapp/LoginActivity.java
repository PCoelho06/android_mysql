package com.example.mysqlapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton, registerButton;
    private EditText loginInput, passwordInput;
    private RequestQueue queue;
    private MyRequest request;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        loginInput = findViewById(R.id.loginInput);
        passwordInput = findViewById(R.id.passwordInput);

        queue = MySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLogged()) {
            Intent i = new Intent(getApplicationContext(), LoginOkActivity.class);
            startActivity(i);
            finish();
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String LOGIN = loginInput.getText().toString().trim();
                final String PASSWORD = passwordInput.getText().toString().trim();

                if(LOGIN.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Merci de renseigner une adresse email ou un nom d'utilisateur", Toast.LENGTH_LONG).show();
                } else if(PASSWORD.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Merci de renseigner votre mot de passe", Toast.LENGTH_LONG).show();
                } else {

                    HashMap<String, String> signIn = new HashMap<>();
                    signIn.put("login", LOGIN);
                    signIn.put("password", PASSWORD);

                    try {
                        request.request(
                            signIn,
                            new MyRequest.PhpReturns() {
                                @Override
                                public void noProblem(String user) throws JSONException {
                                    JSONObject userData = new JSONObject(user);
                                    sessionManager.insertUser(String.valueOf(userData.getInt("id")), userData.getString("username"), userData.getString("email"));

                                    Intent i = new Intent(getApplicationContext(), LoginOkActivity.class);
                                    startActivity(i);
                                    finish();
                                }

                                @Override
                                public void problemEncountered(String message) {
                                    Log.d("PHP", "passage dans problemEncountered");
                                    Toast.makeText(LoginActivity.this, "Attention: " + message, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void systemError(String message) {
                                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                                }
                            });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
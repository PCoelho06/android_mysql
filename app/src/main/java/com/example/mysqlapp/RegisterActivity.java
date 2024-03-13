package com.example.mysqlapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailInput, usernameInput, passwordInput, passwordRepeatInput;
    private Button registerButton;
    private RequestQueue queue;
    private MyRequest request;

    private void init() {
        emailInput.setText("");
        usernameInput.setText("");
        passwordInput.setText("");
        passwordRepeatInput.setText("");
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.emailInput);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        passwordRepeatInput = findViewById(R.id.passwordRepeatInput);
        registerButton = findViewById(R.id.registerButton);

        init();

        queue = MySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String USERNAME = usernameInput.getText().toString().trim();
                final String EMAIL = emailInput.getText().toString().trim();
                final String PASSWORD = passwordInput.getText().toString().trim();
                final String PASSWORD_CONFIRM = passwordRepeatInput.getText().toString().trim();


                if (!isValidEmail(EMAIL)) {
                    Toast.makeText(RegisterActivity.this, "Merci de fournir une adresse email correcte", Toast.LENGTH_LONG).show();
                } else if(EMAIL.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Merci de renseigner une adresse email", Toast.LENGTH_LONG).show();
                } else if(USERNAME.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Merci de renseigner un nom d'utilisateur", Toast.LENGTH_LONG).show();
                } else if(PASSWORD.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Merci de renseigner un mot de passe", Toast.LENGTH_LONG).show();
                } else if(PASSWORD_CONFIRM.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Merci de confirmer le mot de passe choisi", Toast.LENGTH_LONG).show();
                } else if(!PASSWORD.equals(PASSWORD_CONFIRM)) {
                    Toast.makeText(RegisterActivity.this, "Les deux mots de passes ne sont pas identiques", Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, String> register = new HashMap<>();
                    register.put("username", USERNAME);
                    register.put("email", EMAIL);
                    register.put("password", PASSWORD);
                    register.put("password_confirm", PASSWORD_CONFIRM);

                    try {
                        request.request(
                            register,
                            new MyRequest.PhpReturns() {
                                @Override
                                public void noProblem(String message) {
                                    Log.d("PHP", "message PHP: " + message);
                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                                    startActivity(i);
                                    finish();
                                }

                                @Override
                                public void problemEncountered(String message) {
                                    Log.d("PHP", "passage dans problemEncountered");
                                    Toast.makeText(RegisterActivity.this, "Attention: " + message, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void systemError(String message) {
                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
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
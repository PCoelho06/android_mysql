package com.example.mysqlapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class EditPasswordActivity extends AppCompatActivity {
    private EditText oldPassword, newPassword, newPasswordConfirm;
    private Button editPasswordButton;
    private RequestQueue queue;
    private MyRequest request;
    private String userId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        oldPassword = findViewById(R.id.oldPasswordInput);
        newPassword = findViewById(R.id.newPasswordInput);
        newPasswordConfirm = findViewById(R.id.newPasswordInputConfirm);
        editPasswordButton = findViewById(R.id.editPasswordButton);

        queue = MySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        sessionManager = new SessionManager(this);

        userId = sessionManager.getId();

        editPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int ID = Integer.parseInt(userId);
                final String OLD_PASSWORD = oldPassword.getText().toString().trim();
                final String NEW_PASSWORD = newPassword.getText().toString().trim();
                final String NEW_PASSWORD_CONFIRM = newPasswordConfirm.getText().toString().trim();

                if(OLD_PASSWORD.isEmpty()) {
                    Toast.makeText(EditPasswordActivity.this, "Merci de renseigner l'ancien mot de passe", Toast.LENGTH_LONG).show();
                } else if(NEW_PASSWORD.isEmpty()) {
                    Toast.makeText(EditPasswordActivity.this, "Merci de renseigner un nouveau mot de passe", Toast.LENGTH_LONG).show();
                } else if(NEW_PASSWORD_CONFIRM.isEmpty()) {
                    Toast.makeText(EditPasswordActivity.this, "Merci de confirmer le nouveau mot de passe choisi", Toast.LENGTH_LONG).show();
                } else if(!NEW_PASSWORD.equals(NEW_PASSWORD_CONFIRM)) {
                    Toast.makeText(EditPasswordActivity.this, "Les deux mots de passes ne sont pas identiques", Toast.LENGTH_LONG).show();
                } else {

                    HashMap<String, String> passwordData = new HashMap<>();
                    passwordData.put("id", String.valueOf(ID));
                    passwordData.put("old_password", OLD_PASSWORD);
                    passwordData.put("new_password", NEW_PASSWORD);
                    passwordData.put("new_password_confirm", NEW_PASSWORD_CONFIRM);

                    try {
                        request.request(
                            passwordData,
                            new MyRequest.PhpReturns() {
                                @Override
                                public void noProblem(String user) throws JSONException {
                                    Intent i = new Intent(getApplicationContext(), LoginOkActivity.class);
                                    Toast.makeText(EditPasswordActivity.this, "Votre mot de passe a bien été modifié.", Toast.LENGTH_LONG).show();
                                    startActivity(i);
                                    finish();
                                }

                                @Override
                                public void problemEncountered(String message) {
                                    Log.d("PHP", "passage dans problemEncountered");
                                    Toast.makeText(EditPasswordActivity.this, "Attention: " + message, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void systemError(String message) {
                                    Toast.makeText(EditPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                                }
                            }
                        );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
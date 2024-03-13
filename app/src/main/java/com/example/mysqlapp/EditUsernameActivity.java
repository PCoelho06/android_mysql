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

public class EditUsernameActivity extends AppCompatActivity {
    private EditText editUsername, editUsernameConfirm;
    private Button editUsernameButton;
    private RequestQueue queue;
    private MyRequest request;
    private String userId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_username);

        editUsername = findViewById(R.id.editUsernameInput);
        editUsernameConfirm = findViewById(R.id.editUsernameInputConfirm);
        editUsernameButton = findViewById(R.id.editUsernameButton);

        queue = MySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        sessionManager = new SessionManager(this);

        userId = sessionManager.getId();

        editUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int ID = Integer.parseInt(userId);
                final String NEW_USERNAME = editUsername.getText().toString().trim();
                final String NEW_USERNAME_CONFIRM = editUsernameConfirm.getText().toString().trim();

                if(NEW_USERNAME.isEmpty()) {
                    Toast.makeText(EditUsernameActivity.this, "Merci de renseigner un nouveau nom d'utilisateur", Toast.LENGTH_LONG).show();
                } else if(NEW_USERNAME_CONFIRM.isEmpty()) {
                    Toast.makeText(EditUsernameActivity.this, "Merci de confirmer le nouveau nom d'utilisateur choisi", Toast.LENGTH_LONG).show();
                } else if(!NEW_USERNAME.equals(NEW_USERNAME_CONFIRM)) {
                    Toast.makeText(EditUsernameActivity.this, "Les deux noms d'utilisateur ne sont pas identiques", Toast.LENGTH_LONG).show();
                } else {

                    HashMap<String, String> usernameData = new HashMap<>();
                    usernameData.put("id", String.valueOf(ID));
                    usernameData.put("new_username", NEW_USERNAME);
                    usernameData.put("new_username_confirm", NEW_USERNAME_CONFIRM);

                    try {
                        request.request(
                            usernameData,
                            new MyRequest.PhpReturns() {
                                @Override
                                public void noProblem(String user) throws JSONException {
                                    Intent i = new Intent(getApplicationContext(), LoginOkActivity.class);
                                    Toast.makeText(EditUsernameActivity.this, "Votre nom d'utilisateur a bien été modifié.", Toast.LENGTH_LONG).show();
                                    JSONObject userData = new JSONObject(user);
                                    sessionManager.setEmail(userData.getString("username"));
                                    startActivity(i);
                                    finish();
                                }

                                @Override
                                public void problemEncountered(String message) {
                                    Log.d("PHP", "passage dans problemEncountered");
                                    Toast.makeText(EditUsernameActivity.this, "Attention: " + message, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void systemError(String message) {
                                    Toast.makeText(EditUsernameActivity.this, message, Toast.LENGTH_LONG).show();
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
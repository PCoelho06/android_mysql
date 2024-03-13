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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditEmailActivity extends AppCompatActivity {
    private EditText editEmailInput, editEmailInputConfirm;
    private Button editEmailButton;
    private RequestQueue queue;
    private MyRequest request;
    private String userId;
    private SessionManager sessionManager;

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        editEmailInput = findViewById(R.id.editEmailInput);
        editEmailInputConfirm = findViewById(R.id.editEmailInputConfirm);
        editEmailButton = findViewById(R.id.editEmailButton);

        queue = MySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        sessionManager = new SessionManager(this);

        userId = sessionManager.getId();

        editEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int ID = Integer.parseInt(userId);
                final String NEW_EMAIL = editEmailInput.getText().toString().trim();
                final String NEW_EMAIL_CONFIRM = editEmailInputConfirm.getText().toString().trim();

                if(NEW_EMAIL.isEmpty()) {
                    Toast.makeText(EditEmailActivity.this, "Merci de renseigner une nouvelle adresse email", Toast.LENGTH_LONG).show();
                } else if(!isValidEmail(NEW_EMAIL)) {
                    Toast.makeText(EditEmailActivity.this, "Merci de renseigner une nouvelle adresse email valide", Toast.LENGTH_LONG).show();
                } else if(NEW_EMAIL_CONFIRM.isEmpty()) {
                    Toast.makeText(EditEmailActivity.this, "Merci de confirmer la nouvelle adresse email", Toast.LENGTH_LONG).show();
                } else if(!NEW_EMAIL.equals(NEW_EMAIL_CONFIRM)) {
                    Toast.makeText(EditEmailActivity.this, "Les deux adresses email ne sont pas identiques", Toast.LENGTH_LONG).show();
                } else {

                    HashMap<String, String> emailData = new HashMap<>();
                    emailData.put("id", String.valueOf(ID));
                    emailData.put("new_email", NEW_EMAIL);
                    emailData.put("new_email_confirm", NEW_EMAIL_CONFIRM);

                    try {
                        request.request(
                            emailData,
                            new MyRequest.PhpReturns() {
                                @Override
                                public void noProblem(String user) throws JSONException {
                                    Intent i = new Intent(getApplicationContext(), LoginOkActivity.class);
                                    Toast.makeText(EditEmailActivity.this, "Votre adresse email a bien été modifié.", Toast.LENGTH_LONG).show();
                                    JSONObject userData = new JSONObject(user);
                                    sessionManager.setEmail(userData.getString("email"));
                                    startActivity(i);
                                    finish();
                                }

                                @Override
                                public void problemEncountered(String message) {
                                    Log.d("PHP", "passage dans problemEncountered");
                                    Toast.makeText(EditEmailActivity.this, "Attention: " + message, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void systemError(String message) {
                                    Toast.makeText(EditEmailActivity.this, message, Toast.LENGTH_LONG).show();
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
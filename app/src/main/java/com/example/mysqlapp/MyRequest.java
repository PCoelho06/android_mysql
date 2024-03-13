package com.example.mysqlapp;

import android.content.Context;
import android.content.Intent;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarException;

public class MyRequest {

    private Context context;
    private RequestQueue queue;

    private String url;
    private String requestType;

    public MyRequest(Context context, RequestQueue queue) {
        this.context = context;
        this.queue = queue;
    }

    public void request(HashMap<String, String> hashMap, final PhpReturns phpReturns) throws Exception {

        if(hashMap.get("username") != null && hashMap.get("email") != null) {
            url = "http://192.168.1.201/android_mysql/index.php?user=register";
            requestType = "register";
        } else if (hashMap.get("login") != null) {
            url = "http://192.168.1.201/android_mysql/index.php?user=login";
        } else if (hashMap.get("id") != null) {
            if(hashMap.get("new_password") != null){
                url = "http://192.168.1.201/android_mysql/index.php?edit=password";
            } else if (hashMap.get("new_email") != null) {
                url = "http://192.168.1.201/android_mysql/index.php?edit=email";
            } else if (hashMap.get("new_username") != null) {
                url = "http://192.168.1.201/android_mysql/index.php?edit=username";
            } else {
                throw new Exception("Ce champs ne peut pas encore être édité.");
            }
        } else {
            throw new Exception("Cette requête n'est pas reconnue");
        }

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("PHP", response);
                try{
                    JSONObject json = new JSONObject(response);
                    boolean error = json.getBoolean("error");
                    if(error){
                        phpReturns.problemEncountered(json.getString("message"));
                    } else {
                        if(json.has("user")) {
                            phpReturns.noProblem(json.getString("user"));
                        } else {
                            phpReturns.noProblem(json.getString("message"));
                        }
                    }
                } catch (JSONException e) {
                    Log.d("PHP", "catch de la request:" + e);
                    phpReturns.systemError("Une erreur est survenue, veuillez réessayer ultérieurement.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError) {
                    phpReturns.systemError("Une erreur réseau s'est produite, \n\r impossible de joindre le serveur");
                } else if (error instanceof VolleyError) {
                    phpReturns.systemError("Une erreur s'est produite, impossible de joindre le serveur");
                }
                Log.d("PHP", "error: "+ error);
            }
        }){
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return hashMap;
            }
        };

        queue.add(request);
    }

    public interface PhpReturns {
        void noProblem(String message) throws JSONException;
        void problemEncountered(String message);
        void systemError(String message);
    }
}

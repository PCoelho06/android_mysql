package com.example.mysqlapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private final static String PREFS_NAME = "mon_fichierXml";
    private final static int PRIVATE_MODE = Context.MODE_PRIVATE;
    private final static String IS_LOGGED = "isLogged";
    private final static String USERNAME = "UsernameKey";
    private final static String ID = "IdKey";
    private final static String EMAIL = "EmailKey";


    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREFS_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public boolean isLogged() {
        return preferences.getBoolean(IS_LOGGED, false);
    }

    public String getUsername() {
        return preferences.getString(USERNAME, null);
    }
    public String getId() {
        return preferences.getString(ID, null);
    }
    public String getEmail() {
        return preferences.getString(EMAIL, null);
    }
    public void setUsername(String username) {
        editor.putString(USERNAME, username);
        editor.apply();
    }
    public void setId(String id) {
        editor.putString(ID, id);
        editor.apply();
    }
    public void setEmail(String email) {
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public void insertUser(String id, String username, String email) {
        editor.putBoolean(IS_LOGGED, true);
        editor.putString(ID, id);
        editor.putString(USERNAME, username);
        editor.putString(EMAIL, email);
        editor.apply();
    }

    public void logout() {
        editor.clear().apply();
    }
}

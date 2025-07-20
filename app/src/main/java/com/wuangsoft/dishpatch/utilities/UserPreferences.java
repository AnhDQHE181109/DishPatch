package com.wuangsoft.dishpatch.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wuangsoft.dishpatch.models.User;

public class UserPreferences {
    private static final String PREFS_NAME = "DishPatchPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_DATA = "userData";
    private static final String KEY_USER_ID = "userId";
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;
    
    public UserPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }
    
    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_DATA, userJson);
        editor.putString(KEY_USER_ID, user.getUid());
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }
    
    public User getUser() {
        String userJson = prefs.getString(KEY_USER_DATA, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
    
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }
    
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void logout() {
        editor.clear();
        editor.apply();
    }
}

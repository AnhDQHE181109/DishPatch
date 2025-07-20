package com.wuangsoft.dishpatch.deploy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wuangsoft.dishpatch.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "DishPatchPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        
        if (isLoggedIn) {
            // User is already logged in, go to main activity
            startActivity(new Intent(this, MainActivityDeploy.class));
            finish();
            return;
        }
        
        setContentView(R.layout.welcome);
        
        Button loginButton = findViewById(R.id.button);
        Button signUpButton = findViewById(R.id.button2);
        
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, WelcomePage.class);
            intent.putExtra("fragment", "login");
            startActivity(intent);
        });
        
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, WelcomePage.class);
            intent.putExtra("fragment", "register");
            startActivity(intent);
        });
    }
}

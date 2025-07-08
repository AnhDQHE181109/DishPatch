package com.wuangsoft.dishpatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wuangsoft.dishpatch.R;

public class SignupActivity extends AppCompatActivity {

    EditText signUpName, signUpEmail, signUpUsername, signUpPassword;
    TextView loginRedirectText;
    Button signUpButton;
    FirebaseDatabase database;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        signUpName = findViewById(R.id.signup_name);
        signUpEmail = findViewById(R.id.signup_email);
        signUpUsername = findViewById(R.id.signup_username);
        signUpPassword = findViewById(R.id.signup_password);
        signUpButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = signUpName.getText().toString();
                String email = signUpEmail.getText().toString();
                String username = signUpUsername.getText().toString();
                String password = signUpPassword.getText().toString();

                HelperClass helperClass = new HelperClass(name, email, username, password);
                reference.child(name).setValue(helperClass);

                Toast.makeText(SignupActivity.this, "Sign up successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
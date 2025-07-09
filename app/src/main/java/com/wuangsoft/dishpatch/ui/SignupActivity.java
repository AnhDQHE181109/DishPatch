package com.wuangsoft.dishpatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wuangsoft.dishpatch.R;

public class SignupActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText editEmailRegister, editNameRegister, editUsernameRegister, editPasswordRegister;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        TextView signinRedirectText = findViewById(R.id.loginRedirectText);
        mAuth = FirebaseAuth.getInstance();
        editNameRegister = findViewById(R.id.signup_name);
        editEmailRegister = findViewById(R.id.signup_email);
        editUsernameRegister = findViewById(R.id.signup_username);
        editPasswordRegister = findViewById(R.id.signup_password);
        btnCreate = findViewById(R.id.signup_button);

        signinRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, username, password;
                name = editNameRegister.getText().toString().trim();
                email = editEmailRegister.getText().toString().trim();
                username = editUsernameRegister.getText().toString().trim();
                password = editPasswordRegister.getText().toString().trim();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "The field must not Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Register successfully"+ mAuth.getCurrentUser().getUid().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                        }
                        }
                    });
                }
            }
        });
    }
}
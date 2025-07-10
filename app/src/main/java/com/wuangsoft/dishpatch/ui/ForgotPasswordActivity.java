package com.wuangsoft.dishpatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.wuangsoft.dishpatch.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button btnReset;
    private EditText editTextEmail;
    private FirebaseAuth mAuth;
    private String strEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        btnReset = findViewById(R.id.forgot_button);
        editTextEmail = findViewById(R.id.forgot_email);
        mAuth = FirebaseAuth.getInstance();
        TextView loginRedirectText = findViewById(R.id.loginRedirectText);

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = editTextEmail.getText().toString().trim();

                if (TextUtils.isEmpty(strEmail)) {
                    editTextEmail.setError("Email field cannot be empty!");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    editTextEmail.setError("Invalid email address!");
                } else {
                    ResetPassword();
                }
            }
        });
    }

    private void ResetPassword() {
        mAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset password link has been sent!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPasswordActivity.this, "Error:-" +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    }

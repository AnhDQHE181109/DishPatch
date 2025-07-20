package com.wuangsoft.dishpatch.deploy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.User;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    
    private EditText editFullName, editUsername, editEmail, editPassword;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private UserPreferences userPreferences;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        userPreferences = new UserPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        editFullName = view.findViewById(R.id.fullnamebox);
        editUsername = view.findViewById(R.id.usernamebox);
        editEmail = view.findViewById(R.id.emailbox);
        editPassword = view.findViewById(R.id.passwordbox);
        registerButton = view.findViewById(R.id.loginbutton); // Button ID is loginbutton in register layout
        
        TextView loginTextView = view.findViewById(R.id.switchtoLogin);

        // Set Click Listeners
        registerButton.setOnClickListener(v -> performRegistration());

        loginTextView.setOnClickListener(v -> {
            // Navigate to LoginFragment
            if (getActivity() instanceof WelcomePage) {
                ((WelcomePage) getActivity()).navigateToLogin();
            }
        });
    }
    
    private void performRegistration() {
        String fullName = editFullName.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        
        // Validate inputs
        if (fullName.isEmpty()) {
            editFullName.setError("Full name is required");
            return;
        }
        
        if (username.isEmpty()) {
            editUsername.setError("Username is required");
            return;
        }
        
        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            return;
        }
        
        if (password.isEmpty()) {
            editPassword.setError("Password is required");
            return;
        }
        
        if (password.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            return;
        }
        
        registerButton.setEnabled(false);
        registerButton.setText("Creating Account...");
        
        // Create user with Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            createUserProfile(firebaseUser.getUid(), fullName, username, email);
                        }
                    } else {
                        registerButton.setEnabled(true);
                        registerButton.setText("Sign Up");
                        Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void createUserProfile(String uid, String fullName, String username, String email) {
        // Create User object
        User user = new User();
        user.setUid(uid);
        user.setName(fullName);
        user.setUsername(username);
        user.setEmail(email);
        user.setOnline("true");
        user.setRoleId(1); // Regular user role
        user.setTimestamp(String.valueOf(System.currentTimeMillis()));
        
        // Save to Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Save user data to SharedPreferences
                    userPreferences.saveUser(user);
                    
                    Toast.makeText(getContext(), "Account created successfully", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to main activity
                    Intent intent = new Intent(getActivity(), MainActivityDeploy.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .addOnFailureListener(e -> {
                    registerButton.setEnabled(true);
                    registerButton.setText("Sign Up");
                    Toast.makeText(getContext(), "Failed to create profile: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to create user profile", e);
                });
    }
}
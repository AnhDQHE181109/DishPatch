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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.User;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    
    private EditText editEmail, editPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private UserPreferences userPreferences;

    public LoginFragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        editEmail = view.findViewById(R.id.fullnamebox);
        editPassword = view.findViewById(R.id.passwordbox);
        loginButton = view.findViewById(R.id.loginbutton);
        
        TextView signUpTextView = view.findViewById(R.id.switchtoSignUp);
        TextView forgetTextView = view.findViewById(R.id.forgetbutton);

        // Set Click Listeners
        loginButton.setOnClickListener(v -> performLogin());

        signUpTextView.setOnClickListener(v -> {
            // Navigate to RegisterFragment
            if (getActivity() instanceof WelcomePage) {
                ((WelcomePage) getActivity()).navigateToRegister();
            }
        });
        
        forgetTextView.setOnClickListener(v -> {
            // Navigate to ForgetPasswordFragment
            if (getActivity() instanceof WelcomePage) {
                ((WelcomePage) getActivity()).nagvigateToForgetPassword();
            }
        });
    }
    
    private void performLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        
        if (email.isEmpty()) {
            editEmail.setError("Email is required");
            return;
        }
        
        if (password.isEmpty()) {
            editPassword.setError("Password is required");
            return;
        }
        
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            fetchUserDataAndLogin(firebaseUser.getUid());
                        }
                    } else {
                        loginButton.setEnabled(true);
                        loginButton.setText("Log In");
                        Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    private void fetchUserDataAndLogin(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Save user data to SharedPreferences
                        userPreferences.saveUser(user);
                        
                        // Navigate to main activity
                        Intent intent = new Intent(getActivity(), MainActivityDeploy.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else {
                    loginButton.setEnabled(true);
                    loginButton.setText("Log In");
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loginButton.setEnabled(true);
                loginButton.setText("Log In");
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database error", error.toException());
            }
        });
    }

}
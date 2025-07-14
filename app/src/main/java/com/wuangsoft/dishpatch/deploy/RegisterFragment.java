package com.wuangsoft.dishpatch.deploy;


import android.app.AlertDialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wuangsoft.dishpatch.R;
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

import java.util.HashMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wuangsoft.dishpatch.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {


    private Button btnCreate;
    private EditText editEmailRegister, editNameRegister, editUsernameRegister, editPasswordRegister;
    private FirebaseAuth mAuth;
    private String name, email, username, password;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        mAuth = FirebaseAuth.getInstance();
        editNameRegister = view.findViewById(R.id.signup_name);
        editEmailRegister = view.findViewById(R.id.signup_email);
        editUsernameRegister = view.findViewById(R.id.signup_username);
        editPasswordRegister = view.findViewById(R.id.signup_password);
        btnCreate = view.findViewById(R.id.signup_button);
        TextView LoginTextView = view.findViewById(R.id.switchtoLogin);

        // --- Set Click Listeners ---

        TextView LoginTextView = view.findViewById(R.id.switchtoLogin);

        // --- Set Click Listeners ---


        LoginTextView.setOnClickListener(v -> {
            // Navigate to RegisterFragment
            if (getActivity() instanceof WelcomePage) {
                ((WelcomePage) getActivity()).replaceFragment(new LoginFragment());
            }
        });


        btnCreate.setOnClickListener(v -> {
            name = editNameRegister.getText().toString().trim();
            email = editEmailRegister.getText().toString().trim();
            username = editUsernameRegister.getText().toString().trim();
            password = editPasswordRegister.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmailRegister.setError("Invalid email format");
            } else if (password.length() < 6) {
                editPasswordRegister.setError("Password must be at least 6 characters");
            } else {
                createAccount();
            }
        });
    }
    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserToDatabase();
                    } else {
                        Toast.makeText(getContext(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void saveUserToDatabase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("email", email);
        userData.put("Name", name);
        userData.put("username", username);
        userData.put("phone", "");
        userData.put("address", "");
        userData.put("timestamp", "" + System.currentTimeMillis());
        userData.put("roleId", 2); // Customer
        userData.put("online", "true");
        userData.put("avatar", "");

        ref.child(uid).setValue(userData)
                .addOnSuccessListener(unused -> {
                    sendVerificationEmail();
                    if (getContext() != null) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Registration successful")
                                .setMessage("A verification email has been sent. Please verify to log in.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    if (getActivity() instanceof WelcomePage) {
                                        ((WelcomePage) getActivity()).replaceFragment(new LoginFragment());
                                    }
                                })
                                .show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "Verification email sent", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    }

    }
}


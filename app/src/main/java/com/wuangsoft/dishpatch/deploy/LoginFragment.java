package com.wuangsoft.dishpatch.deploy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.databinding.ActivityLoginBinding;
import com.wuangsoft.dishpatch.ui.home.HomeFragment;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {


    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail, editTextPassword;
    private Button loginButton;
    private TextView signUpTextView, forgetPasswordTextView;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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


        return inflater.inflate(R.layout.fragment_login, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // View binding manually
        editTextEmail = view.findViewById(R.id.login_email);
        editTextPassword = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);
        signUpTextView = view.findViewById(R.id.switchtoSignUp);
        forgetPasswordTextView = view.findViewById(R.id.forgetbutton);

        // Navigate to RegisterFragment
        signUpTextView.setOnClickListener(v -> {
            if (getActivity() instanceof WelcomePage) {
                ((WelcomePage) getActivity()).replaceFragment(new RegisterFragment());
            }
        });

        // Navigate to ForgetPasswordFragment
        forgetPasswordTextView.setOnClickListener(v -> {
            if (getActivity() instanceof WelcomePage) {
                ((WelcomePage) getActivity()).replaceFragment(new ForgetPasswordFragment());
            }
        });

        // Handle login button
        loginButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showAlert("Login Failed", "Invalid email format");
                return;
            }

            if (password.isEmpty()) {
                editTextPassword.setError("Password cannot be empty");
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        if (!Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                            showAlert("Login Failed", "Please verify your email before logging in");
                        } else {
                            updateOnlineStatus();
                        }
                    })
                    .addOnFailureListener(e -> {
                        showAlert("Login Failed", "Error: " + e.getMessage());
                    });
        });
    }

    // Show alert dialog
    private void showAlert(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // Update online status in Firebase Realtime DB
    private void updateOnlineStatus() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).updateChildren(hashMap)
                .addOnSuccessListener(unused -> checkAccountType())
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Network Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Check user's account type
    private void checkAccountType() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String accountType = "" + ds.child("users").getValue();
                            Long roleId = ds.child("roleId").getValue(Long.class);
                            if (roleId != null) {
                                if (roleId == 1) {
                                    accountType = "Admin";
                                } else if (roleId == 2) {
                                    accountType = "Customer";

                                } else {
                                    accountType = "Guest";
                                }

                            Toast.makeText(getContext(), "Login success - " + accountType, Toast.LENGTH_SHORT).show();
                            if (getActivity() instanceof WelcomePage) {
                                ((WelcomePage) getActivity()).replaceFragment(new HomeDeployFragment());
                            }
                        } else {
                            Toast.makeText(getContext(), "Login failed - roleId not found", Toast.LENGTH_SHORT).show();
                        }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
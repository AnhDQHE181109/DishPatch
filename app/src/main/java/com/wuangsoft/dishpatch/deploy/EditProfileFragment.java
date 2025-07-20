package com.wuangsoft.dishpatch.deploy;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.User;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    private static final String USER_ID = "HCre5W0AT8VzE0i2BCrp42p0Bv43"; // Your Firebase user ID
    
    private EditText editName, editUsername, editPhone, editAddress;
    private ImageView profileAvatar;
    private ImageButton backButton;
    private Button saveButton, cancelButton;
    
    private DatabaseReference userRef;
    private User currentUser;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        
        initViews(view);
        setupFirebase();
        setupClickListeners();
        loadUserData();
        
        return view;
    }

    private void initViews(View view) {
        editName = view.findViewById(R.id.editName);
        editUsername = view.findViewById(R.id.editUsername);
        editPhone = view.findViewById(R.id.editPhone);
        editAddress = view.findViewById(R.id.editAddress);
        profileAvatar = view.findViewById(R.id.profileAvatar);
        backButton = view.findViewById(R.id.backButton);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);
    }

    private void setupFirebase() {
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(USER_ID);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> navigateBack());
        
        cancelButton.setOnClickListener(v -> navigateBack());

        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void navigateBack() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void saveProfile() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate inputs
        String name = editName.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Name is required");
            return;
        }

        if (username.isEmpty()) {
            editUsername.setError("Username is required");
            return;
        }

        // Update user object
        currentUser.setName(name);
        currentUser.setUsername(username);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        // Save to Firebase
        userRef.setValue(currentUser)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                navigateBack();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to update profile", e);
                Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), 
                             Toast.LENGTH_LONG).show();
            });
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        updateUI(currentUser);
                    } else {
                        Log.e(TAG, "User object is null");
                        showError("Failed to load user data");
                    }
                } else {
                    Log.e(TAG, "User data doesn't exist");
                    showError("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                showError("Failed to load user data: " + databaseError.getMessage());
            }
        });
    }

    private void updateUI(User user) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            // Update name
            String name = user.getName();
            if (name != null && !name.isEmpty()) {
                editName.setText(name);
            }

            // Update username
            String username = user.getUsername();
            if (username != null && !username.isEmpty()) {
                editUsername.setText(username);
            }

            // Update phone
            String phone = user.getPhone();
            if (phone != null && !phone.isEmpty()) {
                editPhone.setText(phone);
            }

            // Update address
            String address = user.getAddress();
            if (address != null && !address.isEmpty()) {
                editAddress.setText(address);
            }

            // TODO: Load avatar image if URL is available
            String avatarUrl = user.getAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                // You can implement image loading here using libraries like Glide or Picasso
                // For now, we'll keep the default image
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}

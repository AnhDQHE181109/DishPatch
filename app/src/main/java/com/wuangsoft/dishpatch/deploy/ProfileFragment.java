package com.wuangsoft.dishpatch.deploy;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.User;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    
    private TextView profileName, profileStatus, profileEmail, profileUsername, 
                     profilePhone, profileAddress, profileRole;
    private ImageView profileAvatar;
    private ImageButton backButton;
    private Button editProfileButton, logoutButton;
    
    private DatabaseReference userRef;
    private UserPreferences userPreferences;
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = new UserPreferences(getContext());
        currentUser = userPreferences.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initViews(view);
        setupFirebase();
        setupClickListeners();
        loadUserData();
        
        return view;
    }

    private void initViews(View view) {
        profileName = view.findViewById(R.id.profileName);
        profileStatus = view.findViewById(R.id.profileStatus);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileUsername = view.findViewById(R.id.profileUsername);
        profilePhone = view.findViewById(R.id.profilePhone);
        profileAddress = view.findViewById(R.id.profileAddress);
        profileRole = view.findViewById(R.id.profileRole);
        profileAvatar = view.findViewById(R.id.profileAvatar);
        backButton = view.findViewById(R.id.backButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
    }

    private void setupFirebase() {
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });        editProfileButton.setOnClickListener(v -> {
            // Navigate to EditProfileFragment
            EditProfileFragment editProfileFragment = EditProfileFragment.newInstance();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, editProfileFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        logoutButton.setOnClickListener(v -> {
            // Implement logout functionality
            FirebaseAuth.getInstance().signOut();
            userPreferences.logout();
            
            // Navigate back to welcome screen
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
            
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            // Use cached user data first
            updateUI(currentUser);
            
            // Also fetch latest data from Firebase
            if (userRef != null) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                currentUser = user;
                                userPreferences.saveUser(user); // Update cached data
                                updateUI(user);
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
        } else {
            showError("No user data found. Please login again.");
        }
    }

    private void updateUI(User user) {
        if (getActivity() == null) return;
        
        getActivity().runOnUiThread(() -> {
            // Update name
            String name = user.getName();
            if (name != null && !name.isEmpty()) {
                profileName.setText(name);
            } else {
                profileName.setText("No Name");
            }

            // Update online status
            String online = user.getOnline();
            if ("true".equals(online)) {
                profileStatus.setText("Online");
                profileStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                profileStatus.setText("Offline");
                profileStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            // Update email
            String email = user.getEmail();
            if (email != null && !email.isEmpty()) {
                profileEmail.setText(email);
            } else {
                profileEmail.setText("No email provided");
            }

            // Update username
            String username = user.getUsername();
            if (username != null && !username.isEmpty()) {
                profileUsername.setText(username);
            } else {
                profileUsername.setText("No username");
            }

            // Update phone
            String phone = user.getPhone();
            if (phone != null && !phone.isEmpty()) {
                profilePhone.setText(phone);
            } else {
                profilePhone.setText("Not provided");
            }

            // Update address
            String address = user.getAddress();
            if (address != null && !address.isEmpty()) {
                profileAddress.setText(address);
            } else {
                profileAddress.setText("Not provided");
            }

            // Update role
            int roleId = user.getRoleId();
            String roleText;
            switch (roleId) {
                case 1:
                    roleText = "Admin";
                    break;
                case 2:
                    roleText = "User";
                    break;
                default:
                    roleText = "Unknown";
                    break;
            }
            profileRole.setText(roleText);

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

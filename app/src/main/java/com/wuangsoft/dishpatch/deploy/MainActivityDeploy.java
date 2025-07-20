package com.wuangsoft.dishpatch.deploy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.TraceCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.databinding.ActivityMainBinding;
import com.wuangsoft.dishpatch.databinding.ActivityMainDeployBinding;
import com.wuangsoft.dishpatch.ui.ShoppingCartActivity;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

public class MainActivityDeploy extends AppCompatActivity {
    ActivityMainDeployBinding binding;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is logged in
        userPreferences = new UserPreferences(this);
        if (!userPreferences.isLoggedIn()) {
            // User not logged in, redirect to welcome
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }
        
        binding = ActivityMainDeployBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) { // Only replace fragment if not restoring from instance state
            replaceFragment(new HomeDeployFragment(), R.id.home_button); // Pass initial selected item
        }


        binding.BottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home_button) {
                replaceFragment(new HomeDeployFragment(), itemId);
                return true;
            } else if (itemId == R.id.explore_button) {
                replaceFragment(new ExploreDeployFragment(), itemId);
                return true;
            } else if (itemId == R.id.favourite_button) {
                replaceFragment(new FavouriteDeployFragment(), itemId);
                return true;
            } else if (itemId == R.id.order_button) {
                replaceFragment(new OrdersDeployFragment(), itemId);
                return true;
            } else {
                return false;
            }
        });

        // Handle back press to update BottomNavigationView
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 1) { // Check if there's something to pop
                    fm.popBackStackImmediate(); // Use popBackStackImmediate to ensure fragment is removed synchronously
                    // Update BottomNavigationView based on the new top fragment
                    updateBottomNavSelection();
                } else {
                    // If only one fragment is left (or no custom backstack handling needed here),
                    // let the default back press behavior finish the activity.
                    // You might need to call finish() if this is the root of your task
                    // and you don't want to rely on the default behavior only.
                    if (TraceCompat.isEnabled()) { // Check if callback is enabled
                        setEnabled(false); // Disable to avoid infinite loop if super.onBackPressed() calls this again
                        MainActivityDeploy.super.getOnBackPressedDispatcher().onBackPressed();
                        setEnabled(true); // Re-enable
                    }
                }
            }
        });
        // Call this once at the beginning to set the correct state if Activity is recreated
        updateBottomNavSelection();
    }

    private void replaceFragment(Fragment fragment, int menuItemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.setReorderingAllowed(true);
        // Give a name to the backstack entry, you can use the menuItemId as a simple unique name
        fragmentTransaction.addToBackStack(String.valueOf(menuItemId));
        fragmentTransaction.commit();
    }

    private void updateBottomNavSelection() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            String currentFragmentTag = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
            if (currentFragmentTag != null) {
                try {
                    int menuItemId = Integer.parseInt(currentFragmentTag);
                    // Important: Use setSelectedItemId to trigger the listener if needed,
                    // or use menu.findItem(menuItemId).setChecked(true) for silent update.
                    // Using menu.findItem().setChecked(true) is generally safer to avoid re-triggering fragment transactions.
                    binding.BottomNav.getMenu().findItem(menuItemId).setChecked(true);
                } catch (NumberFormatException e) {
                    // Handle case where tag is not a number (e.g. if you used a different naming scheme)
                }
            }
        } else {
            // Optional: If backstack is empty and you have a default selection
            // binding.BottomNav.setSelectedItemId(R.id.home_button);
        }
    }


    public void goToShoppingCart(View view) {
        Intent shoppingCartIntent = new Intent(this, ShoppingCartActivity.class);
        startActivity(shoppingCartIntent);
    }
}

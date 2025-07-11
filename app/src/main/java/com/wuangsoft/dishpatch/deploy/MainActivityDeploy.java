package com.wuangsoft.dishpatch.deploy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.databinding.ActivityMainBinding;
import com.wuangsoft.dishpatch.databinding.ActivityMainDeployBinding;
import com.wuangsoft.dishpatch.ui.ShoppingCartActivity;

public class MainActivityDeploy extends AppCompatActivity {
    ActivityMainDeployBinding binding;


@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainDeployBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeDeployFragment());

        binding.BottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_button) {
                replaceFragment(new HomeDeployFragment());
                return true;
            } else if (item.getItemId() == R.id.explore_button) {
                replaceFragment(new ExploreDeployFragment());
                return true;
            } else if (item.getItemId() == R.id.favourite_button) {
                replaceFragment(new FavouriteDeployFragment());
                return true;
            } else if (item.getItemId() == R.id.order_button) {
                replaceFragment(new OrdersDeployFragment());
                return true;
            } else {
                return false;
            }
        });
    }
    private void replaceFragment(Fragment fragment) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(R.id.frame_layout, fragment);
    fragmentTransaction.commit();
    }

    public void goToShoppingCart(View view) {
        Intent shoppingCartIntent = new Intent(this, ShoppingCartActivity.class);
        startActivity(shoppingCartIntent);
    }
}

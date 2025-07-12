package com.wuangsoft.dishpatch.deploy;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.databinding.ActivityMainDeployBinding;
import com.wuangsoft.dishpatch.databinding.WelcomePageBinding;

public class WelcomePage extends AppCompatActivity {
    WelcomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WelcomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) { // Only replace fragment if not restoring from instance state
            replaceFragment(new LoginFragment()); // Pass initial selected item
        }

    }
    public void navigateToLogin() {
        replaceFragment(new LoginFragment());
    }

    public void navigateToRegister() {
        replaceFragment(new RegisterFragment());
    }
    public void nagvigateToForgetPassword() {
        replaceFragment(new ForgetPasswordFragment());
    }
    public void finishActivity(View v){
        super.finish(); // back to welcome.xml
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.welcomeframe, fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}


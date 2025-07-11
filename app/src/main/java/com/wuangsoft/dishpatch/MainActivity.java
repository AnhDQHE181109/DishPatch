package com.wuangsoft.dishpatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

import com.wuangsoft.dishpatch.models.MenuCategory;
import com.wuangsoft.dishpatch.models.MenuItem;
import com.wuangsoft.dishpatch.models.OptionGroup;
import com.wuangsoft.dishpatch.models.Choice;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.wuangsoft.dishpatch.databinding.ActivityMainBinding;
import com.wuangsoft.dishpatch.ui.ShoppingCartActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Intent shoppingCartIntent = new Intent(this, ShoppingCartActivity.class);
        startActivity(shoppingCartIntent);
        Button myButton = findViewById(R.id.addbutton);
        if (myButton != null) {
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMenuCategoryToFirestore();
                }
            });
        }
    }
    private void addMenuCategoryToFirestore () {
        String restaurantId = "restaurant_id_123"; // This should be dynamic or fixed

        // Create some menu items using the Kotlin data classes
        MenuItem spicyWings = new MenuItem(
                "item_id_wings",
                "Spicy Chicken Wings",
                "Crispy wings tossed in our signature spicy sauce, served with blue cheese dip.",
                12.99,
                "https://example.com/images/wings.jpg",
                true,
                java.util.Arrays.asList("Gluten", "Dairy"),
                java.util.Arrays.asList(
                        new OptionGroup(
                                "wings_sauce",
                                "Choose Your Sauce",
                                "singleSelect",
                                true,
                                java.util.Arrays.asList(
                                        new Choice("sauce_mild", "Mild BBQ", 0.00),
                                        new Choice("sauce_hot", "Hot Buffalo", 0.00),
                                        new Choice("sauce_extreme", "Extreme Fire", 1.00)
                                )
                        ),
                        new OptionGroup(
                                "wings_side",
                                "Add a Side",
                                "multiSelect",
                                false,
                                java.util.Arrays.asList(
                                        new Choice("side_fries", "Fries", 2.50),
                                        new Choice("side_salad", "Side Salad", 3.00)
                                )
                        )
                )
        );

        MenuItem nachoFries = new MenuItem(
                "item_id_fries",
                "Loaded Nacho Fries",
                "Golden fries loaded with cheese, jalapeños, and your choice of protein.",
                9.99,
                "https://example.com/images/nachofries.jpg",
                true,
                java.util.Collections.singletonList("Dairy"),
                java.util.Collections.emptyList()
        );

        // Create the menu category data using the MenuCategory data class
        MenuCategory snacksCategory = new MenuCategory(
                "", // categoryId - Firestore can auto-generate this if you use .add()
                restaurantId,
                "Appetizers",
                "Start your meal right with our delicious appetizers!",
                1,    // order
                true, // isActive
                Arrays.asList(spicyWings, nachoFries) // items
        );

        // Add a new document to the "menuCategories" collection (or "menus" as in your example).
        // Using "menuCategories" for clarity.
        db.collection("menuCategories") // Changed from "menus" to "menuCategories" for clarity
                .add(snacksCategory) // Pass the data class object directly
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firebase", "MenuCategory added with ID: " + documentReference.getId());
                    // Optionally, update the categoryId in your local object or for further use
                    // String newCategoryId = documentReference.getId();
                    // db.collection("menuCategories").document(newCategoryId).update("categoryId", newCategoryId);

                })
                .addOnFailureListener(e -> {
                    Log.w("Firebase", "Error adding menu category", e);
                });
    }

}
package com.wuangsoft.dishpatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.CartItemFirebase;
import com.wuangsoft.dishpatch.models.MenuItem;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    
    private TextView productNameText, productPriceText, productDescriptionText, itemCountText;
    private ImageView productImage;
    private ImageButton backButton, favoriteButton, lessButton, moreButton, addToCartButton;
    
    private MenuItem menuItem;
    private int quantity = 1;
    private UserPreferences userPreferences;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productdetail);
        
        initViews();
        setupUserPreferences();
        getProductDataFromIntent();
        setupClickListeners();
        updateQuantityDisplay();
    }
    
    private void initViews() {
        productNameText = findViewById(R.id.prodctname);
        productPriceText = findViewById(R.id.price);
        productDescriptionText = findViewById(R.id.description);
        itemCountText = findViewById(R.id.itemcount);
        productImage = findViewById(R.id.product);
        
        backButton = findViewById(R.id.backButton2);
        favoriteButton = findViewById(R.id.imageButton);
        lessButton = findViewById(R.id.less);
        moreButton = findViewById(R.id.more);
        addToCartButton = findViewById(R.id.addtocart);
    }
    
    private void setupUserPreferences() {
        userPreferences = new UserPreferences(this);
        currentUserId = userPreferences.getUserId();
        
        if (currentUserId == null) {
            Toast.makeText(this, "Please login to add items to cart", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void getProductDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("menuItem")) {
            menuItem = (MenuItem) intent.getSerializableExtra("menuItem");
            if (menuItem != null) {
                displayProductInfo();
            } else {
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No product data received", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void displayProductInfo() {
        productNameText.setText(menuItem.getName());
        productPriceText.setText(String.format("%,d₫", menuItem.getPrice().longValue()).replace(',', '.'));
        productDescriptionText.setText(menuItem.getDescription() != null ? menuItem.getDescription() : "No description available");
        
        // Load product image
        if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(menuItem.getImageUrl())
                    .placeholder(R.drawable.home_icon)
                    .error(R.drawable.home_icon)
                    .into(productImage);
        } else {
            productImage.setImageResource(R.drawable.home_icon);
        }
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        lessButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityDisplay();
            }
        });
        
        moreButton.setOnClickListener(v -> {
            quantity++;
            updateQuantityDisplay();
        });
        
        addToCartButton.setOnClickListener(v -> addToCart());
        
        favoriteButton.setOnClickListener(v -> {
            // TODO: Implement favorite functionality
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        });
    }
    
    private void updateQuantityDisplay() {
        itemCountText.setText(String.valueOf(quantity));
        
        // Update less button drawable based on quantity
        if (quantity > 1) {
            // Set to active drawable when quantity > 1
            lessButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.lessactive));
        } else {
            // Set to default drawable when quantity = 1
            lessButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.less));
        }
    }
    
    private void addToCart() {
        if (menuItem == null || currentUserId == null) {
            Toast.makeText(this, "Unable to add to cart", Toast.LENGTH_SHORT).show();
            return;
        }
        
        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(currentUserId);
        
        // Check if item already exists in cart
        cartRef.orderByChild("dishId").equalTo(menuItem.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Item exists, update quantity
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CartItemFirebase existingItem = snapshot.getValue(CartItemFirebase.class);
                                if (existingItem != null) {
                                    long newQuantity = existingItem.getQuantity() + quantity;
                                    snapshot.getRef().child("quantity").setValue(newQuantity)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(ProductDetailActivity.this, 
                                                    quantity + " more item(s) added to cart", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Failed to update cart item quantity", e);
                                                Toast.makeText(ProductDetailActivity.this, 
                                                    "Failed to update cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                    break; // Only update the first match
                                }
                            }
                        } else {
                            // Item doesn't exist, create new record
                            CartItemFirebase cartItem = new CartItemFirebase();
                            cartItem.setDishId(menuItem.getId());
                            cartItem.setName(menuItem.getName());
                            cartItem.setImageUrl(menuItem.getImageUrl());
                            cartItem.setPricePerItem(menuItem.getPrice().longValue());
                            cartItem.setQuantity((long) quantity);
                            
                            // Generate unique cart item ID
                            String cartItemId = FirebaseDatabase.getInstance().getReference().push().getKey();
                            
                            cartRef.child(cartItemId).setValue(cartItem)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ProductDetailActivity.this, 
                                            quantity + " item(s) added to cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to add item to cart", e);
                                        Toast.makeText(ProductDetailActivity.this, 
                                            "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Database error: " + databaseError.getMessage());
                        Toast.makeText(ProductDetailActivity.this, 
                            "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

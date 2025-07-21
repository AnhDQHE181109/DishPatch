package com.wuangsoft.dishpatch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.controllers.CheckoutListAdapter;
import com.wuangsoft.dishpatch.models.CartItem;
import com.wuangsoft.dishpatch.utilities.DatabaseOperations;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

import java.util.List;

public class PaymentConfirmFragment extends AppCompatActivity {

    private UserPreferences userPreferences;
    private String currentUserId;
    private TextView deliveryAddress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_payment_confirm);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy dữ liệu giỏ hàng được chọn
        List<CartItem> selectedCartItems = (List<CartItem>) getIntent().getSerializableExtra("selectedCartItems");
        RecyclerView recView = findViewById(R.id.confirm_items_rec_view);
        CheckoutListAdapter adapter = new CheckoutListAdapter(selectedCartItems);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);

        // Khởi tạo thông tin người dùng
        userPreferences = new UserPreferences(this);
        currentUserId = userPreferences.getUserId();

        // Gán địa chỉ người dùng
        deliveryAddress = findViewById(R.id.deliveryAddress);
        loadUserAddress(); // 🔁 Lấy địa chỉ từ Firebase

        // Quay lại
        ImageButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);
            finish();
        });

        // Đặt hàng
        Button payNowButton = findViewById(R.id.btnPayNow);
        payNowButton.setOnClickListener(v -> {
            if (selectedCartItems != null) {
                DatabaseOperations dbOps = new DatabaseOperations(currentUserId, DatabaseOperations.ORDERS);
                dbOps.createOrder(selectedCartItems);

                dbOps = new DatabaseOperations(currentUserId, DatabaseOperations.CART);
                dbOps.removeCartItems(selectedCartItems);

                Intent orderComplete = new Intent(PaymentConfirmFragment.this, OrderCompleteActivity.class);
                startActivity(orderComplete);
                finish();
            }
        });
    }


    private void loadUserAddress() {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUserId)
                .child("address");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String address = snapshot.getValue(String.class);
                    deliveryAddress.setText(address);
                } else {
                    deliveryAddress.setText("No address found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentConfirmFragment.this, "Failed to load address.", Toast.LENGTH_SHORT).show();
            }


        });
    }
}

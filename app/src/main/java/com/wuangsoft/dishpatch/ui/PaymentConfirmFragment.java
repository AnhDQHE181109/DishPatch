package com.wuangsoft.dishpatch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.controllers.CheckoutListAdapter;
import com.wuangsoft.dishpatch.models.CartItem;
import com.wuangsoft.dishpatch.utilities.DatabaseOperations;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

import java.util.List;

public class PaymentConfirmFragment extends AppCompatActivity {

    private UserPreferences userPreferences;
    private String currentUserId;

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

        List<CartItem> selectedCartItems = (List<CartItem>)getIntent().getSerializableExtra("selectedCartItems");
        RecyclerView recView = findViewById(R.id.confirm_items_rec_view);
        CheckoutListAdapter adapter = new CheckoutListAdapter(selectedCartItems);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(adapter);

        userPreferences = new UserPreferences(this);
        currentUserId = userPreferences.getUserId();

        ImageButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);
            finish();
        });

        Button payNowButton = findViewById(R.id.btnPayNow);
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCartItems != null) {
                    DatabaseOperations dbOps = new DatabaseOperations(currentUserId, DatabaseOperations.ORDERS);

                    dbOps.createOrder(selectedCartItems);
                    dbOps = new DatabaseOperations(currentUserId, DatabaseOperations.CART);
                    dbOps.removeCartItems(selectedCartItems);

                    Intent orderComplete = new Intent(PaymentConfirmFragment.this, OrderCompleteActivity.class);
                    startActivity(orderComplete);
                    finish();
                }
            }
        });
    }
}

package com.wuangsoft.dishpatch.ui;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.controllers.CheckoutListAdapter;
import com.wuangsoft.dishpatch.models.CartItem;

import java.util.List;

public class PaymentConfirmFragment extends AppCompatActivity {

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
    }
}

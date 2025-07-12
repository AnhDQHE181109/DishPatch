package com.wuangsoft.dishpatch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.controllers.CartItemAdapter;
import com.wuangsoft.dishpatch.models.CartItem;
import com.wuangsoft.dishpatch.models.CartItemFirebase;
import com.wuangsoft.dishpatch.utilities.DatabaseOperations;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final String TAG = ShoppingCartActivity.class.getSimpleName();
    private Toolbar shoppingCartToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        shoppingCartToolbar = findViewById(R.id.shoppingCartToolbar);
        setSupportActionBar(shoppingCartToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        shoppingCartToolbar.setNavigationIcon(R.drawable.back_arrow_large);
        getSupportActionBar().setTitle(R.string.cart_title);
        shoppingCartToolbar.setNavigationOnClickListener(v -> onBackPressed());

        DatabaseOperations dbOps = new DatabaseOperations("user01");

        RecyclerView recView = findViewById(R.id.cartItemsRecView);
        recView.setLayoutManager(new LinearLayoutManager(this));

        dbOps.getCartItems(new DatabaseOperations.CartItemCallback() {
            @Override
            public void onCallback(List<CartItem> cartItemsCallback) {
                findViewById(R.id.dataWaitProgressBar).setVisibility(View.INVISIBLE);
                CartItemAdapter adapter = new CartItemAdapter(cartItemsCallback);
                recView.setAdapter(adapter);
                Log.i(TAG, "cartItems: " + cartItemsCallback.toString());
            }
        });


//        List<CartItemFirebase> cartItemsFetched = dbOps.getCartItems();
//        Log.i("Firebase test ", "Cart data fetched from Firebase: " + dbOps.getCartItems("user01");
//        dbOps.getCartItemsOnce("user01");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_cart_toolbar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit_list) {

        }

        return super.onOptionsItemSelected(item);
    }

    public List<CartItem> sampleCartItems() {
        List<CartItem> cartItems = new ArrayList<>();

        cartItems.add(new CartItem(R.drawable.account_icon, "Pizza", "$10", "1"));
        cartItems.add(new CartItem(R.drawable.background, "AS", "$20", "3"));
        cartItems.add(new CartItem(R.drawable.back_arrow, "DF", "$50", "1"));
        cartItems.add(new CartItem(R.drawable.ic_home_black_24dp, "GG", "$60", "4"));

        cartItems.add(new CartItem(R.drawable.ic_home_black_24dp, "GG", "$60", "4"));
        cartItems.add(new CartItem(R.drawable.ic_home_black_24dp, "GG", "$60", "4"));
        cartItems.add(new CartItem(R.drawable.ic_home_black_24dp, "GG", "$60", "4"));
        cartItems.add(new CartItem(R.drawable.ic_home_black_24dp, "GG", "$60", "4"));
        cartItems.add(new CartItem(R.drawable.ic_home_black_24dp, "GG", "$60", "4"));
        cartItems.add(new CartItem(R.drawable.ic_home_black_24dp, "GG", "$60", "4"));
        cartItems.add(new CartItem(R.drawable.ic_home_black_24dp, "GG", "$60", "4"));

        return cartItems;
    }
}
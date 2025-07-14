package com.wuangsoft.dishpatch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.wuangsoft.dishpatch.utilities.DatabaseOperations;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final String TAG = ShoppingCartActivity.class.getSimpleName();
    private Toolbar shoppingCartToolbar;
    private List<CartItem> selectedCartItems = new ArrayList<>();

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
        ((TextView)findViewById(R.id.subtotalPriceText)).setText("0₫");

        DatabaseOperations dbOps = new DatabaseOperations("user01");

        RecyclerView recView = findViewById(R.id.cartItemsRecView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        CartItemAdapter adapter = new CartItemAdapter(new ArrayList<>());
        recView.setAdapter(adapter);

        dbOps.getCartItems(new DatabaseOperations.CartItemCallback() {
            @Override
            public void onCallbackGetCartItems(List<CartItem> cartItemsCallback) {
                findViewById(R.id.dataWaitProgressBar).setVisibility(View.INVISIBLE);

                adapter.setCartItems(cartItemsCallback);
                adapter.notifyDataSetChanged();

                adapter.setCallback(new CartItemAdapter.Callback() {
                    @Override
                    public void onCheckedChanged(CartItem cartItem, boolean isChecked) {
                        if (isChecked) {
                            selectedCartItems.add(cartItem);
                        } else {
                            selectedCartItems.remove(cartItem);
                        }

                        calculateSubtotal();
//                        Log.i(TAG, "Cart item checked: " + cartItem.getProductName() + " " + isChecked);
                    }

//                    @Override
//                    public void onClick(CartItem cartItem) {
//                        if (selectedCartItems.contains(cartItem)) {
//                            selectedCartItems.remove(cartItem);
//                        } else {
//                            selectedCartItems.add(cartItem);
//                        }
//                    }

                    @Override
                    public void onDecrementClick(CartItem cartItem) {
                        long currentQuantity = Long.parseLong(cartItem.getProductQuantity());
                        if (currentQuantity > 1) {
                            cartItem.setProductQuantity(String.valueOf(Long.parseLong(cartItem.getProductQuantity()) - 1));
                            dbOps.updateCartItemQuantity(cartItem, currentQuantity - 1);
                            adapter.notifyDataSetChanged();
                            calculateSubtotal();
//                            selectedCartItems.clear();
                        }

//                        Log.i(TAG, "Cart item quantity decremented: " + (Long.parseLong(cartItem.getProductQuantity()) - 1));
                    }

                    @Override
                    public void onIncrementClick(CartItem cartItem) {
                        long currentQuantity = Long.parseLong(cartItem.getProductQuantity());
                        if (currentQuantity < 40) {
                            cartItem.setProductQuantity(String.valueOf(Long.parseLong(cartItem.getProductQuantity()) + 1));
                            dbOps.updateCartItemQuantity(cartItem, currentQuantity + 1);
                            adapter.notifyDataSetChanged();
                            calculateSubtotal();
//                            selectedCartItems.clear();
                        }

//                        Log.i(TAG, "Cart item quantity incremented: " + (Long.parseLong(cartItem.getProductQuantity()) + 1));
                    }
                });

                String cartTitle = getResources().getString(R.string.cart_title);
                getSupportActionBar().setTitle(cartTitle + " (" + cartItemsCallback.size() + ")");
                Log.i(TAG, "cartItems: " + cartItemsCallback.toString());
            }
        });

        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for debugging selected cart items
                String selectedItemsResult = "";
                for (CartItem cartItem : selectedCartItems) {
                    selectedItemsResult += cartItem.getProductName() + "\n";
                }
                Toast.makeText(ShoppingCartActivity.this, selectedItemsResult, Toast.LENGTH_SHORT).show();
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

    public void calculateSubtotal() {
        if (selectedCartItems.isEmpty()) {
            ((TextView)findViewById(R.id.subtotalPriceText)).setText("0₫");
        } else {
            long subtotalPrice = 0;
            for (CartItem selectedCartItem : selectedCartItems) {
                subtotalPrice += Long.parseLong(selectedCartItem.getProductPrice()) *
                        Long.parseLong(selectedCartItem.getProductQuantity());
//                Log.i(TAG, "product quantity: " + selectedCartItem.getProductQuantity());
            }
            ((TextView)findViewById(R.id.subtotalPriceText))
                    .setText(String.format("%,d", subtotalPrice).replace(',','.') + "₫");
        }
    }
}
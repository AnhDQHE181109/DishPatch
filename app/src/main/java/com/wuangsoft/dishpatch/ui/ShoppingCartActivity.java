package com.wuangsoft.dishpatch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.wuangsoft.dishpatch.models.CartItemFirebase;
import com.wuangsoft.dishpatch.utilities.DatabaseOperations;
import com.wuangsoft.dishpatch.utilities.SampleDataGenerator;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final String TAG = ShoppingCartActivity.class.getSimpleName();
    private Toolbar shoppingCartToolbar;
    private List<CartItem> selectedCartItems = new ArrayList<>();
    private List<CartItem> fetchedCartItems = new ArrayList<>();
    private boolean editMode = true;
    private MenuItem editModeToggleButton;
    
    private UserPreferences userPreferences;
    private String currentUserId;

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

        // Initialize UserPreferences
        userPreferences = new UserPreferences(this);
        currentUserId = userPreferences.getUserId();
        
        if (currentUserId == null) {
            Toast.makeText(this, "Please login to view cart", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        shoppingCartToolbar.setNavigationIcon(R.drawable.back_arrow_large);
        getSupportActionBar().setTitle(R.string.cart_title);
        shoppingCartToolbar.setNavigationOnClickListener(v -> onBackPressed());
        ((TextView)findViewById(R.id.subtotalPriceText)).setText("0₫");

        CheckBox selectAllCheckOut = findViewById(R.id.selectAllCheckBox);
        findViewById(R.id.deleteItemsButton).setVisibility(View.INVISIBLE);

        findViewById(R.id.cartEmptyMessage).setVisibility(View.INVISIBLE);

        DatabaseOperations dbOps = new DatabaseOperations(currentUserId);

        RecyclerView recView = findViewById(R.id.cartItemsRecView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        CartItemAdapter adapter = new CartItemAdapter(new ArrayList<>());
        recView.setAdapter(adapter);

//        pushSampleData();

        dbOps.getCartItems(new DatabaseOperations.CartItemCallback() {
            @Override
            public void onCallbackGetCartItems(List<CartItem> cartItemsCallback) {
                findViewById(R.id.dataWaitProgressBar).setVisibility(View.INVISIBLE);

                if (cartItemsCallback.isEmpty()) {
                    showCartEmptyMessage(true);
                    return;
                }

                adapter.setCartItems(cartItemsCallback);
                adapter.notifyDataSetChanged();
                fetchedCartItems = cartItemsCallback;

                adapter.setCallback(new CartItemAdapter.Callback() {
                    @Override
                    public void onCheckedChanged(CartItem cartItem, boolean isChecked) {
                        if (isChecked) {
                            selectedCartItems.add(cartItem);
                            Log.i(TAG, "ShoppingCartActivity" + "selectedCartItems added an item!");
                        } else {
                            selectedCartItems.remove(cartItem);
                            Log.i(TAG, "ShoppingCartActivity" + "selectedCartItems removed an item!");
                        }

                        if (selectedCartItems.size() == cartItemsCallback.size()) {
                            selectAllCheckOut.setChecked(true);
                        } else {
                            selectAllCheckOut.setChecked(false);
                        }

                        calculateSubtotal();
                        Log.i(TAG, "Cart item checked: " + cartItem.getProductName() + ": " + isChecked);
//                        Log.i(TAG, "selectedCartItems: " + selectedCartItems.toString());
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
                        } else if (currentQuantity == 1) {
                            // Show confirmation dialog for removing item
                            showRemoveItemConfirmationDialog(cartItem, dbOps, adapter);
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

                selectAllCheckOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCartItems.clear();

                        if (selectAllCheckOut.isChecked()) {
                            if (cartItemsCallback.isEmpty()) {
                                selectAllCheckOut.setChecked(false);
                                return;
                            }
                            for (CartItem cartItem : cartItemsCallback) {
                                if (!selectedCartItems.contains(cartItem)) {
                                    selectedCartItems.add(cartItem);
                                }
                            }
                            adapter.selectAll();
                        } else {
                            adapter.deSelectAll();
                        }

                        calculateSubtotal();
                    }
                });

                setCartTitle(cartItemsCallback.size());
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
                Log.i(TAG, "selectedItemsResult: " + selectedItemsResult);

                Intent orderConfirmScreen = new Intent(ShoppingCartActivity.this, PaymentConfirmFragment.class);
                orderConfirmScreen.putExtra("selectedCartItems", (Serializable) selectedCartItems);
//                orderConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(orderConfirmScreen);
            }
        });

        Button deleteButton = findViewById(R.id.deleteItemsButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCartItems.isEmpty()) {
                    Toast.makeText(ShoppingCartActivity.this,
                            getResources().getString(R.string.askToSelectToDelete), Toast.LENGTH_LONG).show();
                } else {
                    dbOps.removeCartItems(selectedCartItems);
                    adapter.getCartItems().removeAll(selectedCartItems);
                    adapter.notifyDataSetChanged();
                    adapter.deSelectAll();

                    selectedCartItems.clear();
                    calculateSubtotal();
                    selectAllCheckOut.setChecked(false);

                    disableEditMode();
                    editModeToggleButton.setIcon(R.drawable.outline_edit_24);
                    setCartTitle(fetchedCartItems.size());
                    if (fetchedCartItems.isEmpty()) showCartEmptyMessage(true);
                }
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
            editModeToggleButton = item;

            if (editMode) {
                Log.i(TAG, "onOptionsItemSelected: editMode: true");

                item.setIcon(R.drawable.outline_check_24);
                enableEditMode();

                startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });
            } else {
                Log.i(TAG, "onOptionsItemSelected: editMode: false");

                item.setIcon(R.drawable.outline_edit_24);
                disableEditMode();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    void enableEditMode() {
        editMode = false;
        findViewById(R.id.deleteItemsButton).setVisibility(View.VISIBLE);
        findViewById(R.id.subtotalPriceText).setVisibility(View.INVISIBLE);
        findViewById(R.id.checkoutButton).setVisibility(View.INVISIBLE);
    }

    void disableEditMode() {
        editMode = true;
        findViewById(R.id.deleteItemsButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.subtotalPriceText).setVisibility(View.VISIBLE);
        findViewById(R.id.checkoutButton).setVisibility(View.VISIBLE);
    }

    void setCartTitle(int cartSize) {
        String cartTitle = getResources().getString(R.string.cart_title);
        getSupportActionBar().setTitle(cartTitle + " (" + cartSize + ")");
    }

    void showCartEmptyMessage(boolean isShown) {
        TextView cartEmptyMessage = findViewById(R.id.cartEmptyMessage);

        if (isShown) {
            cartEmptyMessage.setText(getResources().getString(R.string.cartEmtpty));
            cartEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            cartEmptyMessage.setVisibility(View.INVISIBLE);
        }

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

    public void pushSampleData() {
        DatabaseOperations dbOps = new DatabaseOperations("user01");
        SampleDataGenerator sag = new SampleDataGenerator();
        List<CartItemFirebase> firebaseCartItems = sag.generateFirebaseSampleData();

        int itemCount = 1;
        for (CartItemFirebase firebaseCartItem : firebaseCartItems) {
            dbOps.addCartItemWithID("cartItem00" + itemCount++, firebaseCartItem);
        }
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
    
    private void showRemoveItemConfirmationDialog(CartItem cartItem, DatabaseOperations dbOps, CartItemAdapter adapter) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Remove Item")
                .setMessage("Do you want to remove \"" + cartItem.getProductName() + "\" from your cart?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    // Remove the item from cart using existing method
                    List<CartItem> itemsToRemove = new ArrayList<>();
                    itemsToRemove.add(cartItem);
                    dbOps.removeCartItems(itemsToRemove);
                    
                    // Update adapter
                    adapter.getCartItems().remove(cartItem);
                    adapter.notifyDataSetChanged();
                    
                    // Update selected items if this item was selected
                    selectedCartItems.remove(cartItem);
                    fetchedCartItems.remove(cartItem);
                    
                    // Update "Select All" checkbox state
                    CheckBox selectAllCheckBox = findViewById(R.id.selectAllCheckBox);
                    if (selectAllCheckBox.isChecked() && !fetchedCartItems.isEmpty()) {
                        selectAllCheckBox.setChecked(false);
                    }
                    
                    // Update UI
                    calculateSubtotal();
                    setCartTitle(fetchedCartItems.size());
                    
                    // Show empty message if cart is now empty
                    if (fetchedCartItems.isEmpty()) {
                        showCartEmptyMessage(true);
                    }
                    
                    Toast.makeText(ShoppingCartActivity.this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Do nothing, just dismiss the dialog
                    dialog.dismiss();
                })
                .show();
    }
}
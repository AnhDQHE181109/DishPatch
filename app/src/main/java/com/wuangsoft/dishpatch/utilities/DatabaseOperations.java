package com.wuangsoft.dishpatch.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.models.CartItem;
import com.wuangsoft.dishpatch.models.CartItemFirebase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOperations {

    private DatabaseReference firebaseDatabase;
    private static final String TAG = DatabaseOperations.class.getSimpleName();

    public DatabaseOperations(String userID) {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("carts").child(userID);
    }

    public void getCartItemsOnce(String userID) {
        firebaseDatabase.child("carts").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.i("firebase", String.valueOf(task.getResult().getValue()));

                }
            }
        });
    }

    public void getCartItems(CartItemCallback callback) {
        List<CartItemFirebase> cartItemsFetched = new ArrayList<>();
        List<CartItem> cartItems = new ArrayList<>();

        ValueEventListener cartItemsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.i("DBOps: getCartItems", "onDataChange invoked");
                cartItemsFetched.clear();
                cartItems.clear();

                for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                    CartItemFirebase cartItemFetched = cartItemSnapshot.getValue(CartItemFirebase.class);
//                    Log.i(TAG, cartItemFetched.toString());
                    cartItemsFetched.add(cartItemFetched);
                }
//                Log.i(TAG, "cartItemsFetched" + cartItemsFetched.toString());

                for (CartItemFirebase cartItemFetched : cartItemsFetched) {
                    CartItem cartItem = new CartItem(cartItemFetched.getDishId(), cartItemFetched.getImageUrl(), cartItemFetched.getName(), cartItemFetched.getPricePerItem().toString(), cartItemFetched.getQuantity().toString());
//                    Log.i(TAG, "cartItemFetched getPricePerItem & getQuantity: " + cartItemFetched.getPricePerItem().toString() + " "
//                            + cartItemFetched.getQuantity().toString());
                    cartItems.add(cartItem);
                }
//                Log.i(TAG, cartItems.toString());

                callback.onCallbackGetCartItems(cartItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "getCartItems:onCancelled", error.toException());
            }
        };
//        firebaseDatabase.addValueEventListener(cartItemsListener);
        firebaseDatabase.addListenerForSingleValueEvent(cartItemsListener);
    }

    public void updateCartItemQuantity(CartItem cartItem, Long newQuantity) {
        List<String> cartItemsList = new ArrayList<>();

//        firebaseDatabase.child(cartItem.getProductID()).child("quantity").setValue(newQuantity);
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.i("DBOps: updateCartItemQuantity", "onDataChange invoked");
                for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {
                    //debug fetching cartItems' keys
//                    cartItemsList.add(cartItemSnapshot.getKey());

                    CartItemFirebase cartItemFetched = cartItemSnapshot.getValue(CartItemFirebase.class);
                    if (cartItemFetched.getDishId().equalsIgnoreCase(cartItem.getProductID())) {
                        firebaseDatabase.child(cartItemSnapshot.getKey()).child("quantity").setValue(newQuantity);
                    }
//                    Log.i(TAG, "updateCartItemQuantity:onDataChange: \n" +
//                            "dishId: " + cartItemFetched.getDishId() + "\n" +
//                            "cartItem.getProductName(): " + cartItem.getProductName());

                }
//                Log.i(TAG, "cartItemsList: " + cartItemsList.toString());
//                callback.onCallbackUpdateItemQuantity(cartItemsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "updateCartItemQuantity:onCancelled", error.toException());
            }
        });
    }

    public interface CartItemCallback {
        void onCallbackGetCartItems(List<CartItem> cartItemsCallback);
//        void onCallbackUpdateItemQuantity(List<String> cartItemsList);
    }

}

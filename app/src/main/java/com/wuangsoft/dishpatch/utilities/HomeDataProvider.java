package com.wuangsoft.dishpatch.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.models.Category;
import com.wuangsoft.dishpatch.models.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeDataProvider {
    private final DatabaseReference dbRef;

    public HomeDataProvider() {
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    public void getCategories(final HomeDataCallback.CategoryCallback callback) {
        dbRef.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Category category = child.getValue(Category.class);
                    if (category != null) categories.add(category);
                }
                callback.onCategoriesLoaded(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeDataProvider", "Failed to load categories", error.toException());
            }
        });
    }

    public void getMenuItems(final HomeDataCallback.MenuItemCallback callback) {
        dbRef.child("menu_items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot menuSnapshot) {
                List<MenuItem> items = new ArrayList<>();
                for (DataSnapshot child : menuSnapshot.getChildren()) {
                    MenuItem item = child.getValue(MenuItem.class);
                    if (item != null) {
                        if (item.getId() == null || item.getId().isEmpty()) {
                            item.setId(child.getKey());
                        }
                        items.add(item);
                    }
                }

                // Step 1: Get ratings
                dbRef.child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot reviewSnapshot) {
                        for (MenuItem item : items) {
                            double total = 0;
                            int count = 0;
                            for (DataSnapshot review : reviewSnapshot.getChildren()) {
                                String dishId = review.child("dishId").getValue(String.class);
                                if (dishId != null && dishId.equals(item.getId())) {
                                    Double rating = review.child("rating").getValue(Double.class);
                                    if (rating != null) {
                                        total += rating;
                                        count++;
                                    }
                                }
                            }
                            double avg = count > 0 ? total / count : 0;
                            item.setRating(avg);
                        }

                        // Step 2: Get orders for best sellers
                        dbRef.child("Orders").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot orderSnapshot) {
                                // Count dishId appearances
                                HashMap<String, Integer> orderCountMap = new HashMap<>();
                                for (DataSnapshot order : orderSnapshot.getChildren()) {
                                    for (DataSnapshot itemSnap : order.getChildren()) {
                                        String dishId = itemSnap.child("dishId").getValue(String.class);
                                        if (dishId != null) {
                                            int prev = orderCountMap.getOrDefault(dishId, 0);
                                            orderCountMap.put(dishId, prev + 1);
                                        }
                                    }
                                }

                                // Attach count to MenuItem
                                for (MenuItem item : items) {
                                    item.setOrderCount(orderCountMap.getOrDefault(item.getId(), 0));
                                }

                                callback.onMenuItemsLoaded(items);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("HomeDataProvider", "Failed to load orders", error.toException());
                                callback.onMenuItemsLoaded(items);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeDataProvider", "Failed to load reviews", error.toException());
                        callback.onMenuItemsLoaded(items);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeDataProvider", "Failed to load menu items", error.toException());
            }
        });
    }

}

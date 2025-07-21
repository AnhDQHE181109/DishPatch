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
import java.util.List;

public class HomeDataProvider {
    private final DatabaseReference dbRef;
    private final DatabaseReference menuItemsRef;
    private final DatabaseReference ratingsRef;

    public HomeDataProvider() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        menuItemsRef = dbRef.child("menu_items");
        ratingsRef = dbRef.child("reviews");
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
        menuItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                // Now fetch reviews and compute average ratings
                ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                        callback.onMenuItemsLoaded(items);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeDataProvider", "Failed to load reviews", error.toException());
                        callback.onMenuItemsLoaded(items);  // fallback with items without ratings
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeDataProvider", "Failed to load menu items", error.toException());
            }
        });
    }
    
    public void getMenuItemsByCategory(String categoryId, final HomeDataCallback.MenuItemCallback callback) {
        menuItemsRef.orderByChild("categoryId").equalTo(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MenuItem> items = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    MenuItem item = child.getValue(MenuItem.class);
                    if (item != null) {
                        item.setId(child.getKey());
                        items.add(item);
                    }
                }
                
                // Get ratings for each item
                ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (MenuItem item : items) {
                            double totalRating = 0;
                            int ratingCount = 0;
                            
                            if (snapshot.child(item.getId()).exists()) {
                                for (DataSnapshot ratingSnapshot : snapshot.child(item.getId()).getChildren()) {
                                    Double rating = ratingSnapshot.getValue(Double.class);
                                    if (rating != null) {
                                        totalRating += rating;
                                        ratingCount++;
                                    }
                                }
                                if (ratingCount > 0) {
                                    item.setRating(totalRating / ratingCount);
                                }
                            }
                        }
                        callback.onMenuItemsLoaded(items);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("HomeDataProvider", "Failed to load ratings for category items", error.toException());
                        callback.onMenuItemsLoaded(items);  // fallback with items without ratings
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeDataProvider", "Failed to load menu items by category", error.toException());
            }
        });
    }
}

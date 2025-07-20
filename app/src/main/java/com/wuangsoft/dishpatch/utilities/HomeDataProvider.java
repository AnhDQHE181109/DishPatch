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
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MenuItem> items = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    MenuItem item = child.getValue(MenuItem.class);
                    if (item != null) {
                        // Set the ID from the key if not already set
                        if (item.getId() == null || item.getId().isEmpty()) {
                            item.setId(child.getKey());
                        }
                        items.add(item);
                    }
                }
                callback.onMenuItemsLoaded(items);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeDataProvider", "Failed to load menu items", error.toException());
            }
        });
    }
}

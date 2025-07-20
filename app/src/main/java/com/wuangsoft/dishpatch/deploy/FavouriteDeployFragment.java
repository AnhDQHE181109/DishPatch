package com.wuangsoft.dishpatch.deploy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.controllers.FavoriteAdapter;
import com.wuangsoft.dishpatch.models.FavoriteItem;
import com.wuangsoft.dishpatch.models.MenuItem;
import com.wuangsoft.dishpatch.ui.ProductDetailActivity;
import com.wuangsoft.dishpatch.utilities.UserPreferences;

import java.util.ArrayList;
import java.util.List;

public class FavouriteDeployFragment extends Fragment implements FavoriteAdapter.OnFavoriteClickListener {

    private static final String TAG = "FavouriteDeployFragment";
    
    private RecyclerView favoritesRecyclerView;
    private FavoriteAdapter favoriteAdapter;
    private List<FavoriteItem> favoriteItems;
    private UserPreferences userPreferences;
    private String currentUserId;
    private TextView emptyStateText;
    
    public FavouriteDeployFragment() {
        // Required empty public constructor
    }

    public static FavouriteDeployFragment newInstance() {
        return new FavouriteDeployFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPreferences = new UserPreferences(getContext());
        currentUserId = userPreferences.getUserId();
        favoriteItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favourite_deploy, container, false);
        
        initViews(root);
        setupRecyclerView();
        setupBackButton(root);
        loadFavorites();
        
        return root;
    }
    
    private void initViews(View root) {
        favoritesRecyclerView = root.findViewById(R.id.favoritesRecyclerView);
        emptyStateText = root.findViewById(R.id.emptyStateText);
    }
    
    private void setupRecyclerView() {
        favoriteAdapter = new FavoriteAdapter(favoriteItems);
        favoriteAdapter.setOnFavoriteClickListener(this);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        favoritesRecyclerView.setAdapter(favoriteAdapter);
    }
    
    private void setupBackButton(View root) {
        ImageView backButton = root.findViewById(R.id.imageView7);
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }
    
    private void loadFavorites() {
        if (currentUserId == null) {
            showEmptyState(true);
            return;
        }
        
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance()
                .getReference("favorites")
                .child(currentUserId);
        
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteItems.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FavoriteItem favoriteItem = snapshot.getValue(FavoriteItem.class);
                    if (favoriteItem != null) {
                        favoriteItems.add(favoriteItem);
                    }
                }
                
                favoriteAdapter.updateFavorites(favoriteItems);
                showEmptyState(favoriteItems.isEmpty());
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load favorites", databaseError.toException());
                Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            favoritesRecyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No favorite items yet.\nAdd items to favorites to see them here.");
        } else {
            favoritesRecyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRemoveFavorite(FavoriteItem favoriteItem) {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Remove Favorite")
                .setMessage("Do you want to remove \"" + favoriteItem.getName() + "\" from your favorites?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeFavoriteFromDatabase(favoriteItem);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
    
    private void removeFavoriteFromDatabase(FavoriteItem favoriteItem) {
        if (currentUserId == null) return;
        
        DatabaseReference favRef = FirebaseDatabase.getInstance()
                .getReference("favorites")
                .child(currentUserId)
                .child(favoriteItem.getDishId());
        
        favRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to remove favorite", e);
                    Toast.makeText(getContext(), "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onItemClick(FavoriteItem favoriteItem) {
        // Convert FavoriteItem to MenuItem and navigate to ProductDetailActivity
        MenuItem menuItem = new MenuItem();
        menuItem.setId(favoriteItem.getDishId());
        menuItem.setName(favoriteItem.getName());
        menuItem.setDescription(favoriteItem.getDescription());
        menuItem.setImageUrl(favoriteItem.getImageUrl());
        menuItem.setPrice(favoriteItem.getPrice());
        
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra("menuItem", menuItem);
        startActivity(intent);
    }
}
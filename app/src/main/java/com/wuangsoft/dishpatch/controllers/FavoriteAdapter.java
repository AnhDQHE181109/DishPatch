package com.wuangsoft.dishpatch.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.FavoriteItem;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<FavoriteItem> favoriteItems;
    private OnFavoriteClickListener onFavoriteClickListener;

    public interface OnFavoriteClickListener {
        void onRemoveFavorite(FavoriteItem favoriteItem);
        void onItemClick(FavoriteItem favoriteItem);
    }

    public FavoriteAdapter(List<FavoriteItem> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteItem item = favoriteItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    public void updateFavorites(List<FavoriteItem> newFavorites) {
        this.favoriteItems = newFavorites;
        notifyDataSetChanged();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemName;
        private TextView itemDescription;
        private ImageButton removeFavoriteButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.favoriteItemImage);
            itemName = itemView.findViewById(R.id.favoriteItemName);
            itemDescription = itemView.findViewById(R.id.favoriteItemDescription);
            removeFavoriteButton = itemView.findViewById(R.id.removeFavoriteButton);
        }

        public void bind(FavoriteItem item) {
            itemName.setText(item.getName());
            itemDescription.setText(item.getDescription() != null ? item.getDescription() : "No description");

            // Load image
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.home_icon)
                        .error(R.drawable.home_icon)
                        .into(itemImage);
            } else {
                itemImage.setImageResource(R.drawable.home_icon);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (onFavoriteClickListener != null) {
                    onFavoriteClickListener.onItemClick(item);
                }
            });

            removeFavoriteButton.setOnClickListener(v -> {
                if (onFavoriteClickListener != null) {
                    onFavoriteClickListener.onRemoveFavorite(item);
                }
            });
        }
    }
}

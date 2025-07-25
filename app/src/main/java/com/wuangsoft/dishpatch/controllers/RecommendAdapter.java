package com.wuangsoft.dishpatch.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.MenuItem;
import com.wuangsoft.dishpatch.ui.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendViewHolder> {
    private List<MenuItem> items = new ArrayList<>();

    public void setItems(List<MenuItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecommendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);
        return new RecommendViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecommendViewHolder holder, int position) {
        MenuItem item = items.get(position);

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.home_icon)
                .error(R.drawable.home_icon)
                .into(holder.image);

        holder.price.setText(String.format("%,.0f₫", item.getPrice()).replace(',', '.'));

        // Format rating like "4.5 ★" or "No rating"
        double ratingValue = item.getRating();
        if (ratingValue > 0) {
            holder.rating.setText(String.format("%.1f ★", ratingValue));
        } else {
            holder.rating.setText("No rating");
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("menuItem", item);
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RecommendViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView iconFavorite;
        TextView name, price, rating;

        public RecommendViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageRecommend);
            price = itemView.findViewById(R.id.textPrice);
            rating = itemView.findViewById(R.id.textRating);
        }
    }
}

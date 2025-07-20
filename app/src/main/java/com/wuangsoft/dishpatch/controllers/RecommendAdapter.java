package com.wuangsoft.dishpatch.controllers;

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
                .into(holder.image);
        holder.name.setText(item.getName());
        holder.price.setText(String.format("%,.0fđ", item.getPrice()));
        holder.rating.setText(String.valueOf(item.getRating()));
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
            iconFavorite = itemView.findViewById(R.id.iconFavorite);
            name = itemView.findViewById(R.id.textRecommendName);
            price = itemView.findViewById(R.id.textPrice);
            rating = itemView.findViewById(R.id.textRating);
        }
    }
}

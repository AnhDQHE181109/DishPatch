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

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.BestSellerViewHolder> {
    private List<MenuItem> items = new ArrayList<>();

    public void setItems(List<MenuItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BestSellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_best_seller, parent, false);
        return new BestSellerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestSellerViewHolder holder, int position) {
        MenuItem item = items.get(position);
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .into(holder.image);
        holder.price.setText(String.format("%,.0fđ", item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class BestSellerViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView price;

        public BestSellerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageBestSeller);
            price = itemView.findViewById(R.id.textPrice);
        }
    }
}
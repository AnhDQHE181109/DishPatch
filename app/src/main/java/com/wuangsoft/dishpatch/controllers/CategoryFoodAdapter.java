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

public class CategoryFoodAdapter extends RecyclerView.Adapter<CategoryFoodAdapter.CategoryFoodViewHolder> {
    private List<MenuItem> items = new ArrayList<>();
    private Context context;

    public CategoryFoodAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_food, parent, false);
        return new CategoryFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryFoodViewHolder holder, int position) {
        MenuItem item = items.get(position);

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.home_icon)
                .error(R.drawable.home_icon)
                .into(holder.foodImage);

        holder.foodName.setText(item.getName());
        holder.foodPrice.setText(String.format("%,.0f₫", item.getPrice()).replace(',', '.'));
        
        // Format rating
        if (item.getRating() > 0) {
            holder.foodRating.setText(String.format("%.1f ★", item.getRating()));
        } else {
            holder.foodRating.setText("No rating");
        }

        // Set click listener to navigate to product detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("menuItem", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CategoryFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, foodPrice, foodRating;

        public CategoryFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_name);
            foodPrice = itemView.findViewById(R.id.food_price);
            foodRating = itemView.findViewById(R.id.food_rating);
        }
    }
}

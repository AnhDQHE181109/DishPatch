package com.wuangsoft.dishpatch.controllers;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import com.wuangsoft.dishpatch.models.Category;
import com.wuangsoft.dishpatch.R;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories = new ArrayList<>();

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.textCategory.setText(category.getName());
        Glide.with(holder.itemView.getContext())
                .load(category.getImageUrl())
                .placeholder(android.R.color.darker_gray)
                .error(android.R.drawable.ic_dialog_alert)
                .into(holder.imageCategory);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCategory;
        TextView textCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCategory = itemView.findViewById(R.id.imageCategory);
            textCategory = itemView.findViewById(R.id.textCategory);
        }
    }
}


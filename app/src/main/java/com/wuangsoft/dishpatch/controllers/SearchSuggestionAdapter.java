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

public class SearchSuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_RESULT = 0;
    private static final int TYPE_EMPTY = 1;

    private List<MenuItem> menuItemList = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return menuItemList.isEmpty() ? TYPE_EMPTY : TYPE_RESULT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_empty, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_search_suggestion, parent, false);
            return new ResultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ResultViewHolder && !menuItemList.isEmpty()) {
            MenuItem item = menuItemList.get(position);
            ResultViewHolder resultHolder = (ResultViewHolder) holder;
            resultHolder.nameTextView.setText(item.getName());
            resultHolder.priceTextView.setText(item.getPrice() + "đ");
            Glide.with(resultHolder.itemView.getContext())
                    .load(item.getImageUrl())
                    .into(resultHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return menuItemList.isEmpty() ? 1 : menuItemList.size();
    }

    public void setMenuItems(List<MenuItem> items) {
        this.menuItemList = items;
        notifyDataSetChanged();
    }

    // ViewHolder for actual results
    static class ResultViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.suggestionImage);
            nameTextView = itemView.findViewById(R.id.suggestionName);
            priceTextView = itemView.findViewById(R.id.suggestionPrice);
        }
    }

    // ViewHolder for empty message
    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

package com.wuangsoft.dishpatch.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.CartItem;

import java.util.List;

import javax.security.auth.callback.Callback;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemHolder> {

    List<CartItem> cartItems;
    private Callback callback;

    public CartItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public CartItemAdapter(List<CartItem> cartItems, Callback callback) {
        this.cartItems = cartItems;
        this.callback = callback;
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_row, parent, false);

        return new CartItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemHolder holder, int position) {
        Glide.with(holder.itemView).load(cartItems.get(position).getProductImageURL()).into(holder.productImage);
//        holder.productImage.setImageResource(cartItems.get(position).getProductImageResourceID());
        holder.productName.setText(cartItems.get(position).getProductName());
        holder.productPrice.setText(String.format("%,d",
                Long.parseLong(cartItems.get(position).getProductPrice())).replace(',', '.') + "₫");
        holder.productQuantity.setText(cartItems.get(position).getProductQuantity());
        holder.bindCheckboxListener();
        holder.bindDecrementButtonListener(cartItems.get(position), callback);
        holder.bindIncrementButtonListener(cartItems.get(position), callback);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onCheckedChanged(CartItem cartItem, boolean isChecked);
        void onDecrementClick(CartItem cartItem);
        void onIncrementClick(CartItem cartItem);
    }

    class CartItemHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productQuantity;
        ImageButton decrementButton;
        ImageButton incrementButton;

        public CartItemHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cartItemCheckbox);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            incrementButton = itemView.findViewById(R.id.incrementButton);
        }

        void bindCheckboxListener() {
//            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (callback != null) {
//                    callback.onCheckedChanged(cartItems.get(getAdapterPosition()), isChecked);
//                }
//            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (callback != null) callback.onCheckedChanged(cartItems.get(getAdapterPosition()), isChecked);
                }
            });
        }

        void bindDecrementButtonListener(CartItem cartItem, Callback callback) {
            decrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onDecrementClick(cartItem);
                }
            });
        }

        void bindIncrementButtonListener(CartItem cartItem, Callback callback) {
            incrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) callback.onIncrementClick(cartItem);
                }
            });
        }
    }
}

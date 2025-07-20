package com.wuangsoft.dishpatch.controllers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.CartItem;
import com.wuangsoft.dishpatch.ui.ShoppingCartActivity;

import java.util.List;

public class CheckoutListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ShoppingCartActivity.class.getSimpleName();
    public static final int cartItemRow = 1;
    public static final int checkoutSummaryRow = 0;

    private List<CartItem> selectedCartItems;

    public CheckoutListAdapter(List<CartItem> selectedCartItems) {
        this.selectedCartItems = selectedCartItems;
    }

    public CheckoutListAdapter() {}

    public List<CartItem> getSelectedCartItems() {
        return selectedCartItems;
    }

    public void setSelectedCartItems(List<CartItem> selectedCartItems) {
        this.selectedCartItems = selectedCartItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case cartItemRow:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_list_cart_item, parent, false);
                return new CartItemHolder(view);
            case checkoutSummaryRow:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_list_payment, parent, false);
                return new CheckoutSummaryHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position != selectedCartItems.size()) {
            CartItem cartItem = selectedCartItems.get(position);
            if (cartItem != null) {
                Log.i(TAG, "cartItem " + cartItem);
                Glide.with(holder.itemView).load(cartItem.getProductImageURL()).into(((CartItemHolder)holder).productImage);
                ((CartItemHolder)holder).productName.setText(cartItem.getProductName());
                ((CartItemHolder)holder).productPrice.setText(String.format("%,d",
                        Long.parseLong(cartItem.getProductPrice())).replace(',', '.') + "₫");
                ((CartItemHolder)holder).productQuantity.setText(cartItem.getProductQuantity());
            }
        } else {
            long subtotalPrice = 0;
            for (CartItem selectedCartItem : selectedCartItems) {
                subtotalPrice += Long.parseLong(selectedCartItem.getProductPrice()) *
                        Long.parseLong(selectedCartItem.getProductQuantity());
//                Log.i(TAG, "product quantity: " + selectedCartItem.getProductQuantity());
            }

            ((CheckoutSummaryHolder)holder).totalPrice.setText(String.format("%,d",
                    subtotalPrice).replace(',', '.') + "₫");
        }
    }

    @Override
    public int getItemCount() {
        return selectedCartItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (selectedCartItems != null) {
            if (position == selectedCartItems.size()) {
                return checkoutSummaryRow;
            } else {
                return cartItemRow;
            }
        }

        return 1;
    }

    class CartItemHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView productQuantity;

        public CartItemHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage_confirm);
            productName = itemView.findViewById(R.id.productName_confirm);
            productPrice = itemView.findViewById(R.id.productPrice_confirm);
            productQuantity = itemView.findViewById(R.id.productQuantity_confirm);
        }

    }

    class CheckoutSummaryHolder extends RecyclerView.ViewHolder {
        TextView totalPrice;

        public CheckoutSummaryHolder(@NonNull View itemView) {
            super(itemView);
            totalPrice = itemView.findViewById(R.id.long_total_price);
        }
    }
}

package com.wuangsoft.dishpatch.controllers;

import android.util.Log;
import android.util.SparseBooleanArray;
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
import com.wuangsoft.dishpatch.utilities.HomeDataCallback;
import com.wuangsoft.dishpatch.utilities.HomeDataProvider;

import java.util.List;

import java.util.List;

import javax.security.auth.callback.Callback;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemHolder> {

    List<CartItem> cartItems;
    private Callback callback;
    private SparseBooleanArray itemsStateArray = new SparseBooleanArray();
    private boolean isSelectedAll = false;

    public CartItemAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public CartItemAdapter(List<CartItem> cartItems, Callback callback) {
        this.cartItems = cartItems;
        this.callback = callback;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_row, parent, false);

//        for (int i = 0; i < cartItems.size(); i++) {
//            itemsStateArray.put(i, false);
//        }
//        Log.i("CartItemAdapter: ", itemsStateArray.toString());

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
        holder.bindCheckboxListener(position);
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

    public void selectAll() {
        isSelectedAll = true;
        for (int i = 0; i < cartItems.size(); i++) {
            itemsStateArray.put(i, true);
        }
        notifyDataSetChanged();
    }

    public void deSelectAll() {
        isSelectedAll = false;
        for (int i = 0; i < cartItems.size(); i++) {
            itemsStateArray.put(i, false);
        }
        notifyDataSetChanged();
    }

    public void setItemSelectedState(int position, boolean isSelected) {
        if (position >= 0 && position < cartItems.size()) {
            itemsStateArray.put(position, isSelected);
        }
    }

    public interface Callback {
        void onCheckedChanged(CartItem cartItem, boolean isChecked);
//        void onClick(CartItem cartItem);

        void onDecrementClick(CartItem cartItem);
        void onIncrementClick(CartItem cartItem);
        void onImageClick(com.wuangsoft.dishpatch.models.MenuItem menuItem);
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

            productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        CartItem cartItem = cartItems.get(getAdapterPosition());
                        
                        // Fetch complete product details from database to ensure description is included
                        HomeDataProvider dataProvider = new HomeDataProvider();
                        dataProvider.getMenuItems(new HomeDataCallback.MenuItemCallback() {
                            @Override
                            public void onMenuItemsLoaded(List<com.wuangsoft.dishpatch.models.MenuItem> items) {
                                // Find the matching menu item by ID
                                com.wuangsoft.dishpatch.models.MenuItem fullMenuItem = null;
                                for (com.wuangsoft.dishpatch.models.MenuItem item : items) {
                                    if (item.getId().equals(cartItem.getProductID())) {
                                        fullMenuItem = item;
                                        break;
                                    }
                                }
                                
                                if (fullMenuItem != null) {
                                    // Pass the complete menu item to the callback
                                    callback.onImageClick(fullMenuItem);
                                } else {
                                    // Fallback: create MenuItem from CartItem data
                                    com.wuangsoft.dishpatch.models.MenuItem menuItem = new com.wuangsoft.dishpatch.models.MenuItem();
                                    menuItem.setId(cartItem.getProductID());
                                    menuItem.setName(cartItem.getProductName());
                                    menuItem.setImageUrl(cartItem.getProductImageURL());
                                    // Convert price string to double (remove currency symbols and dots)
                                    String priceStr = cartItem.getProductPrice().replaceAll("[₫.,]", "");
                                    try {
                                        menuItem.setPrice(Double.parseDouble(priceStr));
                                    } catch (NumberFormatException e) {
                                        menuItem.setPrice(0.0);
                                    }
                                    
                                    callback.onImageClick(menuItem);
                                }
                            }
                        });
                    }
                }
            });
        }

        void bindCheckboxListener(int position) {
//            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (callback != null) {
//                    callback.onCheckedChanged(cartItems.get(getAdapterPosition()), isChecked);
//                }
//            });

            checkBox.setOnCheckedChangeListener(null);

            checkBox.setChecked(itemsStateArray.get(getAdapterPosition(), false));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!itemsStateArray.get(getAdapterPosition(), false));
//                    Log.i("CartItemAdapter", "itemView onClick called");
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    int adapterPosition = getAdapterPosition();
//
//                    if (!itemsStateArray.get(adapterPosition) && isChecked) {
//                        itemsStateArray.put(adapterPosition, true);
//                    } else if (itemsStateArray.get(adapterPosition) && !isChecked) {
//                        itemsStateArray.put(adapterPosition, false);
//                    }

                    itemsStateArray.put(getAdapterPosition(), isChecked);
                    if (callback != null) callback.onCheckedChanged(cartItems.get(getAdapterPosition()), isChecked);

                }
            });

//            checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int adapterPosition = getAdapterPosition();
//                    if (!itemsStateArray.get(adapterPosition, false)) {
//                        checkBox.setChecked(true);
//                        itemsStateArray.put(adapterPosition, true);
//                    }
//                    else  {
//                        checkBox.setChecked(false);
//                        itemsStateArray.put(adapterPosition, false);
//                    }
//
////                    if (callback != null) callback.onCheckedChanged(cartItems.get(getAdapterPosition()), isChecked);
//                }
//            });

//            if (itemsStateArray.get(position)) {
//                checkBox.setChecked(true);
//            } else {
//                checkBox.setChecked(false);
//            }

//            Log.i("CartItemAdapter", "bindCheckboxListener called");

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

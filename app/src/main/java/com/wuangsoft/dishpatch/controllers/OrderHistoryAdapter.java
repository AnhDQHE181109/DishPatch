package com.wuangsoft.dishpatch.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.OrderHistoryItem;
import com.wuangsoft.dishpatch.models.OrderItem;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying a list of order history items in a RecyclerView.
 * Each item shows a summary of the order, including the first item's image and name,
 * the total number of items, the total price, and the date of the order.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {

    private final Context context;
    private List<OrderHistoryItem> orderList;
    private final OnOrderActionListener listener;

    /**
     * Interface to handle actions performed on an order item, such as cancelling.
     * The fragment or activity using this adapter must implement this interface.
     */
    public interface OnOrderActionListener {
        void onCancelClick(OrderHistoryItem order);
    }

    public OrderHistoryAdapter(Context context, List<OrderHistoryItem> orderList, OnOrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    /**
     * Updates the data set of the adapter and refreshes the RecyclerView.
     * @param newOrderList The new list of orders to be displayed.
     */
    public void updateData(List<OrderHistoryItem> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged(); // Refreshes the entire list. For more complex apps, consider DiffUtil.
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single order row
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        // Get the order for the current position and bind its data to the ViewHolder
        OrderHistoryItem order = orderList.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        // Return the total number of orders in the list
        return orderList != null ? orderList.size() : 0;
    }

    /**
     * The ViewHolder class holds the UI references for a single item view (a single order row).
     */
    class OrderHistoryViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewOrder;
        TextView textViewTitle, textViewQuantity, textViewTime, textViewPrice;
        Button btnCancelOrder;

        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find all the UI components within the item_order.xml layout
            imageViewOrder = itemView.findViewById(R.id.imageViewOrder);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }

        /**
         * Binds an OrderHistoryItem object to the views in the ViewHolder.
         * @param order The order data to display.
         * @param actionListener The listener to handle actions like clicks.
         */
        void bind(final OrderHistoryItem order, final OnOrderActionListener actionListener) {
            // Check if the order has items and populate the views accordingly
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                OrderItem firstItem = order.getItems().get(0);

                // Set the title to the name of the first item in the order
                textViewTitle.setText(firstItem.getName());

                // Use Glide to load the first item's image from its URL
                Glide.with(context)
                        .load(firstItem.getImageUrl())
                        .placeholder(R.drawable.welcome_button) // A default image while loading
                        .error(R.drawable.back_arrow)           // An image to show if loading fails
                        .into(imageViewOrder);

                // Display the total number of items in the order
                int itemCount = order.getItems().size();
                // Use Plurals for correct grammar ("1 item", "2 items")
                textViewQuantity.setText(context.getResources().getQuantityString(R.plurals.item_count, itemCount, itemCount));
            } else {
                // Provide fallback text if an order has no items
                textViewTitle.setText(R.string.order_details_unavailable);
                textViewQuantity.setText(R.string.no_items_in_order);
                imageViewOrder.setImageResource(R.drawable.welcome_button);
            }

            // Format and display the order creation time and total price
            if (order.getCreatedAt() != null) {
                textViewTime.setText(formatDate(order.getCreatedAt()));
            }
            if (order.getTotalAmount() != null) {
                textViewPrice.setText(formatPrice(order.getTotalAmount()));
            }

            // The "Cancel Order" button should only be visible for active ("pending") orders
            if ("pending".equalsIgnoreCase(order.getStatus())) {
                btnCancelOrder.setVisibility(View.VISIBLE);
                btnCancelOrder.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onCancelClick(order);
                    }
                });
            } else {
                // Hide the button for completed or cancelled orders
                btnCancelOrder.setVisibility(View.GONE);
            }
        }

        /**
         * Formats a Unix timestamp (in milliseconds) into a human-readable string.
         * @param timestamp The timestamp to format.
         * @return A formatted date string like "12:30 PM - 16 Jul".
         */
        private String formatDate(Long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a - dd MMM", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }

        /**
         * Formats a long value representing the price into Vietnamese Dong (₫) currency format.
         * @param price The price to format.
         * @return A formatted price string like "20.000đ".
         */
        private String formatPrice(Long price) {
            // Using a Locale for Vietnam ensures the correct currency symbol and formatting
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return format.format(price);
        }
    }
}
package com.wuangsoft.dishpatch.deploy;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.controllers.OrderHistoryAdapter;
import com.wuangsoft.dishpatch.models.OrderHistoryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdersDeployFragment extends Fragment implements OrderHistoryAdapter.OnOrderActionListener {

    // --- Views ---
    private Button btnActive, btnCompleted, btnCancelled;
    private RecyclerView recyclerViewOrders;
    private LinearLayout emptyLayout;

    // --- Firebase & Adapter ---
    private OrderHistoryAdapter adapter;
    private DatabaseReference ordersRef;
    private ValueEventListener ordersListener;
    private final List<OrderHistoryItem> allUserOrders = new ArrayList<>();

    // --- Fragment Lifecycle & Instantiation ---

    public OrdersDeployFragment() {
        // Required empty public constructor
    }

    public static OrdersDeployFragment newInstance() {
        return new OrdersDeployFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders_deploy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize all views from the layout
        initializeViews(view);

        // Set up the RecyclerView with its adapter
        setupRecyclerView();

        // Set up click listeners for the filter buttons
        setupFilterButtons();

        // Fetch all orders for the current user from Firebase
        fetchAllUserOrders();
    }

    /**
     * Finds and assigns all the UI components from the view.
     */
    private void initializeViews(View view) {
        btnActive = view.findViewById(R.id.btnActive);
        btnCompleted = view.findViewById(R.id.btnCompleted);
        btnCancelled = view.findViewById(R.id.btnCancelled);
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        emptyLayout = view.findViewById(R.id.emptyLayout);
    }

    /**
     * Configures the RecyclerView with a LayoutManager and the custom OrderHistoryAdapter.
     */
    private void setupRecyclerView() {
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        // Initialize adapter with an empty list and 'this' as the listener for cancel actions
        adapter = new OrderHistoryAdapter(getContext(), new ArrayList<>(), this);
        recyclerViewOrders.setAdapter(adapter);
    }

    /**
     * Sets up OnClickListeners for the filter buttons. Each button will trigger
     * the filtering of the orders based on a specific status.
     */
    private void setupFilterButtons() {
        // The status "pending" from your JSON maps to the "Active" button
        btnActive.setOnClickListener(v -> filterAndDisplayOrders("pending"));
        btnCompleted.setOnClickListener(v -> filterAndDisplayOrders("delivered"));
        btnCancelled.setOnClickListener(v -> filterAndDisplayOrders("cancelled"));
    }

    /**
     * Fetches all orders for the logged-in user from the Firebase Realtime Database.
     */
    private void fetchAllUserOrders() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "You need to be logged in to view orders", Toast.LENGTH_LONG).show();
            updateEmptyView(true); // Show empty layout if no user
            return;
        }

        String userId = currentUser.getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);

        // This listener will react to any data changes at the user's orders node
        ordersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allUserOrders.clear();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    OrderHistoryItem order = orderSnapshot.getValue(OrderHistoryItem.class);
                    if (order != null) {
                        // The order ID is the key of the snapshot, so we set it manually
                        order.setOrderId(orderSnapshot.getKey());
                        allUserOrders.add(order);
                    }
                }
                // Sort orders by creation date to show the newest ones first
                Collections.sort(allUserOrders, (o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()));

                // Initially, display the active orders
                filterAndDisplayOrders("pending");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                updateEmptyView(true); // Show empty view on error
            }
        };
        ordersRef.addValueEventListener(ordersListener);
    }

    /**
     * Filters the master list of orders by status and updates the RecyclerView.
     * @param status The status to filter by ("pending", "delivered", "cancelled").
     */
    private void filterAndDisplayOrders(String status) {
        List<OrderHistoryItem> filteredList = new ArrayList<>();
        for (OrderHistoryItem order : allUserOrders) {
            if (status.equalsIgnoreCase(order.getStatus())) {
                filteredList.add(order);
            }
        }

        adapter.updateData(filteredList);

        // THIS IS THE KEY PART FOR THE OVERALL EMPTY VIEW
        updateEmptyView(filteredList.isEmpty());

        updateButtonStyles(status);
    }

    /**
     * Updates the visual style of the filter buttons to show which one is currently selected.
     * @param activeStatus The status of the currently active filter.
     */
    private void updateButtonStyles(String activeStatus) {
        if (getContext() == null) return; // Ensure context is available

        // Reset all buttons to the default style
        btnActive.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.button_inactive_background));
        btnCompleted.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.button_inactive_background));
        btnCancelled.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.button_inactive_background));

        // Set the active button to a different style
        int activeColor = ContextCompat.getColor(getContext(), R.color.button_active_background);
        if ("pending".equalsIgnoreCase(activeStatus)) {
            btnActive.setBackgroundColor(activeColor);
        } else if ("delivered".equalsIgnoreCase(activeStatus)) {
            btnCompleted.setBackgroundColor(activeColor);
        } else if ("cancelled".equalsIgnoreCase(activeStatus)) {
            btnCancelled.setBackgroundColor(activeColor);
        }
    }


    /**
     * Toggles the visibility of the RecyclerView and the empty layout message.
     * @param isEmpty True if the list is empty, false otherwise.
     */
    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            // No orders match the filter, so show the big "empty" icon and message
            recyclerViewOrders.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            // There are orders to show, so display the list
            recyclerViewOrders.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Handles the "Cancel Order" button click from the adapter.
     * Shows a confirmation dialog before proceeding.
     */
    @Override
    public void onCancelClick(OrderHistoryItem order) {
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Proceed with cancellation
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (userId != null) {
                        DatabaseReference orderToCancelRef = FirebaseDatabase.getInstance()
                                .getReference("Orders")
                                .child(userId)
                                .child(order.getOrderId());

                        // Update the status field in Firebase to "cancelled"
                        orderToCancelRef.child("status").setValue("cancelled")
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Order cancelled", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to cancel order", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Clean up the Firebase listener when the fragment's view is destroyed
     * to prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ordersRef != null && ordersListener != null) {
            ordersRef.removeEventListener(ordersListener);
        }
    }
}
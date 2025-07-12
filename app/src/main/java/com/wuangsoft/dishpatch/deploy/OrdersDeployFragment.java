package com.wuangsoft.dishpatch.deploy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.wuangsoft.dishpatch.R;

public class OrdersDeployFragment extends Fragment {

    private Button btnActive, btnCompleted, btnCancelled;
    private RecyclerView recyclerViewOrders;
    private LinearLayout emptyLayout;

    public OrdersDeployFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders_deploy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnActive = view.findViewById(R.id.btnActive);
        btnCompleted = view.findViewById(R.id.btnCompleted);
        btnCancelled = view.findViewById(R.id.btnCancelled);
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        emptyLayout = view.findViewById(R.id.emptyLayout);

        // Gán listener cho nút lọc
        btnActive.setOnClickListener(v -> loadOrders("pending"));
        btnCompleted.setOnClickListener(v -> loadOrders("delivered"));
        btnCancelled.setOnClickListener(v -> loadOrders("canceled"));

        // Mặc định tải đơn Active
        loadOrders("pending");
    }

    private void loadOrders(String status) {
        // TODO: Load đơn từ Firestore theo status (sẽ viết sau)

        // Tạm thời ẩn danh sách và hiển thị layout trống
        recyclerViewOrders.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }
}

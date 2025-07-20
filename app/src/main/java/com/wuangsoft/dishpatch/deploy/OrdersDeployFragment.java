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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersDeployFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersDeployFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Button btnActive, btnCompleted, btnCancelled;
    private RecyclerView recyclerViewOrders;
    private LinearLayout emptyLayout;

    public OrdersDeployFragment() {
        // Required empty public constructor
    }

    public static OrdersDeployFragment newInstance(String param1, String param2) {
        OrdersDeployFragment fragment = new OrdersDeployFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        btnActive.setOnClickListener(v -> loadOrders("pending"));
        btnCompleted.setOnClickListener(v -> loadOrders("delivered"));
        btnCancelled.setOnClickListener(v -> loadOrders("canceled"));

        loadOrders("pending");
    }

    private void loadOrders(String status) {
        // TODO: Load orders from Firestore by status

        recyclerViewOrders.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }
}

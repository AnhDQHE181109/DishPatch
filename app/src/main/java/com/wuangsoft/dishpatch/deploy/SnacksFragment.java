package com.wuangsoft.dishpatch.deploy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.controllers.CategoryFoodAdapter;
import com.wuangsoft.dishpatch.models.MenuItem;
import com.wuangsoft.dishpatch.utilities.HomeDataCallback;
import com.wuangsoft.dishpatch.utilities.HomeDataProvider;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SnacksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SnacksFragment extends Fragment {

    private CategoryFoodAdapter adapter;
    private HomeDataProvider dataProvider;

    public SnacksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_snacks, container, false);
        
        setupRecyclerView(view);
        setupCategoryButtons(view);
        loadSnackItems();
        
        return view;
    }
    
    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryFoodAdapter(getContext());
        recyclerView.setAdapter(adapter);
        dataProvider = new HomeDataProvider();
    }
    
    private void loadSnackItems() {
        dataProvider.getMenuItemsByCategory("cat02", new HomeDataCallback.MenuItemCallback() {
            @Override
            public void onMenuItemsLoaded(List<MenuItem> items) {
                if (adapter != null) {
                    adapter.setItems(items);
                }
            }
        });
    }
    
    private void setupCategoryButtons(View view) {
        ImageButton snacksBtn = view.findViewById(R.id.snacksbttn);
        ImageButton mealsBtn = view.findViewById(R.id.mealbttn);
        ImageButton veganBtn = view.findViewById(R.id.veganbttn);
        ImageButton dessertBtn = view.findViewById(R.id.dessertbttn);
        ImageButton drinksBtn = view.findViewById(R.id.drinksbttn);
        
        // Set current fragment as active
        if (snacksBtn != null) {
            snacksBtn.setSelected(true);
        }
        
        if (mealsBtn != null) {
            mealsBtn.setOnClickListener(v -> navigateToCategory("meal"));
        }
        if (veganBtn != null) {
            veganBtn.setOnClickListener(v -> navigateToCategory("vegan"));
        }
        if (dessertBtn != null) {
            dessertBtn.setOnClickListener(v -> navigateToCategory("dessert"));
        }
        if (drinksBtn != null) {
            drinksBtn.setOnClickListener(v -> navigateToCategory("drinks"));
        }
    }
    
    private void navigateToCategory(String categoryName) {
        // Get the parent activity and call the navigation method
        if (getActivity() instanceof MainActivityDeploy) {
            ((MainActivityDeploy) getActivity()).navigateToExploreWithCategory(categoryName);
        }
    }
}
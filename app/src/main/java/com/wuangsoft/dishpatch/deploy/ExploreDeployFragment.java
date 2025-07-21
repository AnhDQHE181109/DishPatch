package com.wuangsoft.dishpatch.deploy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.wuangsoft.dishpatch.R;

public class ExploreDeployFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";
    private String selectedCategory = "snacks"; // Default category

    public ExploreDeployFragment() {
        // Required empty public constructor
    }

    public static ExploreDeployFragment newInstance(String category) {
        ExploreDeployFragment fragment = new ExploreDeployFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedCategory = getArguments().getString(ARG_CATEGORY, "snacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Use the simple explore layout as container
        View root = inflater.inflate(R.layout.fragment_explore_deploy, container, false);
        
        showCategoryFragment();
        
        return root;
    }
    
    private void showCategoryFragment() {
        Fragment categoryFragment;
        
        switch (selectedCategory) {
            case "meal":
                categoryFragment = new MealFragment();
                break;
            case "vegan":
                categoryFragment = new VeganFragment();
                break;
            case "dessert":
                categoryFragment = new DessertFragment();
                break;
            case "drinks":
                categoryFragment = new DrinksFragment();
                break;
            default:
                categoryFragment = new SnacksFragment();
                break;
        }
        
        // Replace the entire explore container with the category fragment
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.explore_container, categoryFragment);
        transaction.commit();
    }
}
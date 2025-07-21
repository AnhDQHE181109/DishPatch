package com.wuangsoft.dishpatch.deploy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.Category;
import com.wuangsoft.dishpatch.models.MenuItem;
import com.wuangsoft.dishpatch.ui.ShoppingCartActivity;
import com.wuangsoft.dishpatch.utilities.HomeDataCallback;
import com.wuangsoft.dishpatch.utilities.HomeDataProvider;

import com.wuangsoft.dishpatch.controllers.CategoryAdapter;
import com.wuangsoft.dishpatch.controllers.BestSellerAdapter;
import com.wuangsoft.dishpatch.controllers.RecommendAdapter;
import com.wuangsoft.dishpatch.controllers.SearchSuggestionAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeDeployFragment extends Fragment {

    private RecyclerView recyclerViewCategories, recyclerViewBestSeller, recyclerViewRecommend, recyclerViewSearchSuggestions;
    private LinearLayout searchResultsLayout;
    private EditText searchBar;

    private CategoryAdapter categoryAdapter;
    private BestSellerAdapter bestSellerAdapter;
    private RecommendAdapter recommendAdapter;
    private SearchSuggestionAdapter searchSuggestionAdapter;

    private HomeDataProvider dataProvider;
    private List<MenuItem> allMenuItems = new ArrayList<>();

    public HomeDeployFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_deploy, container, false);

        recyclerViewCategories = root.findViewById(R.id.recyclerViewCategories);
        recyclerViewBestSeller = root.findViewById(R.id.recyclerViewBestSeller);
        recyclerViewRecommend = root.findViewById(R.id.recyclerViewRecommend);
        recyclerViewSearchSuggestions = root.findViewById(R.id.recyclerViewSearchSuggestions);
        searchResultsLayout = root.findViewById(R.id.searchResultsLayout);
        searchBar = root.findViewById(R.id.searchbar);

        dataProvider = new HomeDataProvider();

        categoryAdapter = new CategoryAdapter();
        bestSellerAdapter = new BestSellerAdapter();
        recommendAdapter = new RecommendAdapter();
        searchSuggestionAdapter = new SearchSuggestionAdapter();

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBestSeller.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRecommend.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSearchSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewCategories.setAdapter(categoryAdapter);
        recyclerViewBestSeller.setAdapter(bestSellerAdapter);
        recyclerViewRecommend.setAdapter(recommendAdapter);
        recyclerViewSearchSuggestions.setAdapter(searchSuggestionAdapter);

        searchResultsLayout.setVisibility(View.GONE);

        ImageButton accountButton = root.findViewById(R.id.accountbutton);
        accountButton.setOnClickListener(v -> {
            ProfileFragment profileFragment = ProfileFragment.newInstance();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, profileFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        
        // Setup cart button click listener
//        ImageButton cartButton = root.findViewById(R.id.cartbutton);
//        cartButton.setOnClickListener(v -> {
//            // Navigate to ShoppingCartActivity
//            Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
//            startActivity(intent);
//        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim().toLowerCase();
                if (input.isEmpty()) {
                    searchResultsLayout.setVisibility(View.GONE);
                    searchSuggestionAdapter.setMenuItems(new ArrayList<>());
                } else {
                    List<MenuItem> filtered = new ArrayList<>();
                    for (MenuItem item : allMenuItems) {
                        if (item.getName().toLowerCase().contains(input)) {
                            filtered.add(item);
                        }
                    }
                    searchSuggestionAdapter.setMenuItems(filtered);
                    searchResultsLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadHomeData();

        return root;
    }

    private void loadHomeData() {
        dataProvider.getCategories(new HomeDataCallback.CategoryCallback() {
            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                categoryAdapter.setCategories(categories);
            }
        });

        dataProvider.getMenuItems(new HomeDataCallback.MenuItemCallback() {
            @Override
            public void onMenuItemsLoaded(List<MenuItem> items) {
                List<MenuItem> best = new ArrayList<>(items);
                best.sort((a, b) -> (int) (b.getPrice() - a.getPrice()));
                bestSellerAdapter.setItems(best.subList(0, Math.min(best.size(), 5)));
                recommendAdapter.setItems(items);
                allMenuItems = items;
            }
        });
    }
}
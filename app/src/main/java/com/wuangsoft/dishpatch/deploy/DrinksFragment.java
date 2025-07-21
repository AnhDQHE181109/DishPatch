package com.wuangsoft.dishpatch.deploy;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.controllers.CategoryFoodAdapter;
import com.wuangsoft.dishpatch.controllers.SearchSuggestionAdapter;
import com.wuangsoft.dishpatch.models.MenuItem;
import com.wuangsoft.dishpatch.utilities.HomeDataCallback;
import com.wuangsoft.dishpatch.utilities.HomeDataProvider;

import java.util.ArrayList;
import java.util.List;

public class DrinksFragment extends Fragment {

    private CategoryFoodAdapter adapter;
    private SearchSuggestionAdapter searchSuggestionAdapter;
    private HomeDataProvider dataProvider;

    private EditText searchBar;
    private LinearLayout searchResultsLayout;
    private RecyclerView recyclerViewSearchSuggestions;

    private List<MenuItem> allMenuItems = new ArrayList<>();

    public DrinksFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drinks, container, false);

        setupRecyclerView(view);
        setupCategoryButtons(view);
        setupSearchBar(view);
        loadDrinksItems();
        loadAllMenuItems();  // Load full menu for global search

        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryFoodAdapter(getContext());
        recyclerView.setAdapter(adapter);
        dataProvider = new HomeDataProvider();
    }

    private void loadDrinksItems() {
        dataProvider.getMenuItemsByCategory("cat01", new HomeDataCallback.MenuItemCallback() {
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

        if (drinksBtn != null) {
            drinksBtn.setSelected(true);
        }

        if (snacksBtn != null) {
            snacksBtn.setOnClickListener(v -> navigateToCategory("snacks"));
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
    }

    private void navigateToCategory(String category) {
        if (getActivity() instanceof MainActivityDeploy) {
            ((MainActivityDeploy) getActivity()).navigateToExploreWithCategory(category);
        }
    }

    private void setupSearchBar(View view) {
        searchBar = view.findViewById(R.id.searchbar);
        searchResultsLayout = view.findViewById(R.id.searchResultsLayout);
        recyclerViewSearchSuggestions = view.findViewById(R.id.recyclerViewSearchSuggestions);

        searchSuggestionAdapter = new SearchSuggestionAdapter();
        recyclerViewSearchSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearchSuggestions.setAdapter(searchSuggestionAdapter);

        searchResultsLayout.setVisibility(View.GONE);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

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
        });
    }

    private void loadAllMenuItems() {
        dataProvider.getMenuItems(new HomeDataCallback.MenuItemCallback() {
            @Override
            public void onMenuItemsLoaded(List<MenuItem> items) {
                allMenuItems = new ArrayList<>(items);
            }
        });
    }
}

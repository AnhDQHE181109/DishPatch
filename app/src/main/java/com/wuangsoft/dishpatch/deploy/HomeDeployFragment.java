// HomeDeployFragment.java (Full Home Screen with Firebase + Adapters)
package com.wuangsoft.dishpatch.deploy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wuangsoft.dishpatch.R;
import com.wuangsoft.dishpatch.models.Category;
import com.wuangsoft.dishpatch.models.MenuItem;
import com.wuangsoft.dishpatch.utilities.HomeDataCallback;
import com.wuangsoft.dishpatch.utilities.HomeDataProvider;

import com.wuangsoft.dishpatch.controllers.CategoryAdapter;
import com.wuangsoft.dishpatch.controllers.BestSellerAdapter;
import com.wuangsoft.dishpatch.controllers.RecommendAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeDeployFragment extends Fragment {

    private RecyclerView recyclerViewCategories, recyclerViewBestSeller, recyclerViewRecommend;
    private CategoryAdapter categoryAdapter;
    private BestSellerAdapter bestSellerAdapter;
    private RecommendAdapter recommendAdapter;
    private HomeDataProvider dataProvider;

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

        dataProvider = new HomeDataProvider();

        categoryAdapter = new CategoryAdapter();
        bestSellerAdapter = new BestSellerAdapter();
        recommendAdapter = new RecommendAdapter();

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBestSeller.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewRecommend.setLayoutManager(new GridLayoutManager(getContext(), 2));

        recyclerViewCategories.setAdapter(categoryAdapter);
        recyclerViewBestSeller.setAdapter(bestSellerAdapter);
        recyclerViewRecommend.setAdapter(recommendAdapter);

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
            }
        });
    }
}

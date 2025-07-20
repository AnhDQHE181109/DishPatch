package com.wuangsoft.dishpatch.utilities;

import com.wuangsoft.dishpatch.models.Category;
import com.wuangsoft.dishpatch.models.MenuItem;

import java.util.List;

public class HomeDataCallback {

    public interface CategoryCallback {
        void onCategoriesLoaded(List<Category> categories);
    }

    public interface MenuItemCallback {
        void onMenuItemsLoaded(List<MenuItem> items);
    }
}


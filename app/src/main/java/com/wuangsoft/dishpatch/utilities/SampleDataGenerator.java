package com.wuangsoft.dishpatch.utilities;

import android.os.Build;

import com.wuangsoft.dishpatch.models.CartItem;
import com.wuangsoft.dishpatch.models.CartItemFirebase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SampleDataGenerator {

    private List<CartItem> cartItems;
    private List<CartItemFirebase> firebaseCartItems;

    public List<CartItemFirebase> generateFirebaseSampleData() {
        firebaseCartItems = new ArrayList<>();
        Long addedAt;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addedAt  = Instant.now().getEpochSecond();
        } else {
            addedAt = (long) 0;
        }
        firebaseCartItems.add(new CartItemFirebase(addedAt, "item04", "https://shop.yamaha-motor-india.com/cdn/shop/files/black_ab93a3f9-f389-4a7a-bf90-b9ba660ba92a.png?v=1702465214", "Food 1", 16000L, 3L));
        firebaseCartItems.add(new CartItemFirebase(addedAt, "item05", "https://www.yamaha-motor-india.com/theme/v4/images/webp_images/r_series_all/r3/color/r3-blue.webp", "Food 2", 32000L, 1L, "More ice"));
        firebaseCartItems.add(new CartItemFirebase(addedAt, "item06", "https://images.unsplash.com/photo-1550547660-d9450f859349", "Food 3", 35000L, 6L));
        firebaseCartItems.add(new CartItemFirebase(addedAt, "item07", "https://www.yamaha-motor-india.com/theme/v4/images/header-footer/r15m.webp", "Food 4", 40000L, 4L, "More water"));
        firebaseCartItems.add(new CartItemFirebase(addedAt, "item08", "https://images.unsplash.com/photo-1568605114967-8130f3a36994", "Food 5", 20000L, 2L, "Less sugar"));
        firebaseCartItems.add(new CartItemFirebase(addedAt, "item09", "https://images.unsplash.com/photo-1551183053-bf91a1d81141", "Food 6", 50000L, 7L, "Less salt"));
        firebaseCartItems.add(new CartItemFirebase(addedAt, "item10", "https://images.unsplash.com/photo-1578985545062-69928b1d9587", "Food 7", 100000L, 2L));

        return firebaseCartItems;
    }

}

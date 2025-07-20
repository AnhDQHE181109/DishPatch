package com.wuangsoft.dishpatch.models;

public class Review {
    private float rating;
    private String itemId;

    public Review() {}

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}

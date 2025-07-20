package com.wuangsoft.dishpatch.models;

public class Review {
    private float rating;
    private String comment;
    private String itemId;
    private String userId;
    public Review() {}
    public Review(float rating, String comment, String itemId, String userId) {
        this.rating = rating;
        this.comment = comment;
        this.itemId = itemId;
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
public void setRating(float rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = rating;
}


    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}

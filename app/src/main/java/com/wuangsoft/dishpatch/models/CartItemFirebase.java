package com.wuangsoft.dishpatch.models;

public class CartItemFirebase {

    private Long addedAt;
    private String dishId;
    private String imageUrl;
    private String name;
    private Long pricePerItem;
    private Long quantity;
    private String notes;

    public CartItemFirebase() {
    }

    public CartItemFirebase(Long addedAt, String dishId, String imageUrl, String name, Long pricePerItem, Long quantity) {
        this.addedAt = addedAt;
        this.dishId = dishId;
        this.imageUrl = imageUrl;
        this.name = name;
        this.pricePerItem = pricePerItem;
        this.quantity = quantity;
    }

    public Long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Long addedAt) {
        this.addedAt = addedAt;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(Long pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "CartItemFirebase{" +
                "addedAt=" + addedAt +
                ", dishId='" + dishId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", name='" + name + '\'' +
                ", pricePerItem=" + pricePerItem +
                ", quantity=" + quantity +
                ", notes='" + notes + '\'' +
                '}';
    }
}

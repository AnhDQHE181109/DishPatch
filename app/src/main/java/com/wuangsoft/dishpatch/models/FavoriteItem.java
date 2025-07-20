package com.wuangsoft.dishpatch.models;

public class FavoriteItem {
    private String dishId;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private Long addedAt;
    
    public FavoriteItem() {
        // Default constructor required for Firebase
    }
    
    public FavoriteItem(String dishId, String name, String description, String imageUrl, Double price, Long addedAt) {
        this.dishId = dishId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.addedAt = addedAt;
    }
    
    public String getDishId() {
        return dishId;
    }
    
    public void setDishId(String dishId) {
        this.dishId = dishId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Long getAddedAt() {
        return addedAt;
    }
    
    public void setAddedAt(Long addedAt) {
        this.addedAt = addedAt;
    }
}

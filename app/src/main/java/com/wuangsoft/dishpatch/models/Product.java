package com.wuangsoft.dishpatch.models;

public class Product {
    private String name;
    private String imageUrl;
    private String price;

    public Product() {}

    public Product(String name, String imageUrl, String price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPrice() { return price; }

    public void setPrice(String price) { this.price = price; }
}

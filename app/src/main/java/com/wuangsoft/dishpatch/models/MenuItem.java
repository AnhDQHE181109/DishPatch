package com.wuangsoft.dishpatch.models;

public class MenuItem {
    private String name;
    private String imageUrl;
    private double price;
    private double rating;

    public MenuItem() {

    }

    public MenuItem(String name, String imageUrl, double price, double rating) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public double getRating() {
        return rating;
    }
}

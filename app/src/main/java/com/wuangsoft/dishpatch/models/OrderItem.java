// OrderItem.java
package com.wuangsoft.dishpatch.models;

import java.io.Serializable;

public class OrderItem implements Serializable {

    private String imageUrl;
    private String itemId;
    private String name;
    private Long price;
    private Long quantity;

    public OrderItem() {
        // Default constructor required for calls to DataSnapshot.getValue(OrderItem.class)
    }

    public OrderItem(String imageUrl, String itemId, String name, Long price, Long quantity) {
        this.imageUrl = imageUrl;
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "imageUrl='" + imageUrl + '\'' +
                ", itemId='" + itemId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
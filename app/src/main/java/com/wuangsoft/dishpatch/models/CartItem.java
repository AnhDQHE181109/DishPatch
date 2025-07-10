package com.wuangsoft.dishpatch.models;

import android.widget.ImageView;
import android.widget.TextView;

public class CartItem {

    private int productImage;
    private String productName;
    private String productPrice;
    private String productQuantity;

    public CartItem(int productImage, String productName, String productPrice, String productQuantity) {
        this.productImage = productImage;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    public int getProductImage() {
        return productImage;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "productImage=" + productImage +
                ", productName='" + productName + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productQuantity='" + productQuantity + '\'' +
                '}';
    }
}

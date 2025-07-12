package com.wuangsoft.dishpatch.models;

import android.widget.ImageView;
import android.widget.TextView;

public class CartItem {

    private String productID;
    private int productImageResourceID;
    private String productImageURL;
    private String productName;
    private String productPrice;
    private String productQuantity;

    public CartItem(int productImageResourceID, String productName, String productPrice, String productQuantity) {
        this.productImageResourceID = productImageResourceID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    public CartItem(int productImageResourceID, String productID, String productName, String productPrice, String productQuantity) {
        this.productImageResourceID = productImageResourceID;
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    public CartItem(String productID, String productImageURL, String productName, String productPrice, String productQuantity) {
        this.productImageURL = productImageURL;
        this.productID = productID;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    public CartItem() {

    }

    public String getProductImageURL() {
        return productImageURL;
    }

    public void setProductImageURL(String productImageURL) {
        this.productImageURL = productImageURL;
    }

    public int getProductImageResourceID() {
        return productImageResourceID;
    }

    public void setProductImageResourceID(int productImage) {
        this.productImageResourceID = productImage;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
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
                "productID='" + productID + '\'' +
                ", productImageResourceID=" + productImageResourceID +
                ", productImageURL='" + productImageURL + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productQuantity='" + productQuantity + '\'' +
                '}';
    }
}

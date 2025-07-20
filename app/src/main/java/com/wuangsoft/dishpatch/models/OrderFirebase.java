package com.wuangsoft.dishpatch.models;

import java.util.List;

public class OrderFirebase {
    private Long createdAt;
    private List<OrderItem> items;
    private String status;
    private Long totalAmount;
    private String userId;
    private String reviewId; // optional

    public OrderFirebase() {
    }

    public OrderFirebase(Long createdAt, List<OrderItem> items, String status, Long totalAmount, String userId) {
        this.createdAt = createdAt;
        this.items = items;
        this.status = status;
        this.totalAmount = totalAmount;
        this.userId = userId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    // Tiện ích: lấy title từ món đầu tiên
    public String getTitle() {
        return (items != null && !items.isEmpty()) ? items.get(0).getName() : "No item";
    }

    // Tiện ích: lấy ảnh món đầu tiên
    public String getFirstImageUrl() {
        return (items != null && !items.isEmpty()) ? items.get(0).getImageUrl() : null;
    }
}

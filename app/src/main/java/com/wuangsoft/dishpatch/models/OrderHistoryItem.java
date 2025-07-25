// OrderHistoryItem.java
package com.wuangsoft.dishpatch.models;

import java.io.Serializable;
import java.util.List;

public class OrderHistoryItem implements Serializable {

    private String orderId;
    private Long createdAt;
    private List<OrderItem> items;
    private String status;
    private Long totalAmount;
    private String userId;
    private String reviewId;

    public OrderHistoryItem() {
        // Default constructor required for calls to DataSnapshot.getValue(OrderHistoryItem.class)
    }

    public OrderHistoryItem(String orderId, Long createdAt, List<OrderItem> items, String status, Long totalAmount, String userId, String reviewId) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.items = items;
        this.status = status;
        this.totalAmount = totalAmount;
        this.userId = userId;
        this.reviewId = reviewId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    @Override
    public String toString() {
        return "OrderHistoryItem{" +
                "orderId='" + orderId + '\'' +
                ", createdAt=" + createdAt +
                ", items=" + items +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", userId='" + userId + '\'' +
                ", reviewId='" + reviewId + '\'' +
                '}';
    }
}
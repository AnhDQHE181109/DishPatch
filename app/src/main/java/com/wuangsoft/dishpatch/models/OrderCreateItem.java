package com.wuangsoft.dishpatch.models;

public class OrderCreateItem {

    private Long createdAt;
    private String status;
    private Long totalPrice;

    public OrderCreateItem(Long createdAt, String status, Long totalPrice) {
        this.createdAt = createdAt;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "OrderCreateItem{" + "\n" +
                "createdAt=" + createdAt + "\n" +
                ", status='" + status + '\'' + "\n" +
                ", totalPrice=" + totalPrice + "\n" +
                '}';
    }
}

package com.wuangsoft.dishpatch.models;

public class User {
    private String uid;
    private String name;
    private String address;
    private String avatar;
    private String email;
    private String online;
    private String phone;
    private int roleId;
    private String timestamp;
    private String username;

    // Default constructor required for Firebase
    public User() {}

    public User(String uid, String name, String address, String avatar, String email, 
                String online, String phone, int roleId, String timestamp, String username) {
        this.uid = uid;
        this.name = name;
        this.address = address;
        this.avatar = avatar;
        this.email = email;
        this.online = online;
        this.phone = phone;
        this.roleId = roleId;
        this.timestamp = timestamp;
        this.username = username;
    }

    // Getters and setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

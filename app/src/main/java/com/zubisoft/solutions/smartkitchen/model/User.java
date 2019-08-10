package com.zubisoft.solutions.smartkitchen.model;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String username;
    private String email;
    private String imageUrl;
    private String user_id;

    public User() {
    }

    public User(String user_id, String username, String email, String imageUrl) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

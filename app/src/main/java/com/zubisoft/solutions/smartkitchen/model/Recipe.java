package com.zubisoft.solutions.smartkitchen.model;

import java.io.Serializable;

public class Recipe implements Serializable {

    private String id;
    private String recipeName;
    private int duration;
    private String category;
    private String imageUrl;
    private String description;

    public Recipe() {
    }

    public Recipe(String recipeName, String category, String description, int duration, String imageUrl) {
        this.recipeName = recipeName;
        this.duration = duration;
        this.imageUrl = imageUrl;
        this.category = category;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}

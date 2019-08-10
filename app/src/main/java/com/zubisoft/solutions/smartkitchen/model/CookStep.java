package com.zubisoft.solutions.smartkitchen.model;

import java.io.Serializable;

public class CookStep implements Serializable {

    private String id;
    private int stepNo;
    private int duration;
    private String description;
    private String imageUrl;

    public CookStep() {
    }

    public CookStep(int stepNo, int duration, String description, String imageUrl) {
        this.stepNo = stepNo;
        this.duration = duration;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStepNo() {
        return stepNo;
    }

    public void setStepNo(int stepNo) {
        this.stepNo = stepNo;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "CookStep{" +
                "id=" + id +
                ", stepNo=" + stepNo +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                '}';
    }
}

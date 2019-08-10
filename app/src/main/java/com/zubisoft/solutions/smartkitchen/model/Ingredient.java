package com.zubisoft.solutions.smartkitchen.model;

import java.io.Serializable;

public class Ingredient implements Serializable {

    private String id;
    private String name;
    private int quantity;
    private String metrics;

    public Ingredient() {
    }

    public Ingredient(String name, int quantity, String metrics) {
        this.name = name;
        this.quantity = quantity;
        this.metrics = metrics;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", metrics='" + metrics + '\'' +
                '}';
    }
}

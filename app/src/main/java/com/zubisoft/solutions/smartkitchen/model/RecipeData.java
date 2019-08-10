package com.zubisoft.solutions.smartkitchen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecipeData implements Serializable {

    //    private String key;
//    private String recipeName;
//    private String description;
//    private int duration;
    private Recipe recipe;
    private String category;
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<CookStep> steps = new ArrayList<>();
//    private String ImageUrl;

    public RecipeData() {
    }

//    public String getkey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }
//
//    public String getRecipeName() {
//        return recipeName;
//    }
//
//    public void setRecipeName(String recipeName) {
//        this.recipeName = recipeName;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String decription) {
//        this.description = decription;
//    }
//
//    public int getDuration() {
//        return duration;
//    }
//
//    public void setDuration(int duration) {
//        this.duration = duration;
//    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<CookStep> getSteps() {
        return steps;
    }

    public void setSteps(List<CookStep> steps) {
        this.steps = steps;
    }

//    public String getImageUrl() {
//        return ImageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        ImageUrl = imageUrl;
//    }
//
//    public Recipe getRecipeData(){
//
//        return new Recipe(recipeName, category,description,duration,getImageUrl());
//    }

    private String breakList(List list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Object obj : list) {
            sb.append(obj.toString() + ",");
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public String toString() {
        return "RecipeData{" +
                ", recipe='" + recipe.toString() + '\'' +
                ", ingredients=" + breakList(ingredients) +
                ", steps=" + breakList(steps) +
                "}'";
    }
}

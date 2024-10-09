package com.example.dishdash;

public class RecipeCard {

    private String name;
    private String description;
    private String duration;
    private String mealtype1;
    private String mealtype2;
    private int image;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public String getMealType1() {
        return mealtype1;
    }

    public String getMealType2() {
        return mealtype2;
    }

    public int getImage() {
        return image;
    }

    public RecipeCard(String name, String description, String duration, String mealtype1, String mealtype2, int image) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.mealtype1 = mealtype1;
        this.mealtype2 = mealtype2;
        this.image = image;

    }
}

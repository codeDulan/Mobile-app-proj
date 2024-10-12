package com.example.dishdash;

public class RecipeCard {

    private String id;
    private String name;
    private String description;
    private String duration;
    private String mealType;
    private String imageURL;

    public RecipeCard() {
        // Default constructor required for calls to DataSnapshot.getValue(RecipeCard.class)
    }

    // Constructor including ID
    public RecipeCard(String id, String name, String description, String duration, String mealType, String imageURL) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.mealType = mealType;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public String getMealType() {
        return mealType;
    }

    public String getImageURL() {
        return imageURL;
    }
}
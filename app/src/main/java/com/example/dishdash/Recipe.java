package com.example.dishdash;

public class Recipe {

    private String id;
    private String name;
    private String description;
    private String time;
    private String ingredients;
    private String instructions;
    private String image;
    private String category;
    private String authorId;

    public Recipe() {}

    public Recipe(String name, String description, String time, String ingredients, String instructions, String image, String authorId, String category ) {

        this.name = name;
        this.description = description;
        this.time = time;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.image = image;
        this.category = category;
        this.authorId = authorId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
}

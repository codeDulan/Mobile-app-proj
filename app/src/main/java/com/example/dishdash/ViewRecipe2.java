package com.example.dishdash;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ViewRecipe2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe2);

        // Get the data passed from the previous activity
        String recipeName = getIntent().getStringExtra("recipeName");
        String recipeDetail = getIntent().getStringExtra("recipeDetail");
        String recipeImage = getIntent().getStringExtra("recipeImage");
        String recipeTime = getIntent().getStringExtra("recipeTime");

        // Find the views in the layout
        TextView recipeNameTextView = findViewById(R.id.recipeNameTextView);
        TextView recipeDetailTextView = findViewById(R.id.recipeDetailTextView);
        TextView recipeTimeTextView = findViewById(R.id.recipeTimeTextView);
        ImageView recipeImageView = findViewById(R.id.recipeImageView);

        // Set the values to the views
        recipeNameTextView.setText(recipeName);
        recipeDetailTextView.setText(recipeDetail);
        recipeTimeTextView.setText(recipeTime);

        // Load the recipe image using Glide
        Glide.with(this)
                .load(recipeImage)
                .into(recipeImageView);
    }
}

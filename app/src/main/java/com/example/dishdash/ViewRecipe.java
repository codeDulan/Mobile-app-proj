package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewRecipe extends AppCompatActivity {

    private TextView RecipeTitle, Description, Ingredients, Time, Meal, Instructions, RecipeOwner;
    private VideoView RecipeVideo;
    private ImageButton FavouriteButton, ShareButton;
    private ImageView ProfilePicture;
    private DatabaseReference recipeRef, userRef, userFavRef;
    private FirebaseAuth mAuth;
    private boolean isFavorite = false; // Track whether a recipe is favorite or not
    private String recipeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid(); // Get current logged-in user ID

        // Find views
        RecipeTitle = findViewById(R.id.tv_recipe_title);
        RecipeVideo = findViewById(R.id.player_view);
        Description = findViewById(R.id.tv_description);
        Ingredients = findViewById(R.id.tv_ingredients);
        Time = findViewById(R.id.recipe_time_viewRecipe);
        Meal = findViewById(R.id.recipe_meal_lbl_viewRecipe);
        Instructions = findViewById(R.id.tv_instructions);
        RecipeOwner = findViewById(R.id.tv_recipe_owner);
        ProfilePicture = findViewById(R.id.img_recipe_owner);
        FavouriteButton = findViewById(R.id.btn_heart_viewrecipe); // Heart button

        // Back button logic
        ImageButton backButton = findViewById(R.id.btn_back_viewrecipe);
        backButton.setOnClickListener(v -> finish());

        // Retrieve recipe ID passed from the previous activity
        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("recipeId");

        // Log to check if recipeId is null
        Log.d("ViewRecipe", "Recipe ID: " + recipeId);

        if (recipeId == null || recipeId.isEmpty()) {
            Log.e("ViewRecipe", "No Recipe ID passed. Closing activity.");
            finish(); // Close activity if recipeId is null
            return;
        }

        // Get a reference to Firebase database
        recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId);
        userFavRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favorites");

        // Fetch the recipe data and Favorite status
        fetchRecipeDetails(recipeId);
        checkFavoriteStatus(userId, recipeId);

        // Set up heart button click listener to toggle favorite
        FavouriteButton.setOnClickListener(v -> toggleFavoriteStatus(userId, recipeId));
    }

    private void fetchRecipeDetails(String recipeId) {
        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.e("ViewRecipe", "Recipe not found for ID: " + recipeId);
                    finish(); // Handle if the recipe doesn't exist
                    return;
                }

                String recipeName = dataSnapshot.child("name").getValue(String.class);
                String videoUrl = dataSnapshot.child("videoUrl").getValue(String.class);
                String description = dataSnapshot.child("description").getValue(String.class);
                String ingredients = dataSnapshot.child("ingredients").getValue(String.class);
                String time = dataSnapshot.child("time").getValue(String.class);  // Fetch time
                String category = dataSnapshot.child("category").getValue(String.class);  // Fetch category
                String instructions = dataSnapshot.child("instructions").getValue(String.class);  // Fetch instructions
                String authorId = dataSnapshot.child("authorId").getValue(String.class);

                // Set the recipe data in the UI
                RecipeTitle.setText(recipeName);
                Description.setText(description);
                Ingredients.setText(ingredients);
                Time.setText(time);
                Meal.setText(category);
                Instructions.setText(instructions);

                // Load the video
                setupVideoPlayer(videoUrl);

                // Fetch and set the author details
                fetchAuthorDetails(authorId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.e("ViewRecipe", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Function to fetch the author's details
    private void fetchAuthorDetails(String authorId) {
        if (authorId == null || authorId.isEmpty()) {
            Log.e("ViewRecipe", "No author ID found for this recipe.");
            return; // Don't proceed if authorId is null
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(authorId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.e("ViewRecipe", "Author not found for ID: " + authorId);
                    return;
                }

                String authorName = dataSnapshot.child("name").getValue(String.class);
                String authorImageUrl = dataSnapshot.child("image").getValue(String.class);

                // Set the author details
                RecipeOwner.setText(authorName);

                // Load profile image with Glide
                if (authorImageUrl != null) {
                    Glide.with(ViewRecipe.this)
                            .load(authorImageUrl)
                            .placeholder(R.drawable.img_placeholder)
                            .into(ProfilePicture);
                } else {
                    Log.e("ViewRecipe", "Author image URL is null for ID: " + authorId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewRecipe", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // IM/2021/001 - Setup the video player with controls (play, pause, etc.)
    private void setupVideoPlayer(String videoUrl) {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(RecipeVideo); // Set the anchor view for the controls
        RecipeVideo.setMediaController(mediaController);

        // Load the video if not null
        if (videoUrl != null && !videoUrl.isEmpty()) {
            RecipeVideo.setVideoPath(videoUrl);

//            Play automatically
//            RecipeVideo.start();
        } else {
            Log.e("ViewRecipe", "Video URL is empty or null for recipe ID.");
        }
    }

    // Check if the recipe is in favorites
    private void checkFavoriteStatus(String userId, String recipeId) {
        userFavRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recipe is a favorite
                    isFavorite = true;
                    FavouriteButton.setImageResource(R.drawable.ic_favorite_40dp_fill);
                } else {
                    // Recipe is not a favorite
                    isFavorite = false;
                    FavouriteButton.setImageResource(R.drawable.ic_favorite_40dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewRecipe", "Failed to check favorite status: " + databaseError.getMessage());
            }
        });
    }

    // Toggle favorite status
    private void toggleFavoriteStatus(String userId, String recipeId) {
        if (isFavorite) {
            // Remove from favorites
            userFavRef.child(recipeId).removeValue().addOnSuccessListener(aVoid -> {
                isFavorite = false;
                FavouriteButton.setImageResource(R.drawable.ic_favorite_40dp); // Default heart icon
                Toast.makeText(ViewRecipe.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Add to favorites
            userFavRef.child(recipeId).setValue(true).addOnSuccessListener(aVoid -> {
                isFavorite = true;
                FavouriteButton.setImageResource(R.drawable.ic_favorite_40dp_fill); // Filled heart icon
                Toast.makeText(ViewRecipe.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

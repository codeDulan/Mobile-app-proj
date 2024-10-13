package com.example.dishdash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class ViewRecipe extends AppCompatActivity {

    private TextView RecipeTitle, Description, Ingredients, Time, Meal, Instructions, RecipeOwner;
    private VideoView RecipeVideo;
    private ImageButton Favourite, Share;
    private ImageView ProfilePicture;
    private DatabaseReference recipeRef, userRef, userFavoritesRef;
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

        //share functionality
        Share = findViewById(R.id.btn_share_viewrecipe);
        Share.setOnClickListener(v -> {
            createDynamicLink(recipeId);
        });

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
        Favourite = findViewById(R.id.btn_heart_viewrecipe); // Heart button

        // Back button logic
        ImageButton backButton = findViewById(R.id.btn_back_viewrecipe);
        backButton.setOnClickListener(v -> finish());

        // Retrieve recipe ID passed from the previous activity
        Intent intent = getIntent();
        recipeId = intent.getStringExtra("recipeId");

        // Log to check if recipeId is null
        Log.d("ViewRecipe", "Recipe ID: " + recipeId);

        if (recipeId == null || recipeId.isEmpty()) {
            Log.e("ViewRecipe", "No Recipe ID passed. Closing activity.");
            finish(); // Close activity if recipeId is null
            return;
        }

        // Get references to Firebase database
        recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId);
        userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favorites");

        // Fetch the recipe data and Favorite status
        fetchRecipeDetails(recipeId);
        checkFavoriteStatus(userId, recipeId);

        // Set up heart button click listener to toggle favorite
        Favourite.setOnClickListener(v -> toggleFavoriteStatus(userId, recipeId));
    }




    // Function to create and share the dynamic link
    private void createDynamicLink(String recipeId) {

        String dynamicLinkDomain = "https://dishdash.page.link";

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://dishdash.page.link/recipe?recipeId=" + recipeId))  // Custom deep link
                .setDomainUriPrefix(dynamicLinkDomain)  // Your Firebase dynamic link domain
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder()
                        .setMinimumVersion(1)  // Compatible for minimum app version to open the link
                        .build())
                .buildDynamicLink();

        // Convert the dynamic link to a URI
        Uri dynamicLinkUri = dynamicLink.getUri();

        // Share the dynamic link via Intent
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this recipe: " + dynamicLinkUri.toString());
        sendIntent.setType("text/plain");

        // Start the share Intent
        Intent shareIntent = Intent.createChooser(sendIntent, "Share Recipe");
        startActivity(shareIntent);
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

                // Only load the image if the activity is not destroyed
                if (!isDestroyed()) {
                    Glide.with(ViewRecipe.this)
                            .load(authorImageUrl)
                            .placeholder(R.drawable.img_placeholder)
                            .into(ProfilePicture);
                } else {
                    Log.e("ViewRecipe", "Activity is destroyed. Skipping Glide load.");
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
        } else {
            Log.e("ViewRecipe", "Video URL is empty or null for recipe ID.");
        }
    }

    // Check if the recipe is in favorites
    private void checkFavoriteStatus(String userId, String recipeId) {
        userFavoritesRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recipe is a favorite
                    isFavorite = true;
                    Favourite.setImageResource(R.drawable.ic_favorite_40dp_fill);
                } else {
                    // Recipe is not a favorite
                    isFavorite = false;
                    Favourite.setImageResource(R.drawable.ic_favorite_40dp);
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
            userFavoritesRef.child(recipeId).removeValue().addOnSuccessListener(aVoid -> {
                isFavorite = false;
                Favourite.setImageResource(R.drawable.ic_favorite_40dp); // Default heart icon
                Toast.makeText(ViewRecipe.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Add to favorites
            userFavoritesRef.child(recipeId).setValue(true).addOnSuccessListener(aVoid -> {
                isFavorite = true;
                Favourite.setImageResource(R.drawable.ic_favorite_40dp_fill); // Filled heart icon
                Toast.makeText(ViewRecipe.this, "Added to Favorites", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Favorite extends Fragment {

    private RecyclerView favouriteRecycle;
    private RecipeCardAdapter recipeAdapter;
    private List<RecipeCard> favoriteRecipeList;
    private TextView noFavorites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // Initialize RecyclerView and list
        favouriteRecycle = view.findViewById(R.id.favoriteWindow_recycle);
        favouriteRecycle.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the recipe list and adapter
        favoriteRecipeList = new ArrayList<>();
        recipeAdapter = new RecipeCardAdapter(favoriteRecipeList, (RecipeCard recipe) -> {
            // Handling recipe click
            Intent intent = new Intent(getActivity(), ViewRecipe.class);
            intent.putExtra("recipeId", recipe.getId()); // Pass the recipe ID to ViewRecipe
            startActivity(intent);
        });

        // Set the adapter to the RecyclerView
        favouriteRecycle.setAdapter(recipeAdapter);

        // Initialize the noFavorites TextView
        noFavorites=view.findViewById(R.id.tv_no_favorites);

        // Fetch favorite recipes
        fetchFavoriteRecipes();

        return view;
    }

    private void fetchFavoriteRecipes() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favorites");

        // Fetch the user's favorite recipes
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteRecipeList.clear(); // Clear the list before adding items to the favorite list
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    String recipeId = recipeSnapshot.getKey(); // Get the recipe ID
                    // Fetch recipe details using the recipe ID
                    fetchRecipeDetails(recipeId);
                }

                // Check if the favorite list is empty
//                if (favoriteRecipeList.isEmpty()) {
//                    noFavorites.setVisibility(View.VISIBLE); // Show message when no favorites
//                    favouriteRecycle.setVisibility(View.GONE); // Hide the RecyclerView
//                } else {
//                    noFavorites.setVisibility(View.GONE); // Hide message when there are favorites
//                    favouriteRecycle.setVisibility(View.VISIBLE); // Show the RecyclerView
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Favorite", "Error fetching favorites: " + error.getMessage());
                Toast.makeText(getContext(), "Error fetching favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRecipeDetails(String recipeId) {
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId);

        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String time = dataSnapshot.child("time").getValue(String.class);
                    String category = dataSnapshot.child("category").getValue(String.class);
                    String imageUrl = dataSnapshot.child("image").getValue(String.class);

                    // Create a RecipeCard object and add it to the list
                    RecipeCard recipeCard = new RecipeCard(recipeId, name, description, time, category, imageUrl);
                    favoriteRecipeList.add(recipeCard);
                    recipeAdapter.notifyDataSetChanged(); // Notify the adapter of data changes
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Favorite", "Error fetching recipe details: " + error.getMessage());
            }
        });
    }
}
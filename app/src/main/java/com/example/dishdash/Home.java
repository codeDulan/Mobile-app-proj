package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    private RecyclerView recyclerView;
    private RecipeCardAdapter recipeAdapter;
    private SearchView searchView;
    private List<RecipeCard> recipeList;
    private List<RecipeCard> allRecipes; // To hold all recipes
    private DatabaseReference databaseReference;
    private ImageButton btnBreakfast, btnLunch, btnDinner, btnIndian, btnChinese, btnItalian, btnSoups, btnDesserts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchView = view.findViewById(R.id.search_bar);

        recyclerView = view.findViewById(R.id.homeScreen_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeCardAdapter(recipeList, (RecipeCard recipe) -> {
            Intent intent = new Intent(getActivity(), ViewRecipe.class);
            intent.putExtra("recipeId", recipe.getId()); // Pass the recipe ID to ViewRecipe
            startActivity(intent);
        });

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(recipeAdapter);

        // Listen for search input and update RecyclerView accordingly
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when the user submits the search
                filterRecipes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the user types
                filterRecipes(newText);
                return false;
            }
        });

        // Get a reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allRecipes = new ArrayList<>();
                recipeList.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    // Fetch each field from the database
                    String id = recipeSnapshot.getKey();  // Get the recipe ID from the key
                    String name = recipeSnapshot.child("name").getValue(String.class);
                    String description = recipeSnapshot.child("description").getValue(String.class);
                    String time = recipeSnapshot.child("time").getValue(String.class);
                    String category = recipeSnapshot.child("category").getValue(String.class);
                    String imageUrl = recipeSnapshot.child("image").getValue(String.class);

                    // Create the RecipeCard object and add it to the list
                    recipeList.add(new RecipeCard(id, name, description, time, category, imageUrl));
                    allRecipes.add(new RecipeCard(id, name, description, time, category, imageUrl));
                }
                // Notify adapter about data changes
                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        // Initializing the buttons for categories
        btnBreakfast = view.findViewById(R.id.btn_breakfast);
        btnBreakfast.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryWindow.class);
            intent.putExtra("category", "breakfast"); // Pass the category name to CategoryWindow
            startActivity(intent);
        });

        btnLunch = view.findViewById(R.id.btn_lunch);
        btnLunch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryWindow.class);
            intent.putExtra("category", "lunch");
            startActivity(intent);
        });

        btnDinner = view.findViewById(R.id.btn_dinner);
        btnDinner.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryWindow.class);
            intent.putExtra("category", "dinner");
            startActivity(intent);
        });

        btnIndian = view.findViewById(R.id.btn_indian);
        btnIndian.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryWindow.class);
            intent.putExtra("category", "indian");
            startActivity(intent);
        });

        btnChinese = view.findViewById(R.id.btn_chinese);
        btnChinese.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryWindow.class);
            intent.putExtra("category", "chinese");
            startActivity(intent);
        });

        btnItalian = view.findViewById(R.id.btn_italian);
        btnItalian.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryWindow.class);
            intent.putExtra("category", "italian");
            startActivity(intent);
        });

        btnSoups = view.findViewById(R.id.btn_soups);
        btnSoups.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryWindow.class);
            intent.putExtra("category", "soups");
            startActivity(intent);
        });

        btnDesserts = view.findViewById(R.id.btn_desserts);
        btnDesserts.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryWindow.class);
            intent.putExtra("category", "desserts");
            startActivity(intent);
        });

        return view;

    }

    private void filterRecipes(String query) {
        List<RecipeCard> filteredList = new ArrayList<>();

        // If the query is empty, reload all recipes
        if (query.isEmpty()) {
            // Reload all recipes when the search field is cleared
            filteredList.addAll(allRecipes);  // Use allRecipes to reset the list
        } else {
            for (RecipeCard recipe : allRecipes) {  // Loop through all recipes
                // Perform a case-insensitive search
                if (recipe.getName().toLowerCase().contains(query.toLowerCase()) ||
                        recipe.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(recipe);
                }
            }
        }

        // Update the adapter with the filtered or full list
        recipeAdapter.updateRecipeList(filteredList);
    }





}
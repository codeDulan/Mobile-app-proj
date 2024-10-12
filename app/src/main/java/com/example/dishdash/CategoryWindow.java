package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryWindow extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeCardAdapter recipeAdapter;
    private List<RecipeCard> recipeList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_window);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button logic
        ImageButton backButton = findViewById(R.id.btn_back_category);
        backButton.setOnClickListener(v -> finish());

        // Retrieve the selected category from the Intent
        String category = getIntent().getStringExtra("category");

        // Update the TextView with the selected category
        TextView categoryTitle = findViewById(R.id.lbl_category_categorywindow);
        categoryTitle.setText(category);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.categoryWindow_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the recipe list and adapter
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeCardAdapter(recipeList, (RecipeCard recipe) -> {
            // Handling recipe click
            Intent intent = new Intent(CategoryWindow.this, ViewRecipe.class);
            intent.putExtra("recipeId", recipe.getId()); // Pass the recipe ID to ViewRecipe
            startActivity(intent);
        });

        recyclerView.setAdapter(recipeAdapter);

        // Get a reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        // Fetch recipes for the selected category
        fetchCategoryRecipes(category);
    }

    private void fetchCategoryRecipes(String category) {
        databaseReference.orderByChild("category").equalTo(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    String id = recipeSnapshot.getKey();
                    String name = recipeSnapshot.child("name").getValue(String.class);
                    String description = recipeSnapshot.child("description").getValue(String.class);
                    String time = recipeSnapshot.child("time").getValue(String.class);
                    String imageUrl = recipeSnapshot.child("image").getValue(String.class);

                    // Create RecipeCard object and add to the list
                    recipeList.add(new RecipeCard(id, name, description, time, category, imageUrl));
                }
                // Notify adapter about data changes
                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
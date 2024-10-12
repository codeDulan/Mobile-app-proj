package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
    private List<RecipeCard> recipeList;
    private DatabaseReference databaseReference;
    private ImageButton btnBreakfast, btnLunch, btnDinner, btnIndian, btnChinese, btnItalian, btnSoups, btnDesserts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        // Get a reference to the Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
}
package com.example.dishdash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.homeScreen_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeCardAdapter(recipeList, recipe -> {
            // You can handle click events here if necessary
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
                    String name = recipeSnapshot.child("name").getValue(String.class);
                    String description = recipeSnapshot.child("description").getValue(String.class);
                    String time = recipeSnapshot.child("time").getValue(String.class);
                    String category = recipeSnapshot.child("category").getValue(String.class);
                    String imageUrl = recipeSnapshot.child("image").getValue(String.class); // Image URL

                    // Add the RecipeCard object to the list
                    recipeList.add(new RecipeCard(name, description, time, category, imageUrl));
                }
                // Notify adapter about data changes
                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        return view;

    }
}
package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    private RecyclerView recyclerView;
    private RecipeCardAdapter recipeAdapter;
    private List<RecipeCard> recipeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.homeScreen_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setHasFixedSize(true);

        // Sample data
        recipeList = new ArrayList<>();
        recipeList.add(new RecipeCard("Japanese Noodles", "Western Province, Main Course", "10 Minutes", "Dinner",R.drawable.img_recipe_2));
        recipeList.add(new RecipeCard("Chinese Fried Rice", "Chinese, Main Course", "15 Minutes", "Lunch",R.drawable.img_recipe_3));
        recipeList.add(new RecipeCard("Italian Pasta", "Italian, Main Course", "20 Minutes", "Dinner",R.drawable.img_recipe_4));
        recipeList.add(new RecipeCard("Mexican Tacos", "Mexican, Main Course", "5 Minutes", "Breakfast",R.drawable.img_recipe_5));
        recipeList.add(new RecipeCard("Indian Curry", "Indian, Main Course", "30 Minutes", "Dinner",R.drawable.img_recipe_6));

        // Initialize the adapter with a click listener
        recipeAdapter = new RecipeCardAdapter(recipeList, new RecipeCardAdapter.OnRecipeClickListener() {
            @Override
            public void onRecipeClick(RecipeCard recipe) {
                // Intent to navigate to the ViewRecipe activity
                Intent intent = new Intent(getActivity(), ViewRecipe.class);
                startActivity(intent);
            }
        });

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(recipeAdapter);

        return view;
    }
}

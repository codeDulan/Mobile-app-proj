package com.example.dishdash;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapterTL extends RecyclerView.Adapter<RecipeAdapterTL.RecipeViewHolder> {

    private final List<RecipeTL> recipeList;
    private Context context;

    // Constructor accepting the list of recipes and the context
    public RecipeAdapterTL(List<RecipeTL> recipeList, Context context) {  // Context added to constructor
        this.recipeList = recipeList;
        this.context = context;  // Assign the passed context
    }

    // ViewHolder class
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView clockimg;
        ImageView recipePhoto;
        TextView recipeName;
        TextView recipeDetail;
        TextView timetext;
        TextView editbtn;
        TextView deletebtn;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipePhoto = itemView.findViewById(R.id.recipePhoto);
            recipeName = itemView.findViewById(R.id.recipeName);
            recipeDetail = itemView.findViewById(R.id.recipeDetail);
            clockimg = itemView.findViewById(R.id.clockimg);
            timetext = itemView.findViewById(R.id.timetext);
            editbtn = itemView.findViewById(R.id.editbtn);
            deletebtn = itemView.findViewById(R.id.deletebtn);
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipes, parent, false);  // Ensuring the layout file is correctly named
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        // Get the current recipe
        RecipeTL recipe = recipeList.get(position);

        // Bind data to the views
        holder.recipeName.setText(recipe.getName());
        holder.recipeDetail.setText(recipe.getDetailsRecipe());
        holder.timetext.setText(recipe.getTimeP());

        // Load the recipe image using Glide
        Glide.with(holder.recipePhoto.getContext())
                .load(recipe.getImageUrl())  // URL of the image to load
                .into(holder.recipePhoto);  // Load into ImageView

        // Load the clock image
        Glide.with(holder.clockimg.getContext())
                .load(recipe.getImageclck())  // URL of the image to load
                .into(holder.clockimg);

        holder.recipePhoto.setOnClickListener(v -> {
                    // Create an intent to switch to ViewRecipe activity
                    Intent intent = new Intent(context, ViewRecipe2.class);

            intent.putExtra("recipeName", recipe.getName());
            intent.putExtra("recipeDetail", recipe.getDetailsRecipe());
            intent.putExtra("recipeImage", recipe.getImageUrl());
            intent.putExtra("recipeTime", recipe.getTimeP());

            context.startActivity(intent);
        });

        // Set the recipe as a tag for the Edit and Delete buttons to identify the clicked item
        holder.editbtn.setTag(recipe);
        holder.deletebtn.setTag(recipe);

        // Handle Edit button click
        holder.editbtn.setOnClickListener(v -> {
            // Create an intent to switch to the activity
            Intent intent = new Intent(context, editekk.class);

            intent.putExtra("recipeName", recipe.getName());
            intent.putExtra("recipeDetail", recipe.getDetailsRecipe());

            context.startActivity(intent);  // Start the activity
        });

        // Handle Delete button click
        holder.deletebtn.setOnClickListener(v -> {
            // Handle delete logic
            Intent intent = new Intent(context, deleteRecipeekk.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList != null ? recipeList.size() : 0;  // Safeguard against null list
    }
}

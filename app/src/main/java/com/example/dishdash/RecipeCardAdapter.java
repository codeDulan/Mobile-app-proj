package com.example.dishdash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeViewHolder> {

    private List<RecipeCard> recipeList;
    private OnRecipeClickListener listener;

    public RecipeCardAdapter(List<RecipeCard> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeCard recipe = recipeList.get(position);

        // Use Glide to load the image from URL
        Glide.with(holder.itemView.getContext())
                .load(recipe.getImageURL()) // Get the image URL string
                .into(holder.recipeImage); // Load into ImageView

        holder.recipeName.setText(recipe.getName());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeDuration.setText(recipe.getDuration());
        holder.recipeMeal1.setText(recipe.getMealType());

        // Set click listener for the recipe card
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName, recipeDescription, recipeDuration, recipeMeal1;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_img);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeDescription = itemView.findViewById(R.id.recipe_desc);
            recipeDuration = itemView.findViewById(R.id.recipe_time);
            recipeMeal1 = itemView.findViewById(R.id.recipe_meal1_lbl);
        }
    }

    public void updateRecipeList(List<RecipeCard> newRecipeList) {
        recipeList.clear();
        recipeList.addAll(newRecipeList);
        notifyDataSetChanged(); // Refresh the RecyclerView with the updated data
    }


    // The interface for handling clicks
    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeCard recipe);
    }
}

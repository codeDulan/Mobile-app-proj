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

        // Use Glide to load the image from URL with placeholder and error handling
        if (recipe.getImageURL() != null && !recipe.getImageURL().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(recipe.getImageURL())
                    .placeholder(R.drawable.img_placeholder) // Show placeholder while loading
                    .error(R.drawable.img_error) // Show this if there's an error
                    .into(holder.recipeImage);
        } else {
            // In case image URL is missing, show a default image
            holder.recipeImage.setImageResource(R.drawable.img_default);
        }

        // Set other data
        holder.recipeName.setText(recipe.getName());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeDuration.setText(recipe.getDuration());
        holder.recipeMeal.setText(recipe.getMealType());

        // Set click listener for the recipe card
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName, recipeDescription, recipeDuration, recipeMeal;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_img);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeDescription = itemView.findViewById(R.id.recipe_desc);
            recipeDuration = itemView.findViewById(R.id.recipe_time);
            recipeMeal = itemView.findViewById(R.id.recipe_meal1_lbl);
        }
    }

    // The interface for handling clicks
    public interface OnRecipeClickListener {
        void onRecipeClick(RecipeCard recipe);
    }
}

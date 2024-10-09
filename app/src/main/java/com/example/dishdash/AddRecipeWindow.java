package com.example.dishdash;

import static java.lang.System.currentTimeMillis;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dishdash.databinding.ActivityAddRecipeWindowBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddRecipeWindow extends AppCompatActivity {

    ActivityAddRecipeWindowBinding binding;
    Spinner select;
    private boolean isImageSelected = false;
    private Bitmap bitmap;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeWindowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadCategories();

        binding.publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getData();
            }
        });

        binding.resipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
    }

    private void loadCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("categories");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    // Creating a list to store the categories
                    List<String> categoriesList = new ArrayList<>();
                    categoriesList.add("Select a category"); // Default option

                    // Iterate through the categories in Firebase
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String category = dataSnapshot.getValue(String.class);
                        categoriesList.add(category);
                    }

                    // Setting the categories to the spinner
                    ArrayAdapter<String> myAdapter = new ArrayAdapter<>(AddRecipeWindow.this, android.R.layout.simple_spinner_item, categoriesList);
                    myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    select = findViewById(R.id.static_spinner);
                    select.setAdapter(myAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddRecipeWindow.this, "Error loading categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImage() {

        PickImageDialog.build(new PickSetup()).show(AddRecipeWindow.this).setOnPickResult(r -> {
            Log.e("ProfileWindow", "onPickResult: " + r.getUri());
            binding.resipeImage.setImageBitmap(r.getBitmap());
            bitmap = r.getBitmap();
            binding.resipeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            isImageSelected = true;

        }).setOnPickCancel(() -> Toast.makeText(AddRecipeWindow.this, "Pick Cancelled", Toast.LENGTH_SHORT).show());

    }

    private void getData() {
        String title = binding.recipName.getText().toString();
        String description = binding.description.getText().toString();
        String time = binding.recipeTime.getText().toString();
        String ingredients = binding.ingredients.getText().toString();
        String instructions = binding.instructions.getText().toString();
        String category = select.getSelectedItem().toString();

        if (title.isEmpty()) {
            binding.recipName.setError("Please enter title");
        } else if (description.isEmpty()) {
            binding.description.setError("Please enter description");
        } else if (time.isEmpty()) {
            binding.recipeTime.setError("Please enter time");
        } else if (ingredients.isEmpty()) {
            binding.ingredients.setError("Please enter ingredients");
        } else if (instructions.isEmpty()) {
            binding.instructions.setError("Please enter instructions");

        } else if (category.equals("Select a category")) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();

        } else if (!isImageSelected) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();

        } else {

            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading recipe...");
            dialog.setCancelable(false);
            dialog.show();

            Recipe recipe = new Recipe(title, description, time, ingredients, instructions, "", FirebaseAuth.getInstance().getUid(), category);

            uploadImage(recipe);

        }
    }

    private String uploadImage(Recipe recipe) {

        final String[] url = {""};

        String id = currentTimeMillis() + "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/" + id + "_recipe.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageRef.putBytes(data);

        uploadTask.continueWithTask(task -> {

            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Getting the download URL
            return storageRef.getDownloadUrl();

        }).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();


                url[0] = downloadUri.toString();
                Toast.makeText(AddRecipeWindow.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                saveDataInDatabase(recipe, url[0]);


            } else {
                Toast.makeText(AddRecipeWindow.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Log.e("DatabaseError", "Error uploading image:");
            }
        });
        return url[0];
    }

    private void saveDataInDatabase(Recipe recipe, String url) {

        recipe.setImage(url);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("recipes");
        String id = reference.push().getKey();
        recipe.setId(id);
        if (id != null) {
            reference.child(id).setValue(recipe).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(this, "Error adding recipe", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
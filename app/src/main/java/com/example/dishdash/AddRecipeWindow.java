package com.example.dishdash;

import static java.lang.System.currentTimeMillis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.media.MediaMetadataRetriever;


public class AddRecipeWindow extends AppCompatActivity {

    ActivityAddRecipeWindowBinding binding;
    Spinner select;
    private boolean isImageSelected = false;
    private boolean isVideoUploaded = false;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    private static final int PICK_VIDEO_REQUEST = 2;
    private String uploadedImageUrl = null;
    private String uploadedVideoUrl = null;
    private Recipe recipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeWindowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadCategories();

        binding.publishBtn.setOnClickListener(view -> {
            if (!isVideoUploaded) {
            Toast.makeText(AddRecipeWindow.this, "Please upload a video first", Toast.LENGTH_SHORT).show();
        } else {
            getData();
        }
        });

        binding.resipeVideo.setOnClickListener(view -> pickVideo());

        binding.resipeImage.setOnClickListener(view -> pickImage());
    }

    private void loadCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("categories");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    List<String> categoriesList = new ArrayList<>();
                    categoriesList.add("Select a category");

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String category = dataSnapshot.getValue(String.class);
                        categoriesList.add(category);
                    }

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

            recipe = new Recipe(title, description, time, ingredients, instructions, "", FirebaseAuth.getInstance().getUid(), category, "");

            uploadImage(recipe);
        }
    }

    private void uploadImage(Recipe recipe) {
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
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploadedImageUrl = task.getResult().toString();
                checkAndSaveData(recipe);
            } else {
                Toast.makeText(AddRecipeWindow.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedVideoUri = data.getData();
            binding.resipeVideo.setImageURI(selectedVideoUri);

            // Display the thumbnail of the video
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, selectedVideoUri);

            // Get a frame at time 0 (the start of the video) to use as a thumbnail
            Bitmap thumbnail = retriever.getFrameAtTime(0);

            // Set the thumbnail to the ImageView
            binding.resipeVideo.setImageBitmap(thumbnail);
            binding.resipeVideo.setScaleType(ImageView.ScaleType.CENTER_CROP);


            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            uploadVideo(selectedVideoUri);
        }
    }

    private void uploadVideo(Uri videoUri) {
        String id = currentTimeMillis() + "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("videos/" + id + "_recipe.mp4");

        // Disable the publish button during video upload
        binding.publishBtn.setEnabled(false);

        storageRef.putFile(videoUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploadedVideoUrl = task.getResult().toString();
                isVideoUploaded = true;

                // Enable the publish button when the video is uploaded
                binding.publishBtn.setEnabled(true);

                checkAndSaveData(recipe);
            } else {
                Toast.makeText(AddRecipeWindow.this, "Error uploading video", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void checkAndSaveData(Recipe recipe) {
        if (uploadedImageUrl != null && uploadedVideoUrl != null) {
            saveDataInDatabase(recipe, uploadedImageUrl, uploadedVideoUrl);
        }
    }

    private void saveDataInDatabase(Recipe recipe, String imageUrl, String videoUrl) {
        recipe.setImage(imageUrl);
        recipe.setVideoUrl(videoUrl);

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

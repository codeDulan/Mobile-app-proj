package com.example.dishdash;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.dishdash.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Profile extends Fragment implements IPickResult {

    private FragmentProfileBinding binding;
    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Load profile data
        loadProfile();

        // Handle profile image change button click
        binding.profilePicture.setOnClickListener(v -> {
            PickImageDialog.build(new PickSetup()).show(requireActivity());
        });

        // Set up the RecyclerView for recipes
        setupRecyclerView();

        // Handle add recipe button click
        binding.btnAddRecipe.setOnClickListener(view -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), AddRecipeWindow.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Activity is null", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void loadProfile() {
        // Load user data from Firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user = snapshot.getValue(User.class);
                            if (user != null) {
                                binding.userNameid.setText(user.getName());
                                

                                // Load user image
                                if (user.getImage() != null) {
                                    Glide.with(requireContext())
                                            .load(user.getImage())
                                            .centerCrop()
                                            .placeholder(R.mipmap.ic_launcher)
                                            .into(binding.profilePicture);
                                }
                            }
                        } else {
                            Log.e("DatabaseError", "No data found in snapshot");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DatabaseError", "Error fetching user data: " + error.getMessage());
                    }
                });
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            binding.profilePicture.setImageBitmap(r.getBitmap());
            uploadImage(r.getBitmap());
        } else {
            Toast.makeText(getContext(), r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImage(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/" + FirebaseAuth.getInstance().getUid() + "image.jpg");

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
                Uri downloadUri = task.getResult();
                user.setImage(Objects.requireNonNull(downloadUri).toString());

                // Save updated user data to Firebase
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                reference.child(FirebaseAuth.getInstance().getUid()).setValue(user);

                Toast.makeText(getContext(), "Profile image updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("DatabaseError", "Error uploading image");
            }
        });
    }

    private void setupRecyclerView() {
        // Initialize your data source
        List<RecipeTL> recipeList = getRecipeList();

        // Initialize the adapter
        RecipeAdapterTL adapter = new RecipeAdapterTL(recipeList, requireContext());

        // Set the adapter and layout manager to the RecyclerView
        binding.lookrecipe.setAdapter(adapter);
        binding.lookrecipe.setLayoutManager(new LinearLayoutManager(getContext())); // Default vertical layout
    }

    private List<RecipeTL> getRecipeList() {
        // Creating dummy recipe data
        List<RecipeTL> recipes = new ArrayList<>();
        recipes.add(new RecipeTL("Spaghetti", "Classic pasta", "https://images.immediate.co.uk/production/volatile/sites/30/2018/07/RedPepperAnchovySpaghetti-copy-1dec261.jpg?quality=90&webp=true&resize=300,272", R.drawable.clock, "30 minutes", R.id.editbtn, R.id.deletebtn));
        recipes.add(new RecipeTL("Chicken Curry", "Spicy curry", "https://img.taste.com.au/XEOuZA8K/w643-h428-cfill-q90/taste/2024/03/5-ingredient-turbo-charged-spaghetti-recipe-196959-1.jpg", R.drawable.clock, "45 minutes", R.id.editbtn, R.id.deletebtn));
        recipes.add(new RecipeTL("Beef Stroganoff", "Rich and creamy", "https://example.com/beef_stroganoff.jpg", R.drawable.clock, "40 minutes", R.id.editbtn, R.id.deletebtn));
        recipes.add(new RecipeTL("Tacos", "Mexican delight", "https://example.com/tacos.jpg", R.drawable.clock, "25 minutes", R.id.editbtn, R.id.deletebtn));
        return recipes;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up the binding to avoid memory leaks
        binding = null;
    }
}

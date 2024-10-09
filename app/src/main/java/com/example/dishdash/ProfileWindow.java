package com.example.dishdash;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dishdash.databinding.ActivityProfileWindowBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
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
import java.util.BitSet;
import java.util.Objects;

public class ProfileWindow extends AppCompatActivity implements IPickResult {

    // Declare the binding variable
    private ActivityProfileWindowBinding binding;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing the binding variable
        binding = ActivityProfileWindowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Correct the Firebase reference to the "users" node
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))  // Corrected this line
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {  // Check if data exists
                            user = snapshot.getValue(User.class);  // Map snapshot to User class
                            if (user != null) {
                                // Setting the retrieved user data into the UI elements
                                binding.username.setText(user.getName());
                                binding.email.setText(user.getEmail());

                                // Loading user image URL using Glide
                                if (user.getImage() != null) {
                                    Glide
                                            .with(ProfileWindow.this)  // Use the context of the current Activity
                                            .load(user.getImage())     // Load the image URL
                                            .centerCrop()              // Optional: center crop the image
                                            .placeholder(R.mipmap.ic_launcher)  // Default image while loading
                                            .into(binding.imgProfile);  // Load into the ImageView
                                }
                            } else {
                                Log.e("DatabaseError", "User data is null");
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

//change profile image====================================================
        binding.settingbtn.setOnClickListener(v -> {
            PickImageDialog.build(new PickSetup()).show(this);
        });
    }

    public void onPickResult(PickResult r){
        if (r.getError() == null){

            binding.imgProfile.setImageBitmap(r.getBitmap());
            uploadImage(r.getBitmap());

        }else {
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadImage(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images/" + FirebaseAuth.getInstance().getUid()+"image.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageRef.putBytes(data);

        uploadTask.continueWithTask(task -> {

            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return storageRef.getDownloadUrl();

        }).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    user.setImage(Objects.requireNonNull(downloadUri).toString());
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(user);

                } else {
                    Log.e("DatabaseError", "Error uploading image:");
                }
        });
    }
}


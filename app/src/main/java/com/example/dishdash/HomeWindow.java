package com.example.dishdash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeWindow extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    FirebaseFirestore db;
    GoogleSignInClient gsc;
    Button profile;
    Button add;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_window);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.textView3);
        profile = findViewById(R.id.profile_btn);
        add = findViewById(R.id.add_button);
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        if (user == null) {
            Intent intent = new Intent(HomeWindow.this, LoginWindow.class);
            startActivity(intent);
            finish();
        } else {
            String userId = user.getUid(); // Get the current user's ID
            DocumentReference docRef = db.collection("users").document(userId);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String firstName = document.getString("firstName"); // Get the firstName field
                        textView.setText("Welcome, " + firstName + "!");
                    } else {
                        textView.setText("User data not found.");
                    }
                } else {
                    textView.setText("Failed to fetch user data.");
                }
            });
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeWindow.this, ProfileWindow.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(view -> {
            auth.signOut();
            if (gsc != null) {
                gsc.signOut().addOnCompleteListener(task -> {
                    Intent intent = new Intent(HomeWindow.this, LoginWindow.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                Intent intent = new Intent(HomeWindow.this, LoginWindow.class);
                startActivity(intent);
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeWindow.this, AddRecipeWindow.class);
                startActivity(intent);

            }
        });
    }
}
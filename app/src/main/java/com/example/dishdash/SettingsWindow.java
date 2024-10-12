package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsWindow extends AppCompatActivity {

    FirebaseAuth auth;  // Firebase Authentication
    GoogleSignInClient gsc;  // Google Sign-In client
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings_window);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);  // Initialize Google sign-in client

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            logout = findViewById(R.id.btn_logout);  // Bind the logout button

            logout.setOnClickListener(view -> {
                // Sign out from Firebase
                auth.signOut();

                // Sign out from Google
                gsc.signOut().addOnCompleteListener(task -> {
                    // Redirect to LoginWindow after signing out
                    Intent intent = new Intent(SettingsWindow.this, LoginWindow.class);
                    startActivity(intent);
                    finish();
                });
            });

            return insets;
        });
    }
}

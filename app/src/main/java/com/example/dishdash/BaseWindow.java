package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BaseWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_base_window);

        Button startCookingButton = findViewById(R.id.startCookingButton);
        startCookingButton.setOnClickListener(view -> {
            // Start the LoginWindow activity when the button is clicked
            Intent intent = new Intent(BaseWindow.this, LoginWindow.class);
            startActivity(intent);
        });
    }
}
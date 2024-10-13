package com.example.dishdash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle window insets (not relevant to dynamic links, keep as is)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Capture Firebase Dynamic Links
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;

                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        Log.d("MainActivity", "Dynamic Link: " + deepLink.toString());

                        // Check if the deep link contains the recipeId parameter
                        if (deepLink != null && deepLink.getQueryParameter("recipeId") != null) {
                            String recipeId = deepLink.getQueryParameter("recipeId");

                            // If recipeId exists, open the ViewRecipe activity
                            Intent intent = new Intent(MainActivity.this, ViewRecipe.class);
                            intent.putExtra("recipeId", recipeId);
                            startActivity(intent);
                            finish(); // Close the splash screen activity
                            return; // Stop further execution as we are navigating away
                        }
                    }

                    // If no dynamic link, proceed with the normal flow after 3 seconds
                    navigateToBaseWindowWithDelay();
                })
                .addOnFailureListener(this, e -> {
                    Log.w("MainActivity", "Error getting dynamic link", e);
                    // If there's an error, continue with normal flow after 3 seconds
                    navigateToBaseWindowWithDelay();
                });
    }

    // Function to navigate to the BaseWindow after a delay
    private void navigateToBaseWindowWithDelay() {
        new Handler().postDelayed(() -> {
            // Move to the BaseWindow
            Intent intent = new Intent(MainActivity.this, BaseWindow.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fast_fade_in, R.anim.fast_fade_out); // custom fade transition
            finish(); // Close the splash activity
        }, 3000);
    }
}

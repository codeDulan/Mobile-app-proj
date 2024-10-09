package com.example.dishdash;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.dishdash.databinding.ActivityHomeWindow2Binding;

public class HomeWindow2 extends AppCompatActivity {

    ActivityHomeWindow2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeWindow2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new Home());

        binding.navigationBar.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.navBar_Home) {
                replaceFragment(new Home());
            } else if (itemId == R.id.navBar_Favorites) {
                replaceFragment(new Favorite());
            } else if (itemId == R.id.navBar_Profile) {
                replaceFragment(new Profile());
            } else {
                return false;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
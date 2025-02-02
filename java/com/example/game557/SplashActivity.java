package com.example.game557;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in by checking SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("User  Session", MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", -1); // Default value is -1 if not found

        Intent intent;
        if (userID != -1) {
            // User is logged in, redirect to Main Menu
            intent = new Intent(SplashActivity.this, MainMenuActivity.class);
        } else {
            // User is not logged in, redirect to Login Activity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Close SplashActivity
    }
}
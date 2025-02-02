package com.example.game557;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    TextView tvTitle, tvWelcomeMessage; // Added a TextView for the welcome message
    Button btnPlayGame, btnInstructions, btnLeaderboard, btnCredit, btnManageUsers, btnLogout;
    int userID; // Variable to hold the userID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Retrieve userID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("User  Session", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1); // Default value is -1 if not found

        // Initialize views
        tvTitle = findViewById(R.id.tvTitle);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage); // Initialize the welcome message TextView
        btnPlayGame = findViewById(R.id.btnPlayGame);
        btnInstructions = findViewById(R.id.btnInstructions);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnCredit = findViewById(R.id.btnCredit);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnLogout = findViewById(R.id.btnLogout);

        // Load fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Apply animation
        tvTitle.startAnimation(fadeIn);
        btnPlayGame.startAnimation(fadeIn);
        btnInstructions.startAnimation(fadeIn);
        btnLeaderboard.startAnimation(fadeIn);
        btnCredit.startAnimation(fadeIn);
        btnManageUsers.startAnimation(fadeIn);
        btnLogout.startAnimation(fadeIn);

        // Retrieve username based on userID
        DatabaseHelper db = new DatabaseHelper(this);
        String username = db.getUsernameById(userID);
        if (username != null) {
            tvWelcomeMessage.setText("Choose your selection, " + username + "!"); // Set the welcome message
        } else {
            tvWelcomeMessage.setText("Choose your selection!"); // Fallback message
        }

        // Check if user is logged in
        if (userID == -1) {
            btnLogout.setText("Login");
        }
        else{
            btnLogout.setText("Logout");
        }


        // Set button click listeners
        btnPlayGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnInstructions.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, InstructionsActivity.class);
            startActivity(intent);
        });

        btnLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });

        btnCredit.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, CreditActivity.class);
            startActivity(intent);
        });

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, ManageUsersActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Clear the user session
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear(); // Clear all data
            editor.apply();

            Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // End the current activity so the user cannot return to MainMenuActivity with the back button
        });
    }
}
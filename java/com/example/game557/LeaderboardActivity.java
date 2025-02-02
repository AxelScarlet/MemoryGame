package com.example.game557;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    private LinearLayout leaderboardLayout;
    private DatabaseHelper dbHelper;
    private Button buttonRefresh;
    private Animation fadeInAnimation; // Animation variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboardLayout = findViewById(R.id.leaderboardLayout); // LinearLayout to display leaderboard
        dbHelper = new DatabaseHelper(this); // Initialize the DatabaseHelper
        buttonRefresh = findViewById(R.id.button_refresh); // Refresh button

        // Load fade-in animation
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        loadLeaderboardEntries(); // Load leaderboard entries on activity creation

        // Set up the refresh button
        buttonRefresh.setOnClickListener(v -> loadLeaderboardEntries());
    }

    private void loadLeaderboardEntries() {
        List<Map<String, String>> leaderboardEntries = dbHelper.getAllLeaderboardEntries();

        // Clear any existing entries in the LinearLayout
        leaderboardLayout.removeAllViews();

        // Check if the leaderboard is empty and display a message if necessary
        if (leaderboardEntries.isEmpty()) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No leaderboard data available");
            noDataText.setTextSize(18f);
            noDataText.setGravity(View.TEXT_ALIGNMENT_CENTER);
            leaderboardLayout.addView(noDataText);
        } else {
            // Manually add TextViews for each leaderboard entry
            for (Map<String, String> entry : leaderboardEntries) {
                String nickname = entry.get("nickname");
                String score = entry.get("score");
                String leaderboardText = nickname + " - Score: " + score;

                // Create a container for each entry
                LinearLayout entryContainer = new LinearLayout(this);
                entryContainer.setOrientation(LinearLayout.VERTICAL);
                entryContainer.setBackgroundResource(R.drawable.box_background); // Use the box background
                entryContainer.setPadding(12, 12, 12, 12);
                entryContainer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                entryContainer.setTag(0); // Add margin between entries

                TextView textView = new TextView(this);
                textView.setText(leaderboardText);
                textView.setTextSize(18f);
                textView.setPadding(10, 10, 10, 10);

                // Add the TextView to the entry container
                entryContainer.addView(textView);
                // Add the entry container to the leaderboard layout
                leaderboardLayout.addView(entryContainer);

                // Start the fade-in animation
                entryContainer.startAnimation(fadeInAnimation);
            }
        }
    }
}
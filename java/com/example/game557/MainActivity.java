package com.example.game557;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView nicknameListView;
    private Button buttonProceed, buttonUpdate;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper db;
    private String selectedNickname = ""; // Store the selected nickname
    private static final int REQUEST_CODE_UPDATE = 1; // Request code for update activity
    private int userID; // Variable to hold the userID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        nicknameListView = findViewById(R.id.nickname_list_view); // ListView to show the nicknames
        buttonProceed = findViewById(R.id.button_proceed); // Button to proceed to play the game
        buttonUpdate = findViewById(R.id.button_update); // Button to navigate to ManageUsersActivity
        db = new DatabaseHelper(this);

        // Retrieve userID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("User  Session", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1); // Default value is -1 if not found

        // Initialize ListView adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        nicknameListView.setAdapter(adapter);

        // Load the nicknames based on login status
        if (userID == -1) {
            // User is not logged in
            loadNicknames();
//            Toast.makeText(this,"Not Logged in User ID:" + userID, Toast.LENGTH_SHORT).show();
        } else {
            // User is logged in
//            Toast.makeText(this,"logged in User ID:" + userID, Toast.LENGTH_SHORT).show();
            loadNicknamesByID(userID);
        }

        // Set click listener to view nicknames and select one
        nicknameListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedNickname = adapter.getItem(position);
            if (selectedNickname != null) {
                Toast.makeText(this, "Selected: " + selectedNickname, Toast.LENGTH_SHORT).show();
            }
        });

        // Proceed to play the game with the selected nickname
        buttonProceed.setOnClickListener(v -> {
            if (selectedNickname.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please select a nickname", Toast.LENGTH_SHORT).show();
            } else {
                // Pass the selected nickname to the GameActivity
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("selected_nickname", selectedNickname);
                startActivity(intent);
            }
        });

        // Navigate to UpdateActivity to update nickname
        buttonUpdate.setOnClickListener(v -> {
            if (userID != -1) { // Only allow update if the user is logged in
                Intent intent = new Intent(MainActivity.this, ManageUsersActivity.class);
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            } else {
                Toast.makeText(this, "You must be logged in to update nicknames.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNicknames() {
        ArrayList<String> nicknames = db.getAllNicknames();
        if (nicknames.isEmpty()) {
            Toast.makeText(this, "No nicknames available", Toast.LENGTH_SHORT).show();
        } else {
            adapter.clear();
            adapter.addAll(nicknames);
            adapter.notifyDataSetChanged();
        }
    }

    private void loadNicknamesByID(int userID) {
        ArrayList<String> nicknames = db.getAllNicknamesByID(userID); // Get nicknames for the logged-in user

        if (nicknames.isEmpty()) {
            Toast.makeText(this, "No nicknames available for this user", Toast.LENGTH_SHORT).show();
        } else {
            adapter.clear();
            adapter.addAll(nicknames);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from ManageUsersActivity and if it's OK
        if (requestCode == REQUEST_CODE_UPDATE && resultCode == RESULT_OK) {
            // Refresh the nickname list after a new nickname is added
            loadNicknames();
        }
    }
}
package com.example.game557;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ManageUsersActivity extends AppCompatActivity {

    private EditText editName, editOldName, editNewName, searchNickname;
    private Button buttonInsert, buttonView, buttonDelete, buttonUpdate, buttonSearch;
    private DatabaseHelper db;
    private ListView nicknameListView; // ListView to display nicknames
    private ArrayAdapter<String> adapter; // Adapter for the ListView
    private ArrayList<String> nicknames; // List to hold nicknames
    private int userID; // Variable to hold the userID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // Initialize views
        editName = findViewById(R.id.edit_name);
        editOldName = findViewById(R.id.edit_old_name);
        editNewName = findViewById(R.id.edit_new_name);
        searchNickname = findViewById(R.id.searchNickname);
        buttonInsert = findViewById(R.id.button_insert);
        buttonView = findViewById(R.id.button_view);
        buttonDelete = findViewById(R.id.button_delete);
        buttonUpdate = findViewById(R.id.button_update);
        buttonSearch = findViewById(R.id.button_search);
        nicknameListView = findViewById(R.id.nickname_list); // Initialize ListView

        // Initialize DatabaseHelper
        db = new DatabaseHelper(this);

        // Retrieve userID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("User  Session", MODE_PRIVATE);
        userID = sharedPreferences.getInt("userID", -1); // Default value is -1 if not found

        // Check if user is logged in
        if (userID == -1) {
            Toast.makeText(this, "You must be logged in to manage nicknames.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if not logged in
            return;
        }
        else{
//            Toast.makeText(this," Logged in User ID:" + userID, Toast.LENGTH_SHORT).show();
        }

        // Initialize the nickname list
        nicknames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nicknames);
        nicknameListView.setAdapter(adapter);

        // Set button click listeners
        buttonInsert.setOnClickListener(v -> insertNickname());
        buttonView.setOnClickListener(v -> viewNicknames());
        buttonDelete.setOnClickListener(v -> deleteNickname());
        buttonUpdate.setOnClickListener(v -> updateNickname());
        buttonSearch.setOnClickListener(v -> searchNickname());
    }

    private void insertNickname() {
        String nickname = editName.getText().toString().trim();
        if (!nickname.isEmpty()) {
            boolean success = db.insertNickname(nickname, userID);
            if (success) {
                Toast.makeText(this, "Nickname added!", Toast.LENGTH_SHORT).show();
                editName.setText(""); // Clear input
                viewNicknames(); // Refresh the list
            } else {
                Toast.makeText(this, "Nickname already existed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a nickname", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewNicknames() {
        nicknames.clear(); // Clear the current list
        Cursor cursor = db.getAllNicknames(userID);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // Get the index of the nickname column
                    int nicknameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NICKNAME);

                    // Check if the index is valid
                    if (nicknameIndex != -1) {
                        String nickname = cursor.getString(nicknameIndex);
                        nicknames.add(nickname); // Add nickname to the list
                    } else {
                        Toast.makeText(this, "Nickname column not found", Toast.LENGTH_SHORT).show();
                    }
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "No nicknames found", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(this, "Cursor is null", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
    }

    private void deleteNickname() {
        String nickname = editName.getText().toString().trim();
        if (!nickname.isEmpty()) {
            boolean success = db.deleteNickname(nickname);
            if (success) {
                Toast.makeText(this, "Nickname deleted!", Toast.LENGTH_SHORT).show();
                editName.setText(""); // Clear input
                viewNicknames(); // Refresh the list
            } else {
                Toast.makeText(this, "Failed to delete nickname", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a nickname to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNickname() {
        String oldNickname = editOldName.getText().toString().trim();
        String newNickname = editNewName.getText().toString().trim();

        if (!oldNickname.isEmpty() && !newNickname.isEmpty()) {
            boolean success = db.updateNickname(oldNickname, newNickname);
            if (success) {
                Toast.makeText(this, "Nickname updated!", Toast.LENGTH_SHORT).show();
                editOldName.setText(""); // Clear input
                editNewName.setText(""); // Clear input
                viewNicknames(); // Refresh the list
            } else {
                Toast.makeText(this, "Failed to update nickname", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter both old and new nicknames", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchNickname() {
        String nickname = searchNickname.getText().toString().trim();
        if (!nickname.isEmpty()) {
            boolean exists = db.isNicknameExists(nickname);
            if (exists) {
                Toast.makeText(this, "Nickname found!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Nickname not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a nickname to search", Toast.LENGTH_SHORT).show();
        }
    }
}
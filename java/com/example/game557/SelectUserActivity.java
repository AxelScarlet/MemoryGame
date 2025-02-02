package com.example.game557;
// SelectUserActivity.java
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectUserActivity extends AppCompatActivity {

    ListView lvNicknames;
    DatabaseHelper db;
    ArrayList<String> nicknames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        lvNicknames = findViewById(R.id.lvNicknames);
        db = new DatabaseHelper(this);

        // Load nicknames from the database
        //nicknames = db.getAllNicknames(); // Returns a list of nicknames

        if (nicknames.isEmpty()) {
            Toast.makeText(this, "No nicknames available. Please create one.", Toast.LENGTH_SHORT).show();
            finish();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nicknames);
        lvNicknames.setAdapter(adapter);

        // Handle nickname selection
        lvNicknames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedNickname = nicknames.get(position);

                // Pass the selected nickname to the Game Activity
                Intent intent = new Intent(SelectUserActivity.this, GameActivity.class);
                intent.putExtra("SELECTED_NICKNAME", selectedNickname);
                startActivity(intent);
                finish();
            }
        });
    }
}

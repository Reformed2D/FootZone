package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecruitPlayersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlayersAdapter playersAdapter;
    private DatabaseHelper databaseHelper;
    private List<Player> playersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_players);

        recyclerView = findViewById(R.id.playersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHelper = new DatabaseHelper(this);
        playersList = new ArrayList<>();

        // Initialize adapter with empty list
        playersAdapter = new PlayersAdapter(playersList, position -> {
            // Handle player selection here
            Toast.makeText(this, "Selected player with position: " + position, Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(playersAdapter);

        loadPlayersWithoutTeam();
    }

    private void loadPlayersWithoutTeam() {
        Cursor cursor = databaseHelper.getPlayersWithoutTeam();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Player player = new Player();
                player.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_1)));
                player.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_2)));
                player.setPosition(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_7)));

                playersList.add(player);
            } while (cursor.moveToNext());

            playersAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
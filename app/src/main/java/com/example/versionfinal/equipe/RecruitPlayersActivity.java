package com.example.versionfinal.equipe;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.example.versionfinal.R;
import com.example.versionfinal.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RecruitPlayersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlayersAdapter playersAdapter;
    private DatabaseHelper databaseHelper;
    private List<Player> allPlayers;
    private List<Player> filteredPlayers;
    private EditText searchEditText;
    private ChipGroup positionFilterChipGroup;
    private String currentPositionFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_players);

        initializeViews();
        setupRecyclerView();
        setupSearchAndFilters();
        loadPlayersWithoutTeam();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.playersRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        positionFilterChipGroup = findViewById(R.id.positionFilterChipGroup);

        databaseHelper = new DatabaseHelper(this);
        allPlayers = new ArrayList<>();
        filteredPlayers = new ArrayList<>();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playersAdapter = new PlayersAdapter(filteredPlayers, position -> {
            Toast.makeText(this, "Selected player with position: " + position, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(playersAdapter);
    }

    private void setupSearchAndFilters() {
        // Setup search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPlayers(s.toString(), currentPositionFilter);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup position filters
        positionFilterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentPositionFilter = "";
            } else {
                Chip chip = findViewById(checkedIds.get(0));
                currentPositionFilter = chip.getText().toString();
                if (currentPositionFilter.equals("All")) {
                    currentPositionFilter = "";
                }
            }
            filterPlayers(searchEditText.getText().toString(), currentPositionFilter);
        });
    }

    private void filterPlayers(String searchQuery, String positionFilter) {
        filteredPlayers.clear();

        List<Player> tempList = allPlayers.stream()
                .filter(player ->
                        (searchQuery.isEmpty() ||
                                player.getUsername().toLowerCase().contains(searchQuery.toLowerCase())) &&
                                (positionFilter.isEmpty() ||
                                        player.getPosition().equalsIgnoreCase(positionFilter)))
                .collect(Collectors.toList());

        filteredPlayers.addAll(tempList);
        playersAdapter.notifyDataSetChanged();

        // Show/hide empty state
        if (filteredPlayers.isEmpty()) {
            findViewById(R.id.emptyStateLayout).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.emptyStateLayout).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadPlayersWithoutTeam() {
        Cursor cursor = databaseHelper.getPlayersWithoutTeam();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Player player = new Player();
                player.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_1)));
                player.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_2)));
                player.setPosition(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_7)));

                allPlayers.add(player);
            } while (cursor.moveToNext());

            filteredPlayers.addAll(allPlayers);
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
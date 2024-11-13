package com.example.versionfinal.terrain;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;

import java.util.List;

public class TerrainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TerrainAdapter adapter;
    private RecyclerView recyclerView;
    private EditText searchInput;
    private TextView noTerrainText;
    public static final int REQUEST_CODE_ADD_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terrain_activity);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupRecyclerView();
        setupSearchListener();
        loadTerrains();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view);
        searchInput = findViewById(R.id.search_input);
        noTerrainText = findViewById(R.id.no_terrain_text);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSearchListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = searchInput.getText().toString();
                if (!TextUtils.isEmpty(searchText)) {
                    List<Terrain> filteredList = dbHelper.searchTerrainsByName(searchText);
                    updateTerrainList(filteredList);
                } else {
                    loadTerrains();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadTerrains() {
        List<Terrain> terrainList = dbHelper.getAllTerrains();
        updateTerrainList(terrainList);
    }

    private void updateTerrainList(List<Terrain> terrainList) {
        if (terrainList.isEmpty()) {
            noTerrainText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noTerrainText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new TerrainAdapter(terrainList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    public void addTerrain(View view) {
        Intent intent = new Intent(this, AddEditTerrainActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadTerrains();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTerrains();
    }
}
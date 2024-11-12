package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReclamationsListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewReclamations;
    private ReclamationAdapter adapter;
    private ArrayList<Reclamation> reclamations;
    private DatabaseHelper dbHelper;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reclamati_list_activity);

        // Récupérer l'ID de l'utilisateur à partir de l'Intent
        userId = getIntent().getStringExtra("USER_ID");

        recyclerViewReclamations = findViewById(R.id.recyclerViewReclamations);
        recyclerViewReclamations.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);

        // Récupérer les réclamations pour l'utilisateur
        reclamations = dbHelper.getReclamationsForUser(userId);

        // Configurer l'adaptateur pour afficher les réclamations dans un RecyclerView
        adapter = new ReclamationAdapter(this, reclamations);
        recyclerViewReclamations.setAdapter(adapter);
    }
}

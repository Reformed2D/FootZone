package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReclamationsListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReclamationAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<HashMap<String, String>> reclamationList;
    private List<HashMap<String, String>> filteredReclamationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reclamati_list_activity);

        // Initialisation de la RecyclerView
        recyclerView = findViewById(R.id.reclamationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation de la base de données
        dbHelper = new DatabaseHelper(this);
        reclamationList = new ArrayList<>();
        filteredReclamationList = new ArrayList<>();

        try {
            // Récupération de toutes les réclamations
            reclamationList = dbHelper.getAllReclamations();
            filteredReclamationList.addAll(reclamationList); // Initialiser la liste filtrée

            if (reclamationList.isEmpty()) {
                Toast.makeText(this, "Aucune réclamation trouvée", Toast.LENGTH_SHORT).show();
                Log.d("ReclamationsListActivity", "La liste des réclamations est vide");
            }

            // Configuration de l'adaptateur
            adapter = new ReclamationAdapter(this, filteredReclamationList);
            recyclerView.setAdapter(adapter);

            // Configuration de la barre de recherche
            SearchView searchView = findViewById(R.id.searchView);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e("ReclamationsListActivity", "Erreur lors de la récupération des réclamations", e);
            Toast.makeText(this, "Erreur lors du chargement des réclamations", Toast.LENGTH_SHORT).show();
        }
    }

    // Fonction pour filtrer les réclamations
    private void filter(String query) {
        filteredReclamationList.clear();
        if (query.isEmpty()) {
            filteredReclamationList.addAll(reclamationList);
        } else {
            for (HashMap<String, String> reclamation : reclamationList) {
                if (reclamation.get("type").toLowerCase().contains(query.toLowerCase())) {
                    filteredReclamationList.add(reclamation);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
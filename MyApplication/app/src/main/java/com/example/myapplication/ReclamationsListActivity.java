package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;

public class ReclamationsListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReclamationAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reclamati_list_activity);

        // Initialisation de la RecyclerView
        recyclerView = findViewById(R.id.reclamationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation de la base de données
        dbHelper = new DatabaseHelper(this);

        try {
            // Récupération de toutes les réclamations
            List<HashMap<String, String>> reclamationList = dbHelper.getAllReclamations();

            if (reclamationList.isEmpty()) {
                Toast.makeText(this, "Aucune réclamation trouvée", Toast.LENGTH_SHORT).show();
                Log.d("ReclamationsListActivity", "La liste des réclamations est vide");
            }

            // Configuration de l'adaptateur
            adapter = new ReclamationAdapter(this, reclamationList);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("ReclamationsListActivity", "Erreur lors de la récupération des réclamations", e);
            Toast.makeText(this, "Erreur lors du chargement des réclamations", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
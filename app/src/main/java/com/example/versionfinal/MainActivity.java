package com.example.versionfinal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private GridView citiesGrid;
    private CityAdapter adapter;
    private TextInputEditText searchEditText;
    private TextView noResultsText;
    private TextInputLayout searchLayout;
    private Button locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        initializeViews();

        // Configuration des fonctionnalités
        setupSearchBar();
        setupLocationButton();
        setupGridClickListeners();
        setupAdapter();
    }

    private void initializeViews() {
        citiesGrid = findViewById(R.id.citiesGrid);
        searchLayout = findViewById(R.id.searchLayout);
        searchEditText = findViewById(R.id.searchEditText);
        locationButton = findViewById(R.id.locationButton);
        noResultsText = findViewById(R.id.noResultsText);
    }

    private void setupAdapter() {
        adapter = new CityAdapter(this);
        citiesGrid.setAdapter(adapter);
    }

    private void setupSearchBar() {
        // Configuration du TextWatcher pour la recherche en temps réel
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrer les résultats
                adapter.getFilter().filter(s, count1 -> {
                    // Afficher/masquer le message "Aucun résultat" si nécessaire
                    if (count1 == 0) {
                        showNoResults();
                    } else {
                        hideNoResults();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Gestion du clic sur la barre de recherche (optionnel car TextInputLayout gère déjà le focus)
        searchLayout.setOnClickListener(v -> searchEditText.requestFocus());
    }

    private void showNoResults() {
        if (noResultsText != null) {
            noResultsText.setVisibility(View.VISIBLE);
            citiesGrid.setVisibility(View.GONE);
        }
    }

    private void hideNoResults() {
        if (noResultsText != null) {
            noResultsText.setVisibility(View.GONE);
            citiesGrid.setVisibility(View.VISIBLE);
        }
    }

    private void setupLocationButton() {
        locationButton.setOnClickListener(v -> checkLocationPermission());
    }

    private void setupGridClickListeners() {
        citiesGrid.setOnItemClickListener((parent, view, position, id) -> {
            City city = (City) parent.getItemAtPosition(position);
            handleCitySelection(city);
        });
    }

    private void handleCitySelection(City city) {
        // Gérer la sélection d'une ville
        Toast.makeText(MainActivity.this,
                "Ville sélectionnée : " + city.getName(),
                Toast.LENGTH_SHORT).show();

        // Ici vous pouvez ajouter la logique pour ouvrir une nouvelle activité
        // ou afficher plus d'informations sur la ville sélectionnée
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission si elle n'est pas accordée
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            // Permission déjà accordée, obtenir la localisation
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée
                getLocation();
            } else {
                // Permission refusée
                Toast.makeText(this,
                        "La permission de localisation est nécessaire pour cette fonctionnalité",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getLocation() {
        // Implémenter la logique de géolocalisation
        Toast.makeText(this, "Recherche de votre position...", Toast.LENGTH_SHORT).show();

        // Ici, vous pouvez ajouter le code pour obtenir la position réelle
        // en utilisant LocationManager ou FusedLocationProviderClient
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir la liste des villes si nécessaire
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        // Si la recherche est active et contient du texte, effacer la recherche
        if (searchEditText != null && searchEditText.length() > 0) {
            searchEditText.setText("");
        } else {
            // Sinon, comportement normal du bouton retour
            super.onBackPressed();
        }
    }
}
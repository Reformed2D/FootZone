package com.example.versionfinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation du client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        // Gestion du clic sur la barre de recherche
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

        // Ouvrir Google Maps avec la recherche pour cette ville
        String cityQuery = "soccer+field+in+" + city.getName();
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + cityQuery);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback vers navigateur si Google Maps n'est pas installé
            String mapsUrl = "https://www.google.com/maps/search/" + cityQuery;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
            startActivity(browserIntent);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
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
                getLocation();
            } else {
                Toast.makeText(this,
                        "La permission de localisation est nécessaire pour cette fonctionnalité",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        openMapsWithLocation(location);
                    } else {
                        // Si la localisation n'est pas disponible, ouvrir Maps sans coordonnées
                        openMapsWithoutLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    // En cas d'erreur, ouvrir Maps sans coordonnées
                    openMapsWithoutLocation();
                });
    }

    private void openMapsWithLocation(Location location) {
        // Rechercher les terrains de foot autour de la position actuelle
        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," +
                location.getLongitude() +
                "?q=soccer+field");
        launchMapsIntent(gmmIntentUri);
    }

    private void openMapsWithoutLocation() {
        // Rechercher les terrains de foot sans position spécifique
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=soccer+field");
        launchMapsIntent(gmmIntentUri);
    }

    private void launchMapsIntent(Uri gmmIntentUri) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback vers navigateur si Google Maps n'est pas installé
            String mapsUrl = "https://www.google.com/maps/search/soccer+field";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
            startActivity(browserIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (searchEditText != null && searchEditText.length() > 0) {
            searchEditText.setText("");
        } else {
            super.onBackPressed();
        }
    }
}
package com.example.versionfinal.reclamation;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;

public class ReclamationActivity extends AppCompatActivity {

    private EditText sujetEditText, detailsEditText, descriptionEditText;
    private Spinner typeSpinner;
    private Button submitReclamationButton;
    private DatabaseHelper databaseHelper;
    private String[] reclamationTypes = {"Problème de réservation de terrain", "Problème avec le paiement de la réservation du terrain de communication", "Problème d’entretien du terrain","Problème de comportement ou d'incidents durant les matchs","Autre"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamation);

        // Initialisation des vues
        sujetEditText = findViewById(R.id.sujetEditText);
        detailsEditText = findViewById(R.id.detailsEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        typeSpinner = findViewById(R.id.typeSpinner);
        submitReclamationButton = findViewById(R.id.submitReclamationButton);

        // Initialisation de la base de données
        databaseHelper = new DatabaseHelper(this);

        // Initialiser le Spinner avec les types de réclamation
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, reclamationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        // Action lors du clic sur le bouton de soumission
        submitReclamationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReclamation();
            }
        });
    }

    private void submitReclamation() {
        // Récupérer les valeurs des champs
        String sujet = sujetEditText.getText().toString().trim();
        String details = detailsEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String type = typeSpinner.getSelectedItem().toString();

        // Vérifier que tous les champs sont remplis
        if (sujet.isEmpty() || details.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insérer la réclamation dans la base de données
        boolean result = databaseHelper.addReclamation(sujet, details, type, description);

        // Afficher un message en fonction du résultat et vider les champs si la soumission est réussie
        if (result) {
            Toast.makeText(this, "Réclamation soumise avec succès", Toast.LENGTH_SHORT).show();

            // Vider les champs du formulaire
            sujetEditText.setText("");
            detailsEditText.setText("");
            descriptionEditText.setText("");
            typeSpinner.setSelection(0); // Réinitialiser le spinner au premier élément
        } else {
            Toast.makeText(this, "Échec de la soumission de la réclamation", Toast.LENGTH_SHORT).show();
        }
    }
}

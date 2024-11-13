package com.example.versionfinal.terrain;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;

public class AddEditTerrainActivity extends AppCompatActivity {
    private EditText nameInput, localisationInput, phoneInput;
    private Spinner typeSpinner, statusSpinner;
    private TextView titleAction;
    private Button saveButton;
    private DatabaseHelper dbHelper;
    private boolean isEditMode;
    private int terrainId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_terrain);

        dbHelper = new DatabaseHelper(this);
        initializeViews();
        setupSpinners();
        checkEditMode();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.name_input);
        localisationInput = findViewById(R.id.localisation_input);
        phoneInput = findViewById(R.id.phone_input);
        typeSpinner = findViewById(R.id.type_spinner);
        statusSpinner = findViewById(R.id.status_spinner);
        saveButton = findViewById(R.id.save_button);
        titleAction = findViewById(R.id.titleAction);

        saveButton.setOnClickListener(v -> saveTerrain());
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.terrain_type_options, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        if (intent.hasExtra("terrainId")) {
            terrainId = intent.getIntExtra("terrainId", -1);
            Terrain terrain = dbHelper.getTerrain(terrainId);
            if (terrain != null) {
                nameInput.setText(terrain.getName());
                localisationInput.setText(terrain.getLocalisation());
                phoneInput.setText(terrain.getPhone());

                setSpinnerSelection(typeSpinner, terrain.getType());
                setSpinnerSelection(statusSpinner, terrain.getStatus());
            }
            isEditMode = true;
            titleAction.setText("Edit Terrain");
        } else {
            isEditMode = false;
            titleAction.setText("Add Terrain");
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        spinner.setSelection(position);
    }

    private void saveTerrain() {
        if (validateInputs()) {
            String name = nameInput.getText().toString();
            String localisation = localisationInput.getText().toString();
            String phone = phoneInput.getText().toString();
            String type = typeSpinner.getSelectedItem().toString();
            String status = statusSpinner.getSelectedItem().toString();

            Terrain terrain = new Terrain(name, localisation, type, status, phone);
            if (isEditMode) {
                terrain.setId(terrainId);
                dbHelper.updateTerrain(terrain);
                Toast.makeText(this, "Terrain updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addTerrain(terrain);
                Toast.makeText(this, "Terrain added successfully", Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean validateInputs() {
        if (nameInput.getText().toString().isEmpty()) {
            nameInput.setError("Name is required");
            return false;
        }
        if (localisationInput.getText().toString().isEmpty()) {
            localisationInput.setError("Location is required");
            return false;
        }
        if (phoneInput.getText().toString().isEmpty()) {
            phoneInput.setError("Phone number is required");
            return false;
        }
        return true;
    }
}
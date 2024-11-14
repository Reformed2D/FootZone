package com.example.versionfinal.reclamation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ReclamationActivity extends AppCompatActivity {

    private TextInputLayout sujetLayout, detailsLayout, descriptionLayout;
    private TextInputEditText sujetEditText, detailsEditText, descriptionEditText;
    private AutoCompleteTextView typeSpinner;
    private MaterialButton submitReclamationButton, cancelButton, viewReclamationsButton;
    private CircularProgressIndicator progressIndicator;
    private DatabaseHelper databaseHelper;
    private boolean isEditMode = false;
    private int reclamationId = -1;
    private final String[] reclamationTypes = {
            "Problème de réservation de terrain",
            "Problème avec le paiement",
            "Problème d'entretien du terrain",
            "Problème de comportement",
            "Autre"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamation);

        initializeViews();
        setupListeners();
        setupTypeSpinner();
        updateReclamationsCount();
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateReclamationsCount();
    }

    private void initializeViews() {
        // TextInputLayouts
        sujetLayout = findViewById(R.id.sujetLayout);
        detailsLayout = findViewById(R.id.detailsLayout);
        descriptionLayout = findViewById(R.id.descriptionLayout);

        // EditTexts
        sujetEditText = findViewById(R.id.sujetEditText);
        detailsEditText = findViewById(R.id.detailsEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        typeSpinner = findViewById(R.id.typeSpinner);

        // Buttons
        submitReclamationButton = findViewById(R.id.submitReclamationButton);
        cancelButton = findViewById(R.id.cancelButton);
        viewReclamationsButton = findViewById(R.id.viewReclamationsButton);

        // Progress Indicator
        progressIndicator = findViewById(R.id.progressIndicator);

        // Database
        databaseHelper = new DatabaseHelper(this);
    }

    private void setupListeners() {
        setupTextWatcher(sujetEditText, sujetLayout, "Le sujet est requis");
        setupTextWatcher(detailsEditText, detailsLayout, "Les détails sont requis");
        setupTextWatcher(descriptionEditText, descriptionLayout, "La description est requise");

        submitReclamationButton.setOnClickListener(v -> {
            if (validateInputs()) {
                animateButtonClick(v, () -> {
                    showLoading(true);
                    submitReclamation();
                });
            }
        });

        cancelButton.setOnClickListener(v ->
                animateButtonClick(v, this::finish)
        );

        viewReclamationsButton.setOnClickListener(v ->
                animateButtonClick(v, () -> {
                    Intent intent = new Intent(this, ReclamationsListActivity.class);
                    startActivity(intent);
                })
        );
    }

    private void setupTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_dropdown,
                reclamationTypes
        );
        typeSpinner.setAdapter(adapter);
    }

    private void setupTextWatcher(TextInputEditText editText, TextInputLayout layout, String errorMessage) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(s.toString().trim().isEmpty() ? errorMessage : null);
                updateSubmitButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void animateButtonClick(View view, Runnable onComplete) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction(onComplete)
                            .start();
                })
                .start();
    }

    private void shakeView(View view) {
        view.animate()
                .translationX(20f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .translationX(-20f)
                            .setDuration(100)
                            .withEndAction(() -> {
                                view.animate()
                                        .translationX(0f)
                                        .setDuration(100)
                                        .start();
                            })
                            .start();
                })
                .start();
    }

    private void pulseView(View view) {
        view.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(200)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .start();
                })
                .start();
    }

    private void updateReclamationsCount() {
        int count = databaseHelper.getReclamationsCount();
        String buttonText = getString(R.string.view_reclamations, count);
        viewReclamationsButton.setText(buttonText);

        if (viewReclamationsButton.getTag() != null &&
                (int) viewReclamationsButton.getTag() != count) {
            pulseView(viewReclamationsButton);
        }
        viewReclamationsButton.setTag(count);
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (sujetEditText.getText().toString().trim().isEmpty()) {
            sujetLayout.setError("Le sujet est requis");
            shakeView(sujetLayout);
            isValid = false;
        }

        if (detailsEditText.getText().toString().trim().isEmpty()) {
            detailsLayout.setError("Les détails sont requis");
            shakeView(detailsLayout);
            isValid = false;
        }

        if (descriptionEditText.getText().toString().trim().isEmpty()) {
            descriptionLayout.setError("La description est requise");
            shakeView(descriptionLayout);
            isValid = false;
        }

        if (typeSpinner.getText().toString().isEmpty()) {
            typeSpinner.setError("Le type est requis");
            shakeView(typeSpinner);
            isValid = false;
        }

        return isValid;
    }

    private void updateSubmitButtonState() {
        boolean allFieldsFilled = !sujetEditText.getText().toString().trim().isEmpty() &&
                !detailsEditText.getText().toString().trim().isEmpty() &&
                !descriptionEditText.getText().toString().trim().isEmpty() &&
                !typeSpinner.getText().toString().isEmpty();

        submitReclamationButton.setEnabled(allFieldsFilled);
        submitReclamationButton.setAlpha(allFieldsFilled ? 1f : 0.6f);
    }

    private void submitReclamation() {
        String sujet = sujetEditText.getText().toString().trim();
        String details = detailsEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String type = typeSpinner.getText().toString();

        submitReclamationButton.postDelayed(() -> {
            boolean result = databaseHelper.addReclamation(sujet, details, type, description);
            if (result) {
                showSuccess();
            } else {
                showError();
            }
            showLoading(false);
        }, 1500);
    }

    private void showLoading(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            progressIndicator.setAlpha(0f);
            progressIndicator.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        }

        submitReclamationButton.setEnabled(!show);
        cancelButton.setEnabled(!show);
        viewReclamationsButton.setEnabled(!show);

        sujetEditText.setEnabled(!show);
        detailsEditText.setEnabled(!show);
        descriptionEditText.setEnabled(!show);
        typeSpinner.setEnabled(!show);
    }

    private void showSuccess() {
        Toast.makeText(this, "Réclamation soumise avec succès", Toast.LENGTH_SHORT).show();

        // Animation de succès
        submitReclamationButton.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(200)
                .withEndAction(() -> {
                    submitReclamationButton.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .withEndAction(() -> {
                                clearFields();
                                updateReclamationsCount();
                                animateExit();
                            })
                            .start();
                })
                .start();
    }

    private void clearFields() {
        sujetEditText.setText("");
        detailsEditText.setText("");
        descriptionEditText.setText("");
        typeSpinner.setText("");
    }

    private void animateExit() {
        View rootView = findViewById(android.R.id.content);
        rootView.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(this::finish)
                .start();
    }

    private void showError() {
        Toast.makeText(this, "Échec de la soumission de la réclamation", Toast.LENGTH_SHORT).show();
        submitReclamationButton.setError("Erreur");
        shakeView(submitReclamationButton);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
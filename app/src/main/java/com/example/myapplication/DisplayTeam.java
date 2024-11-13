package com.example.myapplication;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DisplayTeam extends AppCompatActivity {
    private static final String TAG = "DisplayTeam";
    private ImageButton deleteGoalkeeper, deleteDefenderLeft, deleteDefenderRight,
            deleteMidfielderLeft, deleteMidfielderRight,
            deleteForwardLeft, deleteForwardRight;
    private TextView teamNameText;
    private EditText teamNameInput;
    private EditText goalkeeperEdit, defenderLeft, defenderRight, midfielderLeft, midfielderRight, forwardLeft, forwardRight;
    private FloatingActionButton editButton;
    private FloatingActionButton saveButton;

    private long teamId;
    private int userId;
    private boolean isEditable = false;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.displayteam);

        initializeViews();
        dbHelper = new DatabaseHelper(this);

        teamId = getIntent().getLongExtra("TEAM_ID", -1);
        userId = getIntent().getIntExtra("USER_ID", -1);

        Log.d(TAG, "Received team ID: " + teamId);

        // Initially disable all EditTexts
        setFieldsEditable(false);

        // Initially hide save button and show edit button
        saveButton.setVisibility(View.GONE);
        editButton.setVisibility(View.VISIBLE);

        if (teamId != -1) {
            loadTeamData();
        } else {
            Log.e(TAG, "Invalid team ID received");
            Toast.makeText(this, "Error: Invalid team ID", Toast.LENGTH_SHORT).show();
        }

        editButton.setOnClickListener(v -> toggleEditMode(true));
        saveButton.setOnClickListener(v -> toggleEditMode(false));
        initializeDeleteButtons();
        setupDeleteListeners();
    }

    private void initializeViews() {
        teamNameText = findViewById(R.id.teamNameText);
        teamNameInput = findViewById(R.id.teamNameInput);
        goalkeeperEdit = findViewById(R.id.goalkeeperEdit);
        defenderLeft = findViewById(R.id.defenderLeft);
        defenderRight = findViewById(R.id.defenderRight);
        midfielderLeft = findViewById(R.id.midfielderLeft);
        midfielderRight = findViewById(R.id.midfielderRight);
        forwardLeft = findViewById(R.id.forwardLeft);
        forwardRight = findViewById(R.id.forwardRight);
        editButton = findViewById(R.id.editButton);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setFieldsEditable(boolean editable) {
        goalkeeperEdit.setEnabled(editable);
        defenderLeft.setEnabled(editable);
        defenderRight.setEnabled(editable);
        midfielderLeft.setEnabled(editable);
        midfielderRight.setEnabled(editable);
        forwardLeft.setEnabled(editable);
        forwardRight.setEnabled(editable);
        teamNameInput.setEnabled(editable);
    }

    private void loadTeamData() {
        try {
            Team team = dbHelper.getTeamById((int) teamId);
            if (team != null) {
                teamNameText.setText(team.getTeamName());
                teamNameInput.setText(team.getTeamName());
                goalkeeperEdit.setText(team.getGoalkeeper());
                defenderLeft.setText(team.getDefenderLeft());
                defenderRight.setText(team.getDefenderRight());
                midfielderLeft.setText(team.getMidfielderLeft());
                midfielderRight.setText(team.getMidfielderRight());
                forwardLeft.setText(team.getForwardLeft());
                forwardRight.setText(team.getForwardRight());
                Log.d(TAG, "Team loaded successfully: " + team.getTeamName());
            } else {
                Log.e(TAG, "Team is null for ID: " + teamId);
                Toast.makeText(this, "Team not found!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading team data: " + e.getMessage());
            Toast.makeText(this, "Error loading team data", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleEditMode(boolean isEditing) {
        isEditable = isEditing;
        if (isEditing) {
            // Enter edit mode
            teamNameText.setVisibility(View.GONE);
            teamNameInput.setVisibility(View.VISIBLE);
            setFieldsEditable(true);
            setDeleteButtonsVisible(true);  // Show delete buttons
            editButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        } else {
            // Save mode
            if (validateInput()) {
                saveTeamData();
                teamNameText.setVisibility(View.VISIBLE);
                teamNameInput.setVisibility(View.GONE);
                setFieldsEditable(false);
                setDeleteButtonsVisible(false);  // Hide delete buttons
                editButton.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.GONE);
            }
        }
    }

    private boolean validateInput() {
        if (teamNameInput.getText().toString().trim().isEmpty()) {
            teamNameInput.setError("Team name is required");
            return false;
        }
        return true;
    }

    private void saveTeamData() {
        try {
            String updatedTeamName = teamNameInput.getText().toString().trim();
            String updatedGoalkeeper = goalkeeperEdit.getText().toString().trim();
            String updatedDefenderLeft = defenderLeft.getText().toString().trim();
            String updatedDefenderRight = defenderRight.getText().toString().trim();
            String updatedMidfielderLeft = midfielderLeft.getText().toString().trim();
            String updatedMidfielderRight = midfielderRight.getText().toString().trim();
            String updatedForwardLeft = forwardLeft.getText().toString().trim();
            String updatedForwardRight = forwardRight.getText().toString().trim();

            boolean success = dbHelper.updateTeam((int)teamId, updatedTeamName, updatedGoalkeeper,
                    updatedDefenderLeft, updatedDefenderRight, updatedMidfielderLeft,
                    updatedMidfielderRight, updatedForwardLeft, updatedForwardRight);

            if (success) {
                Toast.makeText(this, "Team updated successfully!", Toast.LENGTH_SHORT).show();
                teamNameText.setText(updatedTeamName);
                loadTeamData(); // Reload the data to show updated values
            } else {
                Toast.makeText(this, "Failed to update team", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database update returned false");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Database error: " + e.getMessage());
            Toast.makeText(this, "Database error occurred", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error saving team data: " + e.getMessage());
            Toast.makeText(this, "Error saving team data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
    private void initializeDeleteButtons() {
        deleteGoalkeeper = findViewById(R.id.deleteGoalkeeper);
        deleteDefenderLeft = findViewById(R.id.deleteDefenderLeft);
        deleteDefenderRight = findViewById(R.id.deleteDefenderRight);
        deleteMidfielderLeft = findViewById(R.id.deleteMidfielderLeft);
        deleteMidfielderRight = findViewById(R.id.deleteMidfielderRight);
        deleteForwardLeft = findViewById(R.id.deleteForwardLeft);
        deleteForwardRight = findViewById(R.id.deleteForwardRight);
    }
    private void setupDeleteListeners() {
        deleteGoalkeeper.setOnClickListener(v -> removePlayer(DatabaseHelper.TEAM_COL_3, goalkeeperEdit));
        deleteDefenderLeft.setOnClickListener(v -> removePlayer(DatabaseHelper.TEAM_COL_4, defenderLeft));
        deleteDefenderRight.setOnClickListener(v -> removePlayer(DatabaseHelper.TEAM_COL_5, defenderRight));
        deleteMidfielderLeft.setOnClickListener(v -> removePlayer(DatabaseHelper.TEAM_COL_6, midfielderLeft));
        deleteMidfielderRight.setOnClickListener(v -> removePlayer(DatabaseHelper.TEAM_COL_7, midfielderRight));
        deleteForwardLeft.setOnClickListener(v -> removePlayer(DatabaseHelper.TEAM_COL_8, forwardLeft));
        deleteForwardRight.setOnClickListener(v -> removePlayer(DatabaseHelper.TEAM_COL_9, forwardRight));
    }

    private void setDeleteButtonsVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        deleteGoalkeeper.setVisibility(visibility);
        deleteDefenderLeft.setVisibility(visibility);
        deleteDefenderRight.setVisibility(visibility);
        deleteMidfielderLeft.setVisibility(visibility);
        deleteMidfielderRight.setVisibility(visibility);
        deleteForwardLeft.setVisibility(visibility);
        deleteForwardRight.setVisibility(visibility);
    }

    private void removePlayer(String position, EditText playerField) {
        // Create an AlertDialog to confirm the deletion
        new android.app.AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this player?")
                .setCancelable(false) // Make sure the dialog can't be canceled by tapping outside
                .setPositiveButton("Yes", (dialog, id) -> {
                    // If "Yes" is clicked, proceed with deleting the player
                    try {
                        boolean success = dbHelper.removePlayerFromTeam((int)teamId, position);
                        if (success) {
                            playerField.setText(""); // Clear the field after removal
                            Toast.makeText(this, "Player removed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to remove player", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error removing player: " + e.getMessage());
                        Toast.makeText(this, "Error removing player", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, id) -> {
                    // If "No" is clicked, do nothing and dismiss the dialog
                    dialog.dismiss();
                })
                .create()
                .show();
    }


}
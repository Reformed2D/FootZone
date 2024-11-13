package com.example.myapplication;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

public class TeamActivity extends AppCompatActivity {
    private static final String TAG = "TeamActivity";
    private EditText teamNameInput;
    private EditText goalkeeperEdit;
    private EditText defenderLeftEdit;
    private EditText defenderRightEdit;
    private EditText midfielderLeftEdit;
    private EditText midfielderRightEdit;
    private EditText forwardLeftEdit;
    private EditText forwardRightEdit;
    private FloatingActionButton saveButton;
    private DatabaseHelper databaseHelper;
    private Button recruitPlayersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_player);

        // Initialize recruitPlayersButton
        recruitPlayersButton = findViewById(R.id.recruitPlayersButton);
        recruitPlayersButton.setOnClickListener(v -> {
            Intent intent = new Intent(TeamActivity.this, RecruitPlayersActivity.class);
            startActivity(intent);
        });

        // Initialize other views
        databaseHelper = new DatabaseHelper(this);
        initializeViews();

        // Check if user already has a team
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = preferences.getInt("user_id", -1);

        if (userId != -1) {
            checkExistingTeam(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        saveButton.setOnClickListener(v -> saveTeamData());
    }

    private void checkExistingTeam(int userId) {
        try {
            Cursor teamCursor = databaseHelper.getTeamByUserId(userId);
            if (teamCursor != null && teamCursor.moveToFirst()) {
                int teamIdIndex = teamCursor.getColumnIndex(DatabaseHelper.TEAM_COL_1);
                if (teamIdIndex != -1) {
                    long existingTeamId = teamCursor.getLong(teamIdIndex);
                    Log.d(TAG, "Found existing team with ID: " + existingTeamId);

                    // Launch DisplayTeam activity
                    Intent intent = new Intent(TeamActivity.this, DisplayTeam.class);
                    intent.putExtra("TEAM_ID", existingTeamId);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                    finish(); // Close this activity
                }
            }
            if (teamCursor != null) {
                teamCursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking existing team: " + e.getMessage());
        }
    }

    private void initializeViews() {
        teamNameInput = findViewById(R.id.teamNameInput);
        goalkeeperEdit = findViewById(R.id.goalkeeperEdit);
        defenderLeftEdit = findViewById(R.id.defenderLeft);
        defenderRightEdit = findViewById(R.id.defenderRight);
        midfielderLeftEdit = findViewById(R.id.midfielderLeft);
        midfielderRightEdit = findViewById(R.id.midfielderRight);
        forwardLeftEdit = findViewById(R.id.forwardLeft);
        forwardRightEdit = findViewById(R.id.forwardRight);
        saveButton = findViewById(R.id.saveButton);
    }

    private void saveTeamData() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = preferences.getInt("user_id", -1);
        Log.d(TAG, "Retrieved User ID: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        try {
            // Get data from input fields
            String teamName = teamNameInput.getText().toString().trim();
            String goalkeeper = goalkeeperEdit.getText().toString().trim();
            String defenderLeft = defenderLeftEdit.getText().toString().trim();
            String defenderRight = defenderRightEdit.getText().toString().trim();
            String midfielderLeft = midfielderLeftEdit.getText().toString().trim();
            String midfielderRight = midfielderRightEdit.getText().toString().trim();
            String forwardLeft = forwardLeftEdit.getText().toString().trim();
            String forwardRight = forwardRightEdit.getText().toString().trim();

            int teamId = databaseHelper.insertTeam(teamName, goalkeeper, defenderLeft, defenderRight,
                    midfielderLeft, midfielderRight, forwardLeft, forwardRight, userId);

            if (teamId != -1) {
                Log.d(TAG, "Team saved successfully with ID: " + teamId);
                Toast.makeText(this, "Team saved successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(TeamActivity.this, DisplayTeam.class);
                intent.putExtra("TEAM_ID", (long)teamId);  // Cast to long since teamId is int
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to save team", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error saving team to database");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving team: " + e.getMessage());
            Toast.makeText(this, "Error saving team", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        if (teamNameInput.getText().toString().trim().isEmpty()) {
            teamNameInput.setError("Team name is required");
            return false;
        }
        // Add other validations as needed
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
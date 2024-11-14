package com.example.versionfinal.User;

import static com.example.versionfinal.User.EmailUtils.sendVerificationEmail;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import com.example.versionfinal.equipe.TeamActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText editUsername, editEmail, editBirthdate;
    private AutoCompleteTextView positionSpinner;
    private MaterialButton btnEditProfile, btnChangeImage;
    private ImageView profileImage;
    private RadioGroup radioGroupTeam;
    private RadioButton checkboxTeam, checkboxNoTeam;
    private DatabaseHelper databaseHelper;
    private MaterialButton btnVerifyEmail;
    private boolean isVerified = false;
    private static final String EMAIL_SUBJECT = "Vérification du compte";
    private static final String EMAIL_BODY = "Félicitations ! Votre compte a été vérifié avec succès. Bienvenue dans notre communauté !";

    private static final int PICK_IMAGE_REQUEST = 1000;
    private String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupPositionDropdown();
        setupListeners();
        btnVerifyEmail = findViewById(R.id.btn_verify_email);
        databaseHelper = new DatabaseHelper(this);
        // Ajoutez cette ligne dans setupListeners()
        setupVerificationButton();

        // Dans loadUserData(), ajoutez :

        databaseHelper = new DatabaseHelper(this);

        // Charger les données utilisateur
        String userEmail = getIntent().getStringExtra("email");
        if (userEmail != null) {
            loadUserData(userEmail);
            loadVerificationStatus(userEmail); // Utilisez userEmail au lieu de email
        }
    }


    private void initializeViews() {
        editUsername = findViewById(R.id.edit_username);
        editEmail = findViewById(R.id.edit_email);
        editBirthdate = findViewById(R.id.edit_birthdate);
        profileImage = findViewById(R.id.profile_image);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangeImage = findViewById(R.id.btn_change_image);
        radioGroupTeam = findViewById(R.id.radioGroupTeam);
        checkboxTeam = findViewById(R.id.checkbox_team);
        checkboxNoTeam = findViewById(R.id.checkbox_no_team);
        positionSpinner = findViewById(R.id.spinner_position);
    }

    private void setupPositionDropdown() {
        String[] positions = getResources().getStringArray(R.array.positions_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                positions
        );
        positionSpinner.setAdapter(adapter);
    }
    private void setupVerificationButton() {
        btnVerifyEmail.setOnClickListener(v -> {
            if (!isVerified) {
                sendVerificationEmail();
            }
        });
    }
    private void loadVerificationStatus(String email) {
        Cursor cursor = databaseHelper.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                int verifiedIndex = cursor.getColumnIndex(DatabaseHelper.COL_12);
                if (verifiedIndex != -1) {
                    isVerified = cursor.getInt(verifiedIndex) == 1;
                    updateVerificationUI();
                }
            } catch (Exception e) {
                Log.e("ProfileActivity", "Error loading verification status: " + e.getMessage());
                isVerified = false;
            } finally {
                cursor.close();
            }
        }
    }
    private void updateVerificationUI() {
        if (isVerified) {
            btnVerifyEmail.setText("Vérifié");
            btnVerifyEmail.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, android.R.color.holo_green_dark)));
            btnVerifyEmail.setEnabled(false);
            btnVerifyEmail.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_verified));
        } else {
            btnVerifyEmail.setText("Non Vérifié");
            btnVerifyEmail.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.drawable.ic_not_verified)));
            btnVerifyEmail.setEnabled(true);
        }
    }
    private void sendVerificationEmail() {
        String userEmail = editEmail.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, EMAIL_SUBJECT);
        intent.putExtra(Intent.EXTRA_TEXT, EMAIL_BODY);

        try {
            startActivityForResult(Intent.createChooser(intent, "Envoi de l'email de vérification..."), 1001);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Aucune application de messagerie n'est installée.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        btnEditProfile.setOnClickListener(v -> saveUserData());
        btnChangeImage.setOnClickListener(v -> openImagePicker());
        editBirthdate.setOnClickListener(v -> showDatePickerDialog());

        // Empêcher l'édition manuelle de la date
        editBirthdate.setFocusable(false);
        editBirthdate.setClickable(true);
    }

    private void loadUserData(String email) {
        Cursor cursor = databaseHelper.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            int usernameIndex = cursor.getColumnIndex("USERNAME");
            int emailIndex = cursor.getColumnIndex("EMAIL");
            int birthdateIndex = cursor.getColumnIndex("BIRTHDATE");
            int positionIndex = cursor.getColumnIndex("POSITION");
            int imageIndex = cursor.getColumnIndex("PROFILE_IMAGE");

            if (usernameIndex != -1) {
                editUsername.setText(cursor.getString(usernameIndex));
            }
            if (emailIndex != -1) {
                editEmail.setText(cursor.getString(emailIndex));
            }
            if (birthdateIndex != -1) {
                editBirthdate.setText(cursor.getString(birthdateIndex));
            }
            if (positionIndex != -1) {
                String position = cursor.getString(positionIndex);
                positionSpinner.setText(position, false);
            }
            if (imageIndex != -1) {
                loadProfileImage(cursor.getString(imageIndex));
            }

            cursor.close();
        }
    }

    private void loadProfileImage(String imageString) {
        if (imageString != null && !imageString.isEmpty()) {
            try {
                byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                profileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserData() {
        String username = editUsername.getText().toString();
        String email = editEmail.getText().toString();
        String birthdate = editBirthdate.getText().toString();
        String position = positionSpinner.getText().toString();
        boolean withTeam = checkboxTeam.isChecked();
        boolean withoutTeam = checkboxNoTeam.isChecked();

        if (validateInput()) {
            boolean isUpdated = databaseHelper.updateUser(email, username, "Utilisateur",
                    birthdate, position, withTeam, withoutTeam, encodedImage);

            if (isUpdated) {
                Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                loadUserData(email);
                if (withTeam) {
                    Intent intent = new Intent(ProfileActivity.this, TeamActivity.class);
                    startActivity(intent);
                    finish();  // Close ProfileActivity to prevent going back to it
                }
            } else {
                Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput() {
        if (editUsername.getText().toString().trim().isEmpty()) {
            editUsername.setError("Le nom d'utilisateur est requis");
            return false;
        }
        if (editEmail.getText().toString().trim().isEmpty()) {
            editEmail.setError("L'email est requis");
            return false;
        }
        if (editBirthdate.getText().toString().trim().isEmpty()) {
            editBirthdate.setError("La date de naissance est requise");
            return false;
        }
        if (positionSpinner.getText().toString().trim().isEmpty()) {
            positionSpinner.setError("La position est requise");
            return false;
        }
        return true;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String date = String.format("%02d/%02d/%d", day, month + 1, year);
                    editBirthdate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                encodedImage = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            } catch (IOException e) {
                Toast.makeText(this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        if (requestCode == 1001) {
            // L'email a été envoyé (ou l'utilisateur a annulé)
            // On considère que l'envoi est réussi et on met à jour le statut
            String userEmail = editEmail.getText().toString();
            if (databaseHelper.updateVerificationStatus(userEmail, true)) {
                isVerified = true;
                updateVerificationUI();
                Toast.makeText(this, "Votre compte a été vérifié avec succès !",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
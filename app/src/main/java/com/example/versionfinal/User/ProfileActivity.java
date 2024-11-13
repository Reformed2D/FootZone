package com.example.versionfinal.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editBirthdate;
    private ImageView profileImage;
    private Button btnEditProfile, btnChangeImage;
    private RadioGroup radioGroupTeam;
    private RadioButton checkboxTeam, checkboxNoTeam;
    private Spinner positionSpinner;
    private DatabaseHelper databaseHelper;

    private static final int PICK_IMAGE_REQUEST = 1000;
    private String encodedImage = ""; // Pour stocker l'image encodée

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);

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

        // Configurez le spinner pour les positions
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.positions_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(adapter);

        // Charger les données utilisateur
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        loadUserData(email);

        // Gérer le bouton "Appliquer les modifications"
        btnEditProfile.setOnClickListener(v -> saveUserData());

        // Changer l'image de profil
        btnChangeImage.setOnClickListener(v -> openImagePicker());

        // Ouvrir le DatePicker pour la date de naissance
        editBirthdate.setOnClickListener(v -> showDatePickerDialog());
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
                String username = cursor.getString(usernameIndex);
                editUsername.setText(username);
            }

            if (emailIndex != -1) {
                String userEmail = cursor.getString(emailIndex);
                editEmail.setText(userEmail);
            }

            if (birthdateIndex != -1) {
                String birthdate = cursor.getString(birthdateIndex);
                editBirthdate.setText(birthdate);
            }

            if (positionIndex != -1) {
                String position = cursor.getString(positionIndex);
                int positionSpinnerIndex = ((ArrayAdapter<CharSequence>) positionSpinner.getAdapter()).getPosition(position);
                positionSpinner.setSelection(positionSpinnerIndex);
            }

            if (imageIndex != -1) {
                String imageString = cursor.getString(imageIndex);
                if (imageString != null && !imageString.isEmpty()) {
                    byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profileImage.setImageBitmap(bitmap);
                }
            }

            cursor.close();
        }
    }

    private void saveUserData() {
        String username = editUsername.getText().toString();
        String email = editEmail.getText().toString();
        String birthdate = editBirthdate.getText().toString();
        String position = positionSpinner.getSelectedItem().toString();
        boolean withTeam = checkboxTeam.isChecked();
        boolean withoutTeam = checkboxNoTeam.isChecked();

        boolean isUpdated = databaseHelper.updateUser(email, username, "Utilisateur", birthdate, position, withTeam, withoutTeam, encodedImage);
        if (isUpdated) {
            Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
            loadUserData(email);  // Recharge les données
        } else {
            Toast.makeText(this, "Échec de la mise à jour du profil", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editBirthdate.setText(date);
                }, year, month, day);
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
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);

                // Convertir l'image en chaîne encodée en Base64
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

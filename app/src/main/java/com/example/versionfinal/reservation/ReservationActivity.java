package com.example.versionfinal.reservation;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    private EditText clientNameEdit, fieldNumberEdit, dateEdit, timeEdit, durationEdit, priceEdit, terrainListEdit;
    private Button saveButton, viewButton, payButton; // Button References
    private ReservationDAO reservationDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Initialize views
        clientNameEdit = findViewById(R.id.clientNameEdit);
        fieldNumberEdit = findViewById(R.id.fieldNumberEdit);
        dateEdit = findViewById(R.id.dateEdit);
        timeEdit = findViewById(R.id.timeEdit);
        durationEdit = findViewById(R.id.durationEdit);
        priceEdit = findViewById(R.id.priceEdit);
        terrainListEdit = findViewById(R.id.terrainListEdit);

        saveButton = findViewById(R.id.saveButton);
        viewButton = findViewById(R.id.viewButton);
        payButton = findViewById(R.id.payButton); // Initialize payButton

        // Initialize DAO
        reservationDAO = new ReservationDAO(this);
        reservationDAO.open();

        // Set up button listeners
        saveButton.setOnClickListener(v -> saveReservation());
        viewButton.setOnClickListener(v -> viewReservations());
        payButton.setOnClickListener(v -> processPayment());

        // Date and Time Click Listeners
        dateEdit.setOnClickListener(v -> showDatePickerDialog());
        timeEdit.setOnClickListener(v -> showTimePickerDialog());

        // Terrain list click listener to show selection dialog
        terrainListEdit.setOnClickListener(v -> showTerrainSelectionDialog());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        dateEdit.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) ->
                        timeEdit.setText(selectedHour + ":" + String.format("%02d", selectedMinute)),
                hour, minute, true);
        timePickerDialog.show();
    }

    private void showTerrainSelectionDialog() {
        String[] terrainOptions = {"Terrain 1", "Terrain 2", "Terrain 3"}; // Replace with dynamic list if needed
        boolean[] checkedItems = new boolean[terrainOptions.length];
        List<Integer> selectedTerrains = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Terrains")
                .setMultiChoiceItems(terrainOptions, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedTerrains.add(which); // Assume the terrain ID is the index
                    } else {
                        selectedTerrains.remove(Integer.valueOf(which));
                    }
                })
                .setPositiveButton("OK", (dialog, id) -> {
                    // Convert selected terrain IDs to a comma-separated string and set it to the EditText
                    StringBuilder terrainStringBuilder = new StringBuilder();
                    for (Integer terrainId : selectedTerrains) {
                        terrainStringBuilder.append(terrainId).append(", ");
                    }
                    // Remove the last comma and space
                    if (terrainStringBuilder.length() > 0) {
                        terrainStringBuilder.setLength(terrainStringBuilder.length() - 2);
                    }
                    terrainListEdit.setText(terrainStringBuilder.toString());
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }

    private void saveReservation() {
        try {
            String clientName = clientNameEdit.getText().toString().trim();
            int fieldNumber = Integer.parseInt(fieldNumberEdit.getText().toString().trim());
            String date = dateEdit.getText().toString().trim();
            String time = timeEdit.getText().toString().trim();
            int duration = Integer.parseInt(durationEdit.getText().toString().trim());
            double price = Double.parseDouble(priceEdit.getText().toString().trim());
            String terrainListString = terrainListEdit.getText().toString().trim();

            // Create and save reservation
            Reservation reservation = new Reservation();
            reservation.setClientName(clientName);
            reservation.setFieldNumber(fieldNumber);
            reservation.setDate(date);
            reservation.setStartTime(time);
            reservation.setDuration(duration);
            reservation.setPrice(price);

            // Populate terrain list
            if (!terrainListString.isEmpty()) {
                reservation.setTerrainListFromString(terrainListString);
            }

            long id = reservationDAO.createReservation(reservation);

            if (id > 0) {
                Toast.makeText(this, "Reservation saved!", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(this, "Error saving reservation.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input. Please check your fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewReservations() {
        Intent intent = new Intent(this, ReservationListActivity.class);
        startActivity(intent);
    }

    private void processPayment() {
        try {
            String priceString = priceEdit.getText().toString().trim();
            if (priceString.isEmpty()) {
                Toast.makeText(this, "Please enter a price before paying", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceString);
            // Here you can add your payment processing logic
            // For example, you might show a dialog or navigate to a payment page
            Toast.makeText(this, "Processing payment of $" + price, Toast.LENGTH_SHORT).show();

            // To simulate completing a payment:
            // Clear the price field after payment for demonstration purposes
            priceEdit.setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price. Please check your input.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        clientNameEdit.setText("");
        fieldNumberEdit.setText("");
        dateEdit.setText("");
        timeEdit.setText("");
        durationEdit.setText("");
        priceEdit.setText("");
        terrainListEdit.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reservationDAO.close();
    }
}
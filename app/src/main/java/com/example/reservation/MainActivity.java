package com.example.reservation;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private MaterialButton btnReservation;
    private MaterialButton btnViewReservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReservation = findViewById(R.id.btnReservation);
        btnViewReservations = findViewById(R.id.btnViewReservations);

        btnReservation.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ReservationActivity.class);
            startActivity(intent);
        });

        btnViewReservations.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ReservationListActivity.class);
            startActivity(intent);
        });
    }
}
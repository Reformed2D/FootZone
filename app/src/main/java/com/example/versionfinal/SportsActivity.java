package com.example.versionfinal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.versionfinal.equipe.TeamActivity;
import com.example.versionfinal.reservation.ReservationActivity;

public class SportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        setupSportsCards();
        setupBottomNavigation();
    }

    private void setupSportsCards() {
        CardView[] cards = {
                findViewById(R.id.tennisCard),
                findViewById(R.id.padelCard),
                findViewById(R.id.badmintonCard),
                findViewById(R.id.squashCard),
                findViewById(R.id.pickleballCard)
        };

        String[] sportNames = {"Terrain", "Statistique", "Reservation", "Reclamation", "Equipe"};

        for (int i = 0; i < cards.length; i++) {
            final String sportName = sportNames[i];
            final int position = i;
            cards[i].setOnClickListener(v -> handleCardClick(sportName, position));
        }
    }

    private void handleCardClick(String sportName, int position) {
        switch (position) {
            case 2: // Reservation
                startActivity(new Intent(this, ReservationActivity.class));
                break;
            case 3: // Reclamation
            case 4: // Equipe
                startActivity(new Intent(this, TeamActivity.class));
                break;
            default:
                Toast.makeText(this, "Fonctionnalité en cours de développement", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setupBottomNavigation() {
        findViewById(R.id.searchButton).setOnClickListener(v -> {
            Toast.makeText(this, "Fonctionnalité de recherche en cours de développement", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.reservationsButton).setOnClickListener(v -> {
            startActivity(new Intent(this, ReservationActivity.class));
        });

        findViewById(R.id.profileButton).setOnClickListener(v -> {
            Toast.makeText(this, "Fonctionnalité de profil en cours de développement", Toast.LENGTH_SHORT).show();
        });
    }
}
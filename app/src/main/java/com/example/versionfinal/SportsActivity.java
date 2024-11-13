package com.example.versionfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.versionfinal.User.LoginActivity;

public class SportsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String LOGIN_STATUS = "login_status";

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
        if (isLoggedIn()) {
            // L'utilisateur est connecté, gérer chaque cas
            switch(position) {
                case 0: // Terrain
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
                case 1: // Statistique
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
                case 2: // Reservation
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
                case 3: // Reclamation
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
                case 4: // Equipe
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
            }
        } else {
            // L'utilisateur n'est pas connecté, rediriger vers login
            redirectToLogin(sportName);
        }
    }

    private void setupBottomNavigation() {
        findViewById(R.id.searchButton).setOnClickListener(v -> {
            if (isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                redirectToLogin("la recherche");
            }
        });

        findViewById(R.id.reservationsButton).setOnClickListener(v -> {
            if (isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                redirectToLogin("vos réservations");
            }
        });

        findViewById(R.id.profileButton).setOnClickListener(v -> {
            if (isLoggedIn()) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                redirectToLogin("votre profil");
            }
        });
    }

    private boolean isLoggedIn() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(LOGIN_STATUS, false);
    }

    private void redirectToLogin(String feature) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("REDIRECT_FROM", feature);
        intent.putExtra("message", "Veuillez vous connecter pour accéder à " + feature);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        // Vérifier si l'utilisateur s'est connecté après redirection
        if (isLoggedIn()) {
            // Rafraîchir l'interface si nécessaire
            refreshUI();
        }
    }

    private void refreshUI() {
        // Mettre à jour l'interface utilisateur si nécessaire
        // Par exemple, afficher le nom de l'utilisateur, etc.
    }

    // Classes internes pour la gestion des états
    public static class LoginState {
        public static boolean isLoggedIn = false;
        public static String username = "";
        public static String userType = ""; // "admin" ou "user"
    }
}
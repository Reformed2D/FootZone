package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupérer l'icône par son ID
        ImageView icon = findViewById(R.id.football_logo);

        // Ajouter un listener pour détecter le clic sur l'icône
        icon.setOnClickListener(v -> {
            // Rediriger vers l'IntroActivity
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intent);
        });
    }
}

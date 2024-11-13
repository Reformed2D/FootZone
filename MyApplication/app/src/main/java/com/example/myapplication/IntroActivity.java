package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mettre l'application en plein écran immersif
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_intro);

        // Obtenir la vue vidéo
        VideoView videoView = findViewById(R.id.videoView);

        // Redimensionner le VideoView pour occuper tout l'écran
        videoView.setOnPreparedListener(mp -> {
            mp.setOnVideoSizeChangedListener((mp1, width, height) -> {
                // Obtenir les dimensions de l'écran
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int screenHeight = getResources().getDisplayMetrics().heightPixels;

                // Ajuster les dimensions du VideoView pour occuper tout l'écran
                videoView.getLayoutParams().width = screenWidth;
                videoView.getLayoutParams().height = screenHeight;
                videoView.requestLayout();
            });
        });

        // Définir le chemin de la vidéo (dans le répertoire raw)
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.appp);
        videoView.setVideoURI(video);

        // Démarrer la vidéo
        videoView.start();

        // Redirection vers la page de login une fois la vidéo terminée
        videoView.setOnCompletionListener(mp -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }}

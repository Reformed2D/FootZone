package com.example.versionfinal.User;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import com.example.versionfinal.payment.PaymentActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton, signupButton;
    private ProgressBar progressBar;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupListeners();

        db = new DatabaseHelper(this);
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        signupButton.setOnClickListener(v -> navigateToSignup());
    }

    private void handleLogin() {
        String emailStr = emailEditText.getText().toString().trim();
        String passwordStr = passwordEditText.getText().toString().trim();

        if (validateInput(emailStr, passwordStr)) {
            showProgress(true);
            performLogin(emailStr, passwordStr);
        }
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Veuillez entrer votre email");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Veuillez entrer votre mot de passe");
            return false;
        }
        return true;
    }

    private void performLogin(String email, String password) {
        // Vérification super admin
        if (email.equals("omaradmin@gmail.com") && password.equals("admin")) {
            navigateToAdmin();
            return;
        }

        // Vérification utilisateur normal
        boolean isValid = db.checkUser(email, password);
        if (isValid) {
            handleSuccessfulLogin(email);
        } else {
            handleFailedLogin();
        }
        showProgress(false);
    }

    private void handleSuccessfulLogin(String email) {
        Cursor cursor = db.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            int isAdminIndex = cursor.getColumnIndex("IS_SUPER_ADMIN");
            if (isAdminIndex != -1 && cursor.getInt(isAdminIndex) == 1) {
                navigateToAdmin();
            } else {
                navigateToPayment(email);
            }
            cursor.close();
        }
    }

    private void handleFailedLogin() {
        Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
    }

    private void navigateToAdmin() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToPayment(String email) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    private void navigateToSignup() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!show);
        signupButton.setEnabled(!show);
    }
}
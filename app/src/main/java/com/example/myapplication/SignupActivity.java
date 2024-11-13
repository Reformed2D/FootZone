package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText username, email, password, confirmPassword;
    private Spinner roleSpinner;
    private Button btnSignup;
    private TextView txtLogin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = new DatabaseHelper(this);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        roleSpinner = findViewById(R.id.role_spinner);
        btnSignup = findViewById(R.id.btn_signup);
        txtLogin = findViewById(R.id.txt_login);

        btnSignup.setOnClickListener(v -> {
            String usernameStr = username.getText().toString();
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();
            String confirmPasswordStr = confirmPassword.getText().toString();
            String selectedRole = roleSpinner.getSelectedItem().toString();

            if (usernameStr.isEmpty() || emailStr.isEmpty() || passwordStr.isEmpty() || confirmPasswordStr.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else if (!passwordStr.equals(confirmPasswordStr)) {
                Toast.makeText(SignupActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            } else {
                boolean isInserted = db.insertUser(usernameStr, emailStr, passwordStr, selectedRole);
                if (isInserted) {
                    Toast.makeText(SignupActivity.this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

package com.example.versionfinal.User;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText username, email, password, confirmPassword;
    private AutoCompleteTextView roleSpinner;
    private Button btnSignup;
    private TextView txtLogin;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        setupRoleSpinner();
        setupListeners();

        db = new DatabaseHelper(this);
    }

    private void initializeViews() {
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        roleSpinner = findViewById(R.id.role_spinner);
        btnSignup = findViewById(R.id.btn_signup);
        txtLogin = findViewById(R.id.txt_login);
    }

    private void setupRoleSpinner() {
        String[] roles = getResources().getStringArray(R.array.roles_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                roles
        );
        roleSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        btnSignup.setOnClickListener(v -> handleSignup());
        txtLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void handleSignup() {
        String usernameStr = username.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString();
        String confirmPasswordStr = confirmPassword.getText().toString();
        String selectedRole = roleSpinner.getText().toString();

        if (validateInput(usernameStr, emailStr, passwordStr, confirmPasswordStr, selectedRole)) {
            performSignup(usernameStr, emailStr, passwordStr, selectedRole);
        }
    }

    private boolean validateInput(String username, String email, String password,
                                  String confirmPassword, String role) {
        if (username.isEmpty()) {
            this.username.setError("Le nom d'utilisateur est requis");
            return false;
        }
        if (email.isEmpty()) {
            this.email.setError("L'email est requis");
            return false;
        }
        if (password.isEmpty()) {
            this.password.setError("Le mot de passe est requis");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            this.confirmPassword.setError("La confirmation du mot de passe est requise");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            this.confirmPassword.setError("Les mots de passe ne correspondent pas");
            return false;
        }
        if (role.isEmpty()) {
            roleSpinner.setError("Le rôle est requis");
            return false;
        }
        return true;
    }

    private void performSignup(String username, String email, String password, String role) {
        boolean isInserted = db.insertUser(username, email, password, role);
        if (isInserted) {
            Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        } else {
            Toast.makeText(this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
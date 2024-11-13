
        package com.example.versionfinal.User;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton, signupButton, googleSignInButton;
    private View progressBar;
    private DatabaseHelper db;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "LoginActivity";

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                } else {
                    Toast.makeText(this, "Connexion Google annulée", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupGoogleSignIn();
        setupListeners();

        db = new DatabaseHelper(this);
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        signupButton.setOnClickListener(v -> navigateToSignup());
        googleSignInButton.setOnClickListener(v -> startGoogleSignIn());
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

    private void startGoogleSignIn() {
        showProgress(true);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String email = account.getEmail();
                if (email != null) {
                    Cursor cursor = db.getUserByEmail(email);
                    if (cursor != null && cursor.getCount() > 0) {
                        // Utilisateur existant
                        cursor.close();
                        handleSuccessfulLogin(email);
                    } else {
                        // Nouvel utilisateur
                        createAccountFromGoogle(account);
                    }
                }
            }
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            Toast.makeText(this, "Échec de la connexion Google", Toast.LENGTH_SHORT).show();
            showProgress(false);
        }
    }

    private void createAccountFromGoogle(GoogleSignInAccount account) {
        String name = account.getDisplayName();
        String email = account.getEmail();
        // Générer un mot de passe sécurisé pour les comptes Google
        String password = "google_" + System.currentTimeMillis();

        if (db.insertUser(name != null ? name : "Utilisateur Google", email, password, "Utilisateur")) {
            handleSuccessfulLogin(email);
        } else {
            Toast.makeText(this, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
            showProgress(false);
        }
    }

    private void handleSuccessfulLogin(String email) {
        Cursor cursor = db.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            int isAdminIndex = cursor.getColumnIndex("IS_SUPER_ADMIN");
            if (isAdminIndex != -1 && cursor.getInt(isAdminIndex) == 1) {
                navigateToAdmin();
            } else {
                navigateToProfile(email);
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

    private void navigateToProfile(String email) {
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
        googleSignInButton.setEnabled(!show);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Vérifier si l'utilisateur est déjà connecté avec Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(null));
        }
    }
}

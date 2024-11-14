package com.example.versionfinal.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import com.example.versionfinal.SportsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText, captchaInput;
    private MaterialButton loginButton, signupButton, googleSignInButton;
    private ProgressBar progressBar;
    private TextView captchaText;
    private ImageButton refreshCaptcha;
    private String currentCaptcha;
    private DatabaseHelper db;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "LoginActivity";
    private int loginAttempts = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 3;

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
        generateNewCaptcha();

        db = new DatabaseHelper(this);
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        captchaInput = findViewById(R.id.captchaInput);
        captchaText = findViewById(R.id.captchaText);
        refreshCaptcha = findViewById(R.id.refreshCaptcha);
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
        refreshCaptcha.setOnClickListener(v -> generateNewCaptcha());
        TextView loginWithoutButton = findViewById(R.id.loginWithoutButton);
        loginWithoutButton.setOnClickListener(v -> navigateToSportActivity());
    }
    private void navigateToSportActivity() {
        Intent intent = new Intent(this, SportsActivity.class);
        startActivity(intent);
        finish();
    }
    private void generateNewCaptcha() {
        currentCaptcha = generateRandomCaptcha();
        applyTextEffects(currentCaptcha);
    }

    private String generateRandomCaptcha() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            captcha.append(characters.charAt(random.nextInt(characters.length())));
        }

        return captcha.toString();
    }

    private void applyTextEffects(String captcha) {
        SpannableStringBuilder builder = new SpannableStringBuilder(captcha);
        Random random = new Random();

        for (int i = 0; i < captcha.length(); i++) {
            builder.setSpan(
                    new StyleSpan(random.nextBoolean() ? Typeface.BOLD : Typeface.ITALIC),
                    i, i + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        captchaText.setText(builder);
    }

    private void handleLogin() {
        String emailStr = emailEditText.getText().toString().trim();
        String passwordStr = passwordEditText.getText().toString().trim();
        String captchaStr = captchaInput.getText().toString().trim();

        if (validateInput(emailStr, passwordStr, captchaStr)) {
            if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                Toast.makeText(this, "Trop de tentatives. Réessayez plus tard.", Toast.LENGTH_LONG).show();
                disableLoginTemporarily();
                return;
            }
            showProgress(true);
            performLogin(emailStr, passwordStr);
        }
    }

    private boolean validateInput(String email, String password, String captcha) {
        if (email.isEmpty()) {
            emailEditText.setError("Veuillez entrer votre email");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Veuillez entrer votre mot de passe");
            return false;
        }
        if (captcha.isEmpty()) {
            captchaInput.setError("Veuillez entrer le code captcha");
            return false;
        }
        if (!captcha.equalsIgnoreCase(currentCaptcha)) {
            captchaInput.setError("Code captcha incorrect");
            generateNewCaptcha();
            return false;
        }
        return true;
    }

    private void performLogin(String email, String password) {
        if (email.equals("omaradmin@gmail.com") && password.equals("admin")) {
            navigateToAdmin();
            return;
        }

        boolean isValid = db.checkUser(email, password);
        if (isValid) {
            loginAttempts = 0;
            handleSuccessfulLogin(email);
        } else {
            loginAttempts++;
            handleFailedLogin();
        }
        showProgress(false);
    }

    private void disableLoginTemporarily() {
        loginButton.setEnabled(false);
        new android.os.Handler().postDelayed(() -> {
            loginButton.setEnabled(true);
            loginAttempts = 0;
        }, 30000); // 30 seconds delay
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
                        cursor.close();
                        handleSuccessfulLogin(email);
                    } else {
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
            int userIdIndex = cursor.getColumnIndex("ID");
            if (userIdIndex != -1) {
                int userId = cursor.getInt(userIdIndex);
                // Save the user ID in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", userId);  // Store the userId fetched from the database
                editor.apply();
                Log.d("LoginActivity", "User ID saved: " + userId);  // Optional: Log to verify
            }
            cursor.close();
        }
    }

    private void handleFailedLogin() {
        Toast.makeText(this, "Email, mot de passe ou captcha incorrect", Toast.LENGTH_SHORT).show();
        generateNewCaptcha();
        captchaInput.setText("");
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
        refreshCaptcha.setEnabled(!show);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(null));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateNewCaptcha();
        captchaInput.setText("");
    }
}
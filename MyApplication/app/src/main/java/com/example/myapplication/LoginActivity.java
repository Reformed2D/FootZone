package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnLogin;
    private TextView txtSignup;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        txtSignup = findViewById(R.id.txt_signup);

        btnLogin.setOnClickListener(v -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();

            if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                // Vérification statique pour super admin
                if (emailStr.equals("omaradmin@gmail.com") && passwordStr.equals("admin")) {
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    boolean isValid = db.checkUser(emailStr, passwordStr);
                    if (isValid) {
                        Cursor cursor = db.getUserByEmail(emailStr);
                        if (cursor != null && cursor.moveToFirst()) {
                            Intent intent;
                            int isAdminIndex = cursor.getColumnIndex("IS_SUPER_ADMIN");
                            if (isAdminIndex != -1 && cursor.getInt(isAdminIndex) == 1) {
                                intent = new Intent(LoginActivity.this, AdminActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                intent.putExtra("email", emailStr);
                            }
                            startActivity(intent);
                        }
                        cursor.close();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

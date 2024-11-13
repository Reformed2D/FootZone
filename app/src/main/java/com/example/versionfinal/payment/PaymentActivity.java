package com.example.versionfinal.payment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.versionfinal.R;

public class PaymentActivity extends AppCompatActivity {

    private EditText cardNumberEdit;
    private EditText expiryDateEdit;
    private EditText cvvEdit;
    private EditText cardHolderEdit;
    private RadioGroup paymentMethodGroup;
    private Button confirmPaymentButton;
    private TextView amountTextView;
    private double amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Récupérer le montant passé en extra
        amount = getIntent().getDoubleExtra("PRICE", 0.0);

        // Initialiser les vues
        initViews();

        // Configurer les listeners
        setupListeners();

        // Afficher le montant
        amountTextView.setText(String.format("Montant à payer : %.2f €", amount));
    }

    private void initViews() {
        cardNumberEdit = findViewById(R.id.cardNumberEdit);
        expiryDateEdit = findViewById(R.id.expiryDateEdit);
        cvvEdit = findViewById(R.id.cvvEdit);
        cardHolderEdit = findViewById(R.id.cardHolderEdit);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        confirmPaymentButton = findViewById(R.id.confirmPaymentButton);
        amountTextView = findViewById(R.id.amountTextView);
    }

    private void setupListeners() {
        confirmPaymentButton.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        if (!validateInputs()) {
            return;
        }

        // Simuler un traitement de paiement
        Toast.makeText(this, "Traitement du paiement...", Toast.LENGTH_SHORT).show();
        confirmPaymentButton.setEnabled(false);

        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(PaymentActivity.this,
                    "Paiement réussi!", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }, 2000);
    }

    private boolean validateInputs() {
        String cardNumber = cardNumberEdit.getText().toString().trim();
        String expiryDate = expiryDateEdit.getText().toString().trim();
        String cvv = cvvEdit.getText().toString().trim();
        String cardHolder = cardHolderEdit.getText().toString().trim();

        if (cardNumber.isEmpty() || cardNumber.length() < 16) {
            showError("Numéro de carte invalide");
            return false;
        }

        if (expiryDate.isEmpty() || !expiryDate.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
            showError("Date d'expiration invalide (MM/YY)");
            return false;
        }

        if (cvv.isEmpty() || cvv.length() < 3) {
            showError("CVV invalide");
            return false;
        }

        if (cardHolder.isEmpty()) {
            showError("Nom du titulaire requis");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
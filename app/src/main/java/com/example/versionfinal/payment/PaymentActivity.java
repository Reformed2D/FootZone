package com.example.versionfinal.payment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.versionfinal.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            showReceiptDialog();
        }, 2000);
    }

    private void showReceiptDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.receipt_dialog);
        dialog.setCancelable(false);

        // Générer un numéro de transaction unique
        String transactionId = String.format("TRX-%d", System.currentTimeMillis());

        // Récupérer les informations de paiement
        String cardNumber = cardNumberEdit.getText().toString();
        String maskedCard = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        String cardHolder = cardHolderEdit.getText().toString();

        // Formater la date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Configurer les vues du reçu
        TextView receiptNumber = dialog.findViewById(R.id.receiptNumber);
        TextView receiptDate = dialog.findViewById(R.id.receiptDate);
        TextView receiptAmount = dialog.findViewById(R.id.receiptAmount);
        TextView receiptCardHolder = dialog.findViewById(R.id.receiptCardHolder);
        TextView receiptCardNumber = dialog.findViewById(R.id.receiptCardNumber);
        ImageView qrCodeImage = dialog.findViewById(R.id.qrCodeImage);
        Button btnClose = dialog.findViewById(R.id.btnClose);

        // Définir les textes
        String receiptNumberText = "N° Transaction: " + transactionId;
        String receiptDateText = "Date: " + currentDate;
        String receiptAmountText = String.format("Montant: %.2f €", amount);
        String receiptCardHolderText = "Titulaire: " + cardHolder;
        String receiptCardNumberText = "Carte: " + maskedCard;

        receiptNumber.setText(receiptNumberText);
        receiptDate.setText(receiptDateText);
        receiptAmount.setText(receiptAmountText);
        receiptCardHolder.setText(receiptCardHolderText);
        receiptCardNumber.setText(receiptCardNumberText);

        // Générer le contenu du QR code
        String qrCodeContent = String.format(
                "Transaction: %s\nDate: %s\nMontant: %.2f €\nTitulaire: %s\nCarte: %s",
                transactionId,
                currentDate,
                amount,
                cardHolder,
                maskedCard
        );

        // Générer et afficher le QR code
        Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(qrCodeContent, 500, 500);
        if (qrCodeBitmap != null) {
            qrCodeImage.setImageBitmap(qrCodeBitmap);
        }

        // Configurer le bouton de fermeture
        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            setResult(RESULT_OK);
            finish();
        });

        // Afficher le dialog
        dialog.show();
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
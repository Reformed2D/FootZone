package com.example.versionfinal.payment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextInputLayout amountInputLayout;
    private TextInputEditText amountInput;
    private MaterialButton payButton;
    private DatabaseHelper dbHelper;
    private double totalAmount = 0.00;
    private String userEmail;

    // UI Elements pour le dialog de carte
    private Dialog cardDialog;
    private TextInputEditText cardNumberInput;
    private TextInputEditText expiryDateInput;
    private TextInputEditText cvcInput;
    private TextInputEditText zipInput;
    private AutoCompleteTextView countryDropdown;
    private CheckBox saveCardCheckbox;
    private MaterialButton dialogPayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        dbHelper = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        initializeViews();
        setupClickListeners();
        setupAmountInput();
    }

    private void initializeViews() {
        // Initialize main activity views
        amountInputLayout = findViewById(R.id.amountInput);
        amountInput = findViewById(R.id.amountEditText);
        payButton = findViewById(R.id.payButton);

        // Setup toolbar
        ImageView brandLogo = findViewById(R.id.brandLogo);
        ImageView userIcon = findViewById(R.id.userIcon);

        // Initialize card dialog
        cardDialog = new Dialog(this);
        cardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cardDialog.setContentView(R.layout.dialog_card_input);
        cardDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        // Initialize dialog views
        cardNumberInput = cardDialog.findViewById(R.id.cardNumberInput);
        expiryDateInput = cardDialog.findViewById(R.id.expiryDateInput);
        cvcInput = cardDialog.findViewById(R.id.cvcInput);
        zipInput = cardDialog.findViewById(R.id.zipInput);
        countryDropdown = cardDialog.findViewById(R.id.countryDropdown);
        saveCardCheckbox = cardDialog.findViewById(R.id.saveCardCheckbox);
        dialogPayButton = cardDialog.findViewById(R.id.payButton);

        // Setup country dropdown
        String[] countries = new String[]{"Tunisia", "France", "United States", "Canada", "United Kingdom", "Germany"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                countries
        );
        countryDropdown.setAdapter(adapter);
    }

    private void setupAmountInput() {
        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String amountStr = s.toString().replace("$", "").replace(",", "");
                    if (!amountStr.isEmpty()) {
                        totalAmount = Double.parseDouble(amountStr);
                        updatePayButtonText();
                    }
                } catch (NumberFormatException e) {
                    amountInputLayout.setError("Veuillez entrer un montant valide");
                }
            }
        });
    }

    private void setupClickListeners() {
        // Main payment button
        payButton.setOnClickListener(v -> {
            if (validateAmount()) {
                showCardInputDialog();
            }
        });

        findViewById(R.id.viewHistoryButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentHistoryActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });

        // Dialog pay button
        dialogPayButton.setOnClickListener(v -> processPayment());

        // Setup card number formatting
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() > 0 && !text.contains(" ")) {
                    String formatted = formatCardNumber(text);
                    cardNumberInput.removeTextChangedListener(this);
                    cardNumberInput.setText(formatted);
                    cardNumberInput.setSelection(formatted.length());
                    cardNumberInput.addTextChangedListener(this);
                }
            }
        });

        // Setup expiry date formatting
        expiryDateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() == 2 && !text.contains("/")) {
                    text += "/";
                    expiryDateInput.setText(text);
                    expiryDateInput.setSelection(text.length());
                }
            }
        });
    }

    private boolean validateAmount() {
        if (totalAmount <= 0) {
            amountInputLayout.setError("Veuillez entrer un montant valide");
            return false;
        }
        amountInputLayout.setError(null);
        return true;
    }

    private void showCardInputDialog() {
        if (cardDialog != null) {
            dialogPayButton.setText(String.format("Payer %s", formatAmount(totalAmount)));
            cardDialog.show();
        }
    }

    private void processPayment() {
        if (!validateCardInputs()) {
            return;
        }

        String cardNumber = cardNumberInput.getText().toString();
        String expiryDate = expiryDateInput.getText().toString();
        String cvc = cvcInput.getText().toString();
        String zip = zipInput.getText().toString();
        boolean saveCard = saveCardCheckbox.isChecked();

        // Simulons un paiement réussi
        boolean paymentSuccess = true;

        if (paymentSuccess) {
            // Mettre à jour la base de données
            updatePaymentStatus(true);

            Toast.makeText(this, "Paiement réussi!", Toast.LENGTH_SHORT).show();
            cardDialog.dismiss();

            // Ouvrir PaymentHistoryActivity
            Intent intent = new Intent(this, PaymentHistoryActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);

            finish();
        } else {
            Toast.makeText(this, "Échec du paiement. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateCardInputs() {
        boolean isValid = true;

        String cardNumber = cardNumberInput.getText().toString().replace(" ", "");
        if (cardNumber.length() < 16) {
            cardNumberInput.setError("Numéro de carte invalide");
            isValid = false;
        }

        String expiry = expiryDateInput.getText().toString();
        if (!expiry.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
            expiryDateInput.setError("Date d'expiration invalide");
            isValid = false;
        }

        String cvc = cvcInput.getText().toString();
        if (cvc.length() < 3) {
            cvcInput.setError("CVC invalide");
            isValid = false;
        }

        String zip = zipInput.getText().toString().trim();
        if (zip.length() < 3 || zip.length() > 10) {
            zipInput.setError("Code postal invalide");
            isValid = false;
        }

        return isValid;
    }

    private void updatePaymentStatus(boolean success) {
        dbHelper.recordPayment(
                userEmail,
                totalAmount,
                "CARD",
                cardNumberInput.getText().toString().substring(cardNumberInput.length() - 4)
        );
    }

    private String formatCardNumber(String number) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(number.charAt(i));
        }
        return formatted.toString();
    }

    private String formatAmount(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        return format.format(amount);
    }

    private void updatePayButtonText() {
        if (payButton != null) {
            payButton.setText(String.format("Payer %s", formatAmount(totalAmount)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardDialog != null && cardDialog.isShowing()) {
            cardDialog.dismiss();
        }
    }
}
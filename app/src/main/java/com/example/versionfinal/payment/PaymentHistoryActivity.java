package com.example.versionfinal.payment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;

import java.util.List;

public class PaymentHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PaymentListAdapter adapter;
    private DatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        dbHelper = new DatabaseHelper(this);

        initializeViews();
        loadPayments();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void loadPayments() {
        List<payment> payments = dbHelper.getPayments(userEmail);
        if (payments.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Aucun paiement trouvé");
            emptyView.setGravity(Gravity.CENTER);
            emptyView.setTextSize(16);
            emptyView.setTextColor(Color.parseColor("#666666"));
            ((ViewGroup)recyclerView.getParent()).addView(emptyView);
            recyclerView.setVisibility(View.GONE);
        } else {
            adapter = new PaymentListAdapter(this, payments);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
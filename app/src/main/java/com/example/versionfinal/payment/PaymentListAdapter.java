package com.example.versionfinal.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.versionfinal.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.PaymentViewHolder> {
    private List<payment> payments;
    private Context context;

    public PaymentListAdapter(Context context, List<payment> payments) {
        this.context = context;
        this.payments = payments;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_payment_item, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        payment payment = payments.get(position);

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        holder.amountText.setText(format.format(payment.getAmount()));

        Date date = new Date(payment.getTimestamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.FRANCE);
        holder.dateText.setText(dateFormat.format(date));

        holder.cardText.setText("Carte se terminant par " + payment.getCardNumber());
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView amountText, dateText, cardText;

        PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
            cardText = itemView.findViewById(R.id.cardText);
        }
    }
}
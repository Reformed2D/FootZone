package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReclamationAdapter extends RecyclerView.Adapter<ReclamationAdapter.ReclamationViewHolder> {

    private Context context;
    private ArrayList<Reclamation> reclamations;

    public ReclamationAdapter(Context context, ArrayList<Reclamation> reclamations) {
        this.context = context;
        this.reclamations = reclamations;
    }

    @Override
    public ReclamationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.reclamation_item, parent, false);
        return new ReclamationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReclamationViewHolder holder, int position) {
        Reclamation reclamation = reclamations.get(position);
        holder.sujetTextView.setText(reclamation.getSujet());
        holder.typeTextView.setText(reclamation.getType());
        holder.descriptionTextView.setText(reclamation.getDescription());

        // Logique pour le bouton de suppression, par exemple
        holder.btnDeleteReclamation.setOnClickListener(v -> {
            // Implémenter la suppression de la réclamation si nécessaire
            Toast.makeText(context, "Réclamation supprimée", Toast.LENGTH_SHORT).show();
            // Optionnellement, supprimer l'élément de la base de données et de la liste
        });
    }

    @Override
    public int getItemCount() {
        return reclamations.size();
    }

    public static class ReclamationViewHolder extends RecyclerView.ViewHolder {
        TextView sujetTextView;
        TextView typeTextView;
        TextView descriptionTextView;
        Button btnDeleteReclamation;

        public ReclamationViewHolder(View itemView) {
            super(itemView);
            sujetTextView = itemView.findViewById(R.id.textViewDescription);
            typeTextView = itemView.findViewById(R.id.textViewType);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            btnDeleteReclamation = itemView.findViewById(R.id.btnDeleteReclamation);
        }
    }
}

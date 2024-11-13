package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;

public class ReclamationAdapter extends RecyclerView.Adapter<ReclamationAdapter.ReclamationViewHolder> {
    private Context context;
    private List<HashMap<String, String>> reclamationList;

    public ReclamationAdapter(Context context, List<HashMap<String, String>> reclamationList) {
        this.context = context;
        this.reclamationList = reclamationList;
    }

    @NonNull
    @Override
    public ReclamationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reclamation_item, parent, false);
        return new ReclamationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReclamationViewHolder holder, int position) {
        HashMap<String, String> reclamation = reclamationList.get(position);

        holder.textViewSujet.setText(reclamation.get("sujet"));
        holder.textViewDetails.setText(reclamation.get("details"));
        holder.textViewType.setText(reclamation.get("type"));
        holder.textViewDescription.setText(reclamation.get("description"));
    }

    @Override
    public int getItemCount() {
        return reclamationList.size();
    }

    public static class ReclamationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSujet, textViewDetails, textViewType, textViewDescription;

        public ReclamationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSujet = itemView.findViewById(R.id.textViewSujet);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
            textViewType = itemView.findViewById(R.id.textViewType);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
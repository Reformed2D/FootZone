package com.example.versionfinal.terrain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import com.example.versionfinal.reservation.ReservationActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TerrainAdapter extends RecyclerView.Adapter<TerrainAdapter.ViewHolder> {

    private List<Terrain> terrainList;
    private Context context;
    private DatabaseHelper dbHelper;

    public TerrainAdapter(List<Terrain> terrainList, Context context) {
        this.terrainList = terrainList;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.terrain_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Terrain terrain = terrainList.get(position);

        // Configuration des textes
        holder.nameTextView.setText(terrain.getName());
        holder.addressTextView.setText(terrain.getLocalisation());
        holder.phoneTextView.setText(terrain.getPhone());
        holder.typeTextView.setText(terrain.getType());
        holder.statusTextView.setText(terrain.getStatus());

        // Charger l'image si elle existe
        if (terrain.getImageUri() != null && !terrain.getImageUri().isEmpty()) {
            holder.terrainImageView.setImageURI(Uri.parse(terrain.getImageUri()));
        } else {
            holder.terrainImageView.setImageResource(R.drawable.arena);
        }

        // Click listeners
        setupClickListeners(holder, terrain);
    }

    private void setupClickListeners(@NonNull ViewHolder holder, Terrain terrain) {
        // Image upload
        holder.terrainImageView.setOnClickListener(v -> {
            if (context instanceof TerrainActivity) {
                ((TerrainActivity) context).startImagePicker(terrain.getId());
            }
        });

        // Appel téléphonique
        holder.phoneTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + terrain.getPhone()));
            context.startActivity(intent);
        });

        // Navigation
        holder.addressTextView.setOnClickListener(v -> {
            String locationUrl = "https://www.google.com/maps/search/?api=1&query=" +
                    Uri.encode(terrain.getLocalisation());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl));
            context.startActivity(intent);
        });

        // À propos
        holder.aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTerrainActivity.class);
            intent.putExtra("terrain_id", terrain.getId());
            context.startActivity(intent);
        });

        // Réservation
        holder.reservationButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReservationActivity.class);
            intent.putExtra("terrain_id", terrain.getId());
            context.startActivity(intent);
        });

        // Édition
        holder.iconEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTerrainActivity.class);
            intent.putExtra("terrain_id", terrain.getId());
            ((TerrainActivity) context).startActivityForResult(
                    intent, TerrainActivity.REQUEST_CODE_ADD_EDIT);
        });

        // Suppression
        holder.iconDelete.setOnClickListener(v -> showDeleteDialog(terrain));
    }

    private void showDeleteDialog(Terrain terrain) {
        new AlertDialog.Builder(context)
                .setTitle("Supprimer le terrain")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce terrain ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    dbHelper.deleteTerrain(terrain);
                    terrainList.remove(terrain);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Terrain supprimé", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return terrainList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, phoneTextView, typeTextView, statusTextView;
        ImageView terrainImageView, iconEdit, iconDelete;
        MaterialButton aboutButton, reservationButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text);
            addressTextView = itemView.findViewById(R.id.localisation_text);
            phoneTextView = itemView.findViewById(R.id.phone_text);
            typeTextView = itemView.findViewById(R.id.type_text);
            statusTextView = itemView.findViewById(R.id.status_text);
            terrainImageView = itemView.findViewById(R.id.terrain_image);
            iconEdit = itemView.findViewById(R.id.icon_edit);
            iconDelete = itemView.findViewById(R.id.icon_delete);
            aboutButton = itemView.findViewById(R.id.about_button);
            reservationButton = itemView.findViewById(R.id.reservation_button);
        }
    }
}
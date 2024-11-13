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
        holder.nameTextView.setText(terrain.getName());
        holder.addressTextView.setText(terrain.getLocalisation());
        holder.phoneTextView.setText(terrain.getPhone());
        holder.typeTextView.setText(terrain.getType());
        holder.statusTextView.setText(terrain.getStatus());

        holder.addressTextView.setOnClickListener(v -> {
            String locationUrl = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(terrain.getLocalisation());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl));
            context.startActivity(browserIntent);
        });

        holder.statusTextView.setOnClickListener(v -> showStatusDialog(holder.statusTextView, terrain));

        holder.iconEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTerrainActivity.class);
            intent.putExtra("terrainId", terrain.getId());
            ((TerrainActivity) context).startActivityForResult(intent, TerrainActivity.REQUEST_CODE_ADD_EDIT);
        });

        holder.iconDelete.setOnClickListener(v -> deleteTerrain(terrain));
    }

    @Override
    public int getItemCount() {
        return terrainList.size();
    }

    private void showStatusDialog(TextView statusTextView, Terrain terrain) {
        final String[] statusOptions = {"Disponible", "Non Disponible"};
        new AlertDialog.Builder(context)
                .setTitle("Select Status")
                .setItems(statusOptions, (dialog, which) -> {
                    String selectedStatus = statusOptions[which];
                    statusTextView.setText(selectedStatus);
                    terrain.setStatus(selectedStatus);
                    dbHelper.updateTerrain(terrain);
                })
                .create()
                .show();
    }

    private void deleteTerrain(Terrain terrain) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Terrain")
                .setMessage("Are you sure you want to delete this terrain?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    dbHelper.deleteTerrain(terrain);
                    terrainList.remove(terrain);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Terrain deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, addressTextView, phoneTextView, typeTextView, statusTextView;
        ImageView restaurantImageView, iconEdit, iconDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text);
            addressTextView = itemView.findViewById(R.id.localisation_text);
            phoneTextView = itemView.findViewById(R.id.phone_text);
            typeTextView = itemView.findViewById(R.id.type_text);
            statusTextView = itemView.findViewById(R.id.status_text);
            iconEdit = itemView.findViewById(R.id.icon_edit);
            iconDelete = itemView.findViewById(R.id.icon_delete);
        }
    }
}
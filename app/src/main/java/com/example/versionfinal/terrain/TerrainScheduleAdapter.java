package com.example.versionfinal.terrain;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.versionfinal.R;
import java.util.List;

public class TerrainScheduleAdapter extends RecyclerView.Adapter<TerrainScheduleAdapter.ViewHolder> {
    private List<TerrainScheduleItem> scheduleItems;

    public TerrainScheduleAdapter(List<TerrainScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_terrain_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TerrainScheduleItem item = scheduleItems.get(position);
        holder.terrainNameText.setText(item.getTerrainName());
        holder.timeSlotText.setText(item.getTimeSlot());

        int statusColor = item.isAvailable() ?
                R.color.available_color : R.color.occupied_color;
        String statusText = item.isAvailable() ? "Disponible" : "Occupé";

        // Ajouter le temps restant si disponible
        if (item.isAvailable() && !item.getRemainingTime().isEmpty()) {
            statusText += " (" + item.getRemainingTime() + ")";
        }

        holder.statusText.setText(statusText);
        holder.statusText.setTextColor(ContextCompat.getColor(
                holder.itemView.getContext(), statusColor));
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    public void updateSchedule(List<TerrainScheduleItem> newItems) {
        this.scheduleItems = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView terrainNameText;
        TextView timeSlotText;
        TextView statusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            terrainNameText = itemView.findViewById(R.id.terrainNameText);
            timeSlotText = itemView.findViewById(R.id.timeSlotText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}

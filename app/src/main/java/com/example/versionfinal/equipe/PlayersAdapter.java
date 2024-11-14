package com.example.versionfinal.equipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.versionfinal.R;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder> {
    private List<Player> players;
    private OnPlayerClickListener listener;

    public interface OnPlayerClickListener {
        void onPlayerClick(String position);
    }

    public PlayersAdapter(List<Player> players, OnPlayerClickListener listener) {
        this.players = players;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_recruit, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        holder.usernameTextView.setText(player.getUsername());
        holder.positionTextView.setText(player.getPosition());

        // Set position icon based on player position
        switch (player.getPosition().toLowerCase()) {
            case "goalkeeper":
                holder.positionIcon.setImageResource(R.drawable.ic_goalkeeper);
                break;
            case "defender":
                holder.positionIcon.setImageResource(R.drawable.ic_defender);
                break;
            case "midfielder":
                holder.positionIcon.setImageResource(R.drawable.ic_midfielder);
                break;
            case "forward":
                holder.positionIcon.setImageResource(R.drawable.ic_forward);
                break;
        }

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayerClick(player.getPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView usernameTextView;
        TextView positionTextView;
        ImageView positionIcon;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            usernameTextView = itemView.findViewById(R.id.playerNameTextView);
            positionTextView = itemView.findViewById(R.id.playerPositionTextView);
            positionIcon = itemView.findViewById(R.id.positionIcon);
        }
    }
}
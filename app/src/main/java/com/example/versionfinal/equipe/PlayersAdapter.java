package com.example.versionfinal.equipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.versionfinal.R;
import com.example.versionfinal.equipe.Player;

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
    public PlayersAdapter.PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player_recruit, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayersAdapter.PlayerViewHolder holder, int position) {
        Player player = players.get(position);
        holder.usernameTextView.setText(player.getUsername());
        holder.positionTextView.setText(player.getPosition());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayerClick(player.getPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView positionTextView;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.playerNameTextView);
            positionTextView = itemView.findViewById(R.id.playerPositionTextView);
        }
    }
}
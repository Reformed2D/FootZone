package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private ArrayList<HashMap<String, String>> userList;
    private OnDeleteClickListener onDeleteClickListener;

    public UserAdapter(Context context, ArrayList<HashMap<String, String>> userList, OnDeleteClickListener onDeleteClickListener) {
        this.context = context;
        this.userList = userList;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        HashMap<String, String> user = userList.get(position);
        holder.usernameTextView.setText(user.get("USERNAME"));
        holder.emailTextView.setText(user.get("EMAIL"));
        holder.roleTextView.setText(user.get("ROLE"));

        // Gérer le clic sur le bouton "Delete"
        holder.deleteButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(user.get("ID"));
            }
        });

        // Gérer le clic sur le bouton "Voir Réclamations"
        holder.viewReclamationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReclamationsListActivity.class);
            intent.putExtra("USER_ID", user.get("ID"));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, emailTextView, roleTextView;
        Button deleteButton, viewReclamationsButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            viewReclamationsButton = itemView.findViewById(R.id.viewReclamationsButton);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String userId);
    }
}
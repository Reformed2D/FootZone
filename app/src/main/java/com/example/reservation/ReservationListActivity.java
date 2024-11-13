package com.example.reservation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReservationListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private ReservationDAO reservationDAO;
    private EditText editTextSearch;
    private List<Reservation> allReservations; // Full list of reservations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);

        // Initialize DAO
        reservationDAO = new ReservationDAO(this);
        reservationDAO.open();

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up Search EditText
        editTextSearch = findViewById(R.id.editTextSearch);

        // Load all reservations initially
        loadReservations();

        // Set up the text watcher to filter as you type
        setupSearch();
    }

    private void loadReservations() {
        allReservations = reservationDAO.getAllReservations(); // Fetch all reservations
        adapter = new ReservationAdapter(allReservations); // Initialize adapter with full list
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterReservations(s.toString()); // Filter as user types
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });
    }

    private void filterReservations(String query) {
        List<Reservation> filteredList = new ArrayList<>();

        // If there's a query, filter based on the client name
        for (Reservation reservation : allReservations) {
            if (reservation.getClientName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(reservation);
            }
        }

        // Update the adapter with the filtered list
        adapter.updateList(filteredList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reservationDAO.close(); // Close the DAO connection when activity is destroyed
    }

    // Adapter Class for RecyclerView
    private class ReservationAdapter extends RecyclerView.Adapter<ReservationViewHolder> {
        private List<Reservation> reservations;

        public ReservationAdapter(List<Reservation> reservations) {
            this.reservations = reservations;
        }

        // Method to update the list of reservations
        public void updateList(List<Reservation> newReservations) {
            this.reservations = newReservations;
            notifyDataSetChanged(); // Notify adapter of data changes
        }

        @Override
        public ReservationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReservationViewHolder holder, int position) {
            Reservation reservation = reservations.get(position);
            holder.bind(reservation);
        }

        @Override
        public int getItemCount() {
            return reservations.size();
        }
    }

    // ViewHolder class for each reservation item in the RecyclerView
    private class ReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView clientNameText, fieldNumberText, dateText, timeText;
        private Button editButton, deleteButton;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            clientNameText = itemView.findViewById(R.id.textClientName);
            fieldNumberText = itemView.findViewById(R.id.textFieldNumber);
            dateText = itemView.findViewById(R.id.textDate);
            timeText = itemView.findViewById(R.id.textTime);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(final Reservation reservation) {
            clientNameText.setText("Client: " + reservation.getClientName());
            fieldNumberText.setText("Field: " + reservation.getFieldNumber());
            dateText.setText("Date: " + reservation.getDate());
            timeText.setText("Time: " + reservation.getStartTime());

            // Edit and Delete buttons click listeners
            editButton.setOnClickListener(v -> showEditDialog(reservation));
            deleteButton.setOnClickListener(v -> showDeleteDialog(reservation));
        }
    }

    // Method to show Edit dialog
    private void showEditDialog(final Reservation reservation) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_reservation, null);
        EditText editClientName = dialogView.findViewById(R.id.editClientName);
        EditText editFieldNumber = dialogView.findViewById(R.id.editFieldNumber);
        EditText editDate = dialogView.findViewById(R.id.editDate);
        EditText editTime = dialogView.findViewById(R.id.editTime);
        EditText editDuration = dialogView.findViewById(R.id.editDuration);
        EditText editPrice = dialogView.findViewById(R.id.editPrice);
        EditText editTerrainList = dialogView.findViewById(R.id.editTerrainList);

        // Pre-fill the dialog's fields
        editClientName.setText(reservation.getClientName());
        editFieldNumber.setText(String.valueOf(reservation.getFieldNumber()));
        editDate.setText(reservation.getDate());
        editTime.setText(reservation.getStartTime());
        editDuration.setText(String.valueOf(reservation.getDuration()));
        editPrice.setText(String.valueOf(reservation.getPrice()));
        editTerrainList.setText(reservation.getTerrainListAsString());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Reservation")
                .setView(dialogView)
                .setPositiveButton("Save", (dialogInterface, which) -> {
                    try {
                        reservation.setClientName(editClientName.getText().toString());
                        reservation.setFieldNumber(Integer.parseInt(editFieldNumber.getText().toString()));
                        reservation.setDate(editDate.getText().toString());
                        reservation.setStartTime(editTime.getText().toString());
                        reservation.setDuration(Integer.parseInt(editDuration.getText().toString()));
                        reservation.setPrice(Double.parseDouble(editPrice.getText().toString()));
                        reservation.setTerrainListFromString(editTerrainList.getText().toString());

                        reservationDAO.updateReservation(reservation); // Update the database
                        loadReservations(); // Reload the list to reflect changes
                        Toast.makeText(this, "Reservation updated successfully!", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid input. Please check your fields.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.show();
    }

    // Method to show Delete confirmation dialog
    private void showDeleteDialog(final Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Reservation")
                .setMessage("Are you sure you want to delete this reservation?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    reservationDAO.deleteReservation(reservation.getId());
                    loadReservations(); // Reload the list to reflect removal
                    Toast.makeText(this, "Reservation deleted successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }
}
// TerrainScheduleActivity.java
package com.example.versionfinal.terrain;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.versionfinal.DatabaseHelper;
import com.example.versionfinal.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TerrainScheduleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TerrainScheduleAdapter adapter;
    private DatabaseHelper dbHelper;
    private Spinner dateSpinner;
    private TextView currentTimeText;
    private List<String> dateList;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Handler handler;
    private Timer timer;
    private static final int UPDATE_INTERVAL = 60000; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terrain_schedule);

        dbHelper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        handler = new Handler();

        initializeViews();
        setupDateSpinner();
        setupRecyclerView();
        startTimeUpdates();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.scheduleRecyclerView);
        dateSpinner = findViewById(R.id.dateSpinner);
        currentTimeText = findViewById(R.id.currentTimeText);
        updateCurrentTime(); // Met à jour l'heure immédiatement
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TerrainScheduleAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Charger les données initiales
        String currentDate = dateFormat.format(new Date());
        loadScheduleForDate(currentDate);
    }

    private List<String> generateNextSevenDays() {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            dates.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dates;
    }

    private void startTimeUpdates() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    updateCurrentTime();
                    // Recharger les disponibilités si on affiche la date actuelle
                    String selectedDate = dateList.get(dateSpinner.getSelectedItemPosition());
                    if(selectedDate.equals(dateFormat.format(new Date()))) {
                        loadScheduleForDate(selectedDate);
                    }
                });
            }
        }, 0, UPDATE_INTERVAL);
    }

    private void updateCurrentTime() {
        if (currentTimeText != null) {
            String currentTime = timeFormat.format(new Date());
            currentTimeText.setText("Heure actuelle : " + currentTime);
        }
    }

    private void setupDateSpinner() {
        dateList = generateNextSevenDays();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(spinnerAdapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadScheduleForDate(dateList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadScheduleForDate(String date) {
        List<TerrainScheduleItem> scheduleItems = new ArrayList<>();
        List<Terrain> terrains = dbHelper.getAllTerrains();
        Date currentDate = new Date();
        String currentDateStr = dateFormat.format(currentDate);
        String currentTime = timeFormat.format(currentDate);

        // Générer des créneaux de 1 heure de 8h à 22h
        for (Terrain terrain : terrains) {
            for (int hour = 8; hour <= 22; hour++) {
                String timeSlot = String.format(Locale.getDefault(), "%02d:00", hour);
                String endTimeSlot = String.format(Locale.getDefault(), "%02d:00", hour + 1);

                boolean isAvailable = dbHelper.isTimeSlotAvailable(date, timeSlot, terrain.getId());

                // Si c'est aujourd'hui, vérifier si le créneau n'est pas déjà passé
                if (date.equals(currentDateStr)) {
                    if (timeSlot.compareTo(currentTime) <= 0) {
                        isAvailable = false; // Le créneau est passé
                    }
                }

                TerrainScheduleItem item = new TerrainScheduleItem(
                        terrain.getName(),
                        timeSlot + " - " + endTimeSlot,
                        date,
                        isAvailable,
                        getRemainingTime(date, timeSlot)
                );
                scheduleItems.add(item);
            }
        }

        if (adapter != null) {
            adapter.updateSchedule(scheduleItems);
        }
    }

    private String getRemainingTime(String date, String timeSlot) {
        try {
            Date currentDate = new Date();
            String currentDateStr = dateFormat.format(currentDate);

            // Si la date est aujourd'hui et le créneau n'est pas encore passé
            if (date.equals(currentDateStr)) {
                SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date slotDate = fullFormat.parse(date + " " + timeSlot);

                if (slotDate != null && slotDate.after(currentDate)) {
                    long diff = slotDate.getTime() - currentDate.getTime();
                    long hours = diff / (60 * 60 * 1000);
                    long minutes = (diff % (60 * 60 * 1000)) / (60 * 1000);

                    return String.format(Locale.getDefault(),
                            "Dans %dh %dmin", hours, minutes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
package com.example.versionfinal.reservation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.versionfinal.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ReservationDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Create a new reservation
    public long createReservation(Reservation reservation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CLIENT_NAME, reservation.getClientName());
        values.put(DatabaseHelper.COLUMN_FIELD_NUMBER, reservation.getFieldNumber());
        values.put(DatabaseHelper.COLUMN_DATE, reservation.getDate());
        values.put(DatabaseHelper.COLUMN_START_TIME, reservation.getStartTime());
        values.put(DatabaseHelper.COLUMN_DURATION, reservation.getDuration());
        values.put(DatabaseHelper.COLUMN_PRICE, reservation.getPrice());
        values.put(DatabaseHelper.COLUMN_TERRAIN_LIST, reservation.getTerrainListAsString());

        return database.insert(DatabaseHelper.TABLE_RESERVATIONS, null, values);
    }

    // Read all reservations
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_RESERVATIONS, null, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Reservation reservation = cursorToReservation(cursor);
                    reservations.add(reservation);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } else {
            Log.e("ReservationDAO", "Failed to retrieve reservations: cursor is null");
        }

        return reservations;
    }

    // Read a single reservation by ID
    public Reservation getReservation(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_RESERVATIONS,
                null, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Reservation reservation = cursorToReservation(cursor);
            cursor.close();
            return reservation;
        } else {
            if (cursor != null) cursor.close();
            Log.e("ReservationDAO", "No reservation found with ID: " + id);
            return null; // No reservation found
        }
    }

    // Update an existing reservation
    public int updateReservation(Reservation reservation) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_CLIENT_NAME, reservation.getClientName());
        values.put(DatabaseHelper.COLUMN_FIELD_NUMBER, reservation.getFieldNumber());
        values.put(DatabaseHelper.COLUMN_DATE, reservation.getDate());
        values.put(DatabaseHelper.COLUMN_START_TIME, reservation.getStartTime());
        values.put(DatabaseHelper.COLUMN_DURATION, reservation.getDuration());
        values.put(DatabaseHelper.COLUMN_PRICE, reservation.getPrice());
        values.put(DatabaseHelper.COLUMN_TERRAIN_LIST, reservation.getTerrainListAsString());

        return database.update(DatabaseHelper.TABLE_RESERVATIONS, values,
                DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(reservation.getId())});
    }

    // Delete a reservation
    public void deleteReservation(long id) {
        database.delete(DatabaseHelper.TABLE_RESERVATIONS, DatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Convert Cursor to Reservation object
    private Reservation cursorToReservation(Cursor cursor) {
        Reservation reservation = new Reservation();

        int idIndex = safeGetColumnIndex(cursor, DatabaseHelper.COLUMN_ID);
        if (idIndex != -1) {
            reservation.setId(cursor.getLong(idIndex));
        }

        int clientNameIndex = safeGetColumnIndex(cursor, DatabaseHelper.COLUMN_CLIENT_NAME);
        if (clientNameIndex != -1) {
            reservation.setClientName(cursor.getString(clientNameIndex));
        }

        int fieldNumberIndex = safeGetColumnIndex(cursor, DatabaseHelper.COLUMN_FIELD_NUMBER);
        if (fieldNumberIndex != -1) {
            reservation.setFieldNumber(cursor.getInt(fieldNumberIndex));
        }

        int dateIndex = safeGetColumnIndex(cursor, DatabaseHelper.COLUMN_DATE);
        if (dateIndex != -1) {
            reservation.setDate(cursor.getString(dateIndex));
        }

        int startTimeIndex = safeGetColumnIndex(cursor, DatabaseHelper.COLUMN_START_TIME);
        if (startTimeIndex != -1) {
            reservation.setStartTime(cursor.getString(startTimeIndex));
        }

        int durationIndex = safeGetColumnIndex(cursor, DatabaseHelper.COLUMN_DURATION);
        if (durationIndex != -1) {
            reservation.setDuration(cursor.getInt(durationIndex));
        }

        int priceIndex = safeGetColumnIndex(cursor, DatabaseHelper.COLUMN_PRICE);
        if (priceIndex != -1) {
            reservation.setPrice(cursor.getDouble(priceIndex));
        }

        int terrainListIndex = safeGetColumnIndex(cursor, DatabaseHelper.COLUMN_TERRAIN_LIST);
        if (terrainListIndex != -1) {
            String terrainListString = cursor.getString(terrainListIndex);
            reservation.setTerrainListFromString(terrainListString);
        }

        return reservation;
    }

    // Utility function to safely get column index
    private int safeGetColumnIndex(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index == -1) {
            Log.e("ReservationDAO", "Column " + columnName + " does not exist");
        }
        return index;
    }
}
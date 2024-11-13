package com.example.reservation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reservation {
    private long id; // Unique identifier for the reservation
    private String clientName; // Name of the client
    private int fieldNumber; // Number of the field reserved
    private String date; // Date of the reservation
    private String startTime; // Start time of the reservation
    private int duration; // Duration of the reservation in hours
    private double price; // Price of the reservation
    private List<Integer> terrainList; // List of terrain ids associated with the reservation

    // Constructor
    public Reservation() {
        this.terrainList = new ArrayList<>(); // Initialize with an empty ArrayList
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getFieldNumber() {
        return fieldNumber;
    }

    public void setFieldNumber(int fieldNumber) {
        this.fieldNumber = fieldNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Integer> getTerrainList() {
        return terrainList;
    }

    public void addTerrain(int terrainId) {
        if (!terrainList.contains(terrainId)) {
            terrainList.add(terrainId); // Add terrain id if it's not already in the list
        }
    }

    // Returns the terrain IDs as a comma-separated string
    public String getTerrainListAsString() {
        if (terrainList.isEmpty()) return null;
        StringBuilder result = new StringBuilder();
        for (Integer terrain : terrainList) {
            result.append(terrain).append(","); // Append terrain ids with comma
        }
        result.setLength(result.length() - 1); // Remove the last comma
        return result.toString();
    }

    // Populate terrain list from a comma-separated string
    public void setTerrainListFromString(String terrainListString) {
        if (terrainListString != null && !terrainListString.isEmpty()) {
            String[] items = terrainListString.split(",");
            terrainList.clear(); // Clear the list to avoid duplicates when re-adding
            for (String item : items) {
                terrainList.add(Integer.parseInt(item.trim())); // Convert and add id to the list
            }
        }
    }
}
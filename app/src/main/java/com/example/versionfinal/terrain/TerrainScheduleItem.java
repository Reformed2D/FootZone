
// TerrainScheduleItem.java
package com.example.versionfinal.terrain;

public class TerrainScheduleItem {
    private String terrainName;
    private String timeSlot;
    private String date;
    private boolean available;
    private String remainingTime;

    public TerrainScheduleItem(String terrainName, String timeSlot, String date,
                               boolean available, String remainingTime) {
        this.terrainName = terrainName;
        this.timeSlot = timeSlot;
        this.date = date;
        this.available = available;
        this.remainingTime = remainingTime;
    }
    public String getTerrainName() {
        return terrainName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getDate() {
        return date;
    }
    public String getRemainingTime() {
        return remainingTime;
    }

    public boolean isAvailable() {
        return available;
    }
}
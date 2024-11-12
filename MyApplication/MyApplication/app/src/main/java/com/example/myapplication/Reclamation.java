package com.example.myapplication;

public class Reclamation {
    private String sujet;
    private String details;
    private String type;
    private String description;

    public Reclamation(String sujet, String details, String type, String description) {
        this.sujet = sujet;
        this.details = details;
        this.type = type;
        this.description = description;
    }

    public Reclamation(String description, String type) {
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

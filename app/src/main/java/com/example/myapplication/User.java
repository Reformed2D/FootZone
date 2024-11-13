package com.example.myapplication;

public class User {
    private int id;
    private String username;
    private String position;
    private String email;
    private String role;
    private String birthdate;
    private boolean withTeam;
    private boolean withoutTeam;
    private String profileImage;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isWithTeam() {
        return withTeam;
    }

    public void setWithTeam(boolean withTeam) {
        this.withTeam = withTeam;
    }

    public boolean isWithoutTeam() {
        return withoutTeam;
    }

    public void setWithoutTeam(boolean withoutTeam) {
        this.withoutTeam = withoutTeam;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
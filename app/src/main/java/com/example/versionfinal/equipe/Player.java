package com.example.versionfinal.equipe;

public class Player {
    public String name;
    public String position;

    public Player(String name, String position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }
}
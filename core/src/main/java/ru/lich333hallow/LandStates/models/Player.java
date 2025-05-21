package ru.lich333hallow.LandStates.models;

import lombok.Data;

@Data
public class Player {
    private String playerId;
    private String name;

    public Player(){}

    public Player(String playerId, String name, String color){
        this.playerId = playerId;
        this.name = name;
    }
}

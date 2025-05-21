package ru.lich333hallow.LandStates.models;

import lombok.Data;

@Data
public class PlayerInGame {
    private String name;
    private int balance;
    private int miners;
    private int defenders;
    private int bases;
}

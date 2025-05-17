package ru.lich333hallow.LandStates.models;

import java.util.UUID;

import lombok.Data;

@Data
public class State {
    private int type;

    private UUID playerId;

    private String color;
}

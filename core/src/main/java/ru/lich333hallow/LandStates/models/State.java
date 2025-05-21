package ru.lich333hallow.LandStates.models;


import lombok.Data;

@Data
public class State {
    private int type;
    private PlayerInGame player;
    private int warriors;
}

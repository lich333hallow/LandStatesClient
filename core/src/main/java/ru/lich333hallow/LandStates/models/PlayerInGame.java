package ru.lich333hallow.LandStates.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInGame {
    private String name;
    private int number;
    private String color;
    private int balance;
    private List<State> bases;
}

package ru.lich333hallow.LandStates.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String playerId;
    private String name;
    private String color;
}

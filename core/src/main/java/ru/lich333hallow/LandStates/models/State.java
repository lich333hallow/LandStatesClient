package ru.lich333hallow.LandStates.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.lich333hallow.LandStates.components.Base;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private int id;
    private int type;
    private int food;
    private Base base;
    private int peasants;
    private int miners;
    private int warriors;
    private int sourceId = 0;
}

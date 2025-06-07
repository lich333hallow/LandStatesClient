package ru.lich333hallow.LandStates.clientDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateDTO {
    private int id;
    private int type;
    private int food;
    private int peasants;
    private int miners;
    private int warriors;
    private int sourceId;
}

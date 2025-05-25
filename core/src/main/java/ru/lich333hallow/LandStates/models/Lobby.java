package ru.lich333hallow.LandStates.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lobby {
    private String lobbyId;
    private String hostId;
    private String hostName;
    private String lobbyName;
    private int numberOfPlayers;
    private int timeInSeconds;
    private List<Player> playerDTOS;
    private boolean active;
    private int nowPlayers;
}

package ru.lich333hallow.LandStates.models;

import java.util.List;

import lombok.Data;

@Data
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

    public Lobby(){}

    public Lobby(String lobbyId, String lobbyName, String hostId, String hostName, int numberOfPlayers, int timeInSeconds, List<Player> players, boolean active, int nowPlayers){
        this.lobbyId = lobbyId;
        this.hostId = hostId;
        this.hostName = hostName;
        this.lobbyName = lobbyName;
        this.numberOfPlayers = numberOfPlayers;
        this.timeInSeconds = timeInSeconds;
        this.playerDTOS = players;
        this.active = active;
        this.nowPlayers = nowPlayers;
    }
}

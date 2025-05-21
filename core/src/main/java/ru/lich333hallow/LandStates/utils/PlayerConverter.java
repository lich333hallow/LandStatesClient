package ru.lich333hallow.LandStates.utils;

import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.models.PlayerInGame;

public class PlayerConverter {
    public static PlayerInGame convert(Player player){
        PlayerInGame player1 = new PlayerInGame();

        player1.setName(player.getName());
        player1.setBalance(100);
        player1.setDefenders(0);
        player1.setMiners(0);
        player1.setBases(1);

        return player1;
    }
}

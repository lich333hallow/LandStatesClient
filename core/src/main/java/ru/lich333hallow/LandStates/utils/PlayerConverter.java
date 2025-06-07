package ru.lich333hallow.LandStates.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ru.lich333hallow.LandStates.clientDTO.PlayerDTO;
import ru.lich333hallow.LandStates.components.Base;
import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.models.PlayerInGame;
import ru.lich333hallow.LandStates.clientDTO.StateDTO;
import ru.lich333hallow.LandStates.models.State;

public class PlayerConverter {
    private static int nextId = 0;
    public static PlayerInGame convert(Player player){
        PlayerInGame player1 = new PlayerInGame();

        player1.setName(player.getName());
        player1.setBalance(0);
        player1.setColor(player.getColor());
        player1.setNumber(nextId++);

        return player1;
    }

    public static PlayerDTO convert(PlayerInGame player){
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName(player.getName());
        playerDTO.setNumber(player.getNumber());
        playerDTO.setColor(player.getColor());
        playerDTO.setBalance(player.getBalance());

        List<StateDTO> states = new ArrayList<>();

        player.getBases().forEach(s -> {
            StateDTO state = new StateDTO();

            Base base = s.getBase();

            state.setId(s.getId());
            state.setType(base.getSelectedSector());
            state.setFood(s.getFood());
            state.setPeasants(s.getPeasants());
            state.setMiners(s.getMiners());
            state.setWarriors(s.getWarriors());
            states.add(state);
        });

        playerDTO.setBases(states);
        return playerDTO;
    }

    public static PlayerInGame convert(PlayerDTO playerDTO, List<PlayerInGame> players) {
        PlayerInGame p = new PlayerInGame();

        p.setName(playerDTO.getName());
        p.setNumber(playerDTO.getNumber());
        p.setColor(playerDTO.getColor());
        p.setBalance(playerDTO.getBalance());

        Optional<PlayerInGame> existingPlayer = players.stream()
            .filter(p1 -> p1.getNumber() == p.getNumber())
            .findFirst();

        List<State> existingStates = existingPlayer.map(PlayerInGame::getBases).orElse(Collections.emptyList());
        List<StateDTO> stateDTOS = playerDTO.getBases();
        List<State> merged = new ArrayList<>();

        for (int i = 0; i < stateDTOS.size(); i++) {
            State existingState = i < existingStates.size() ? existingStates.get(i) : new State();
            merged.add(StateConverter.convert(stateDTOS.get(i), existingState));
        }

        p.setBases(merged);

        return p;
    }

    public static List<Player> convert(List<PlayerInGame> playerInGames){
        List<Player> p  = new ArrayList<>();
        playerInGames.forEach(plg -> {
            Player pl = new Player();
            pl.setName(plg.getName());
            pl.setColor(plg.getColor());
            p.add(pl);
        });
        return p;
    }
}

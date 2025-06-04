package ru.lich333hallow.LandStates.utils;

import java.util.ArrayList;
import java.util.List;

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
        player1.setBalance(100);
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

    public static PlayerInGame convert(PlayerDTO playerDTO, List<PlayerInGame> players){
        PlayerInGame p = new PlayerInGame();

        p.setName(playerDTO.getName());
        p.setNumber(playerDTO.getNumber());
        p.setColor(playerDTO.getColor());
        p.setBalance(playerDTO.getBalance());

        List<State> states = players.stream().filter(p1 -> p1.getNumber() == p.getNumber()).findFirst().get().getBases();
        List<StateDTO> stateDTOS = playerDTO.getBases();
        List<State> merged = new ArrayList<>();
        for (int i = 0; i < stateDTOS.size(); i++)  merged.add(StateConverter.convert(stateDTOS.get(i), states.get(i)));
        p.setBases(merged);

        return p;
    }
}

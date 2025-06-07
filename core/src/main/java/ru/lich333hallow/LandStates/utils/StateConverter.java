package ru.lich333hallow.LandStates.utils;

import java.util.ArrayList;
import java.util.List;

import ru.lich333hallow.LandStates.clientDTO.StateDTO;
import ru.lich333hallow.LandStates.models.State;

public class StateConverter {

    public static StateDTO convert(State state){
        StateDTO stateDTO = new StateDTO();

        stateDTO.setId(state.getId());
        stateDTO.setType(state.getType());
        stateDTO.setFood(state.getFood());
        stateDTO.setPeasants(state.getPeasants());
        stateDTO.setMiners(state.getMiners());
        stateDTO.setWarriors(state.getWarriors());
        stateDTO.setSourceId(state.getSourceId());

        return stateDTO;
    }

    public static State convert(StateDTO stateDTO, State state){
        State modifiedState = new State();
        modifiedState.setId(stateDTO.getId());
        modifiedState.setType(stateDTO.getType());
        modifiedState.setFood(stateDTO.getFood());
        modifiedState.setBase(state.getBase());
        modifiedState.setPeasants(stateDTO.getPeasants());
        modifiedState.setWarriors(stateDTO.getWarriors());
        modifiedState.setMiners(stateDTO.getMiners());
        modifiedState.setSourceId(stateDTO.getSourceId());
        return modifiedState;
    }
}

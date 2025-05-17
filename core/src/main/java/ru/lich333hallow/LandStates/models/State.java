package ru.lich333hallow.LandStates.models;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import lombok.Data;

@Data
public class State {
    @SerializedName("type")
    private int type;

    @SerializedName("player_id")
    private UUID playerId;

    @SerializedName("color")
    private Color color;
}

package ru.lich333hallow.LandStates.models;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import lombok.Data;

@Data
public class Player {
    @SerializedName("id")
    private UUID playerId;

    @SerializedName("nickname")
    private String nickName;

    @SerializedName("color")
    private Color color;
}

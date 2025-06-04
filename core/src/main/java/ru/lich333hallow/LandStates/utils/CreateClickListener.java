package ru.lich333hallow.LandStates.utils;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import lombok.Getter;
import lombok.Setter;
import ru.lich333hallow.LandStates.components.Base;

public class CreateClickListener {
    @Getter
    @Setter
    private static int currentPlayerId;

    public static ClickListener createListener(Base base){
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (base.getOwnerId() == getCurrentPlayerId()) {
                    base.selectUnit(x, y);
                }
            }
        };
    }
}

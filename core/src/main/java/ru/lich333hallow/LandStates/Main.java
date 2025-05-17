package ru.lich333hallow.LandStates;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import lombok.Getter;
import lombok.Setter;
import ru.lich333hallow.LandStates.screens.CreatingScreen;
import ru.lich333hallow.LandStates.screens.GameScreen;
import ru.lich333hallow.LandStates.screens.LobbyScreen;
import ru.lich333hallow.LandStates.screens.MenuScreen;
import ru.lich333hallow.LandStates.screens.ResultsScreen;
import ru.lich333hallow.LandStates.screens.SettingsScreen;

@Getter
@Setter
public class Main extends Game {

    private SpriteBatch batch;
    private BitmapFont font72Green;

    private Screen gameScreen;
    private Screen menuScreen;
    private Screen lobbyScreen;
    private Screen settingsScreen;
    private Screen resultsScreen;
    private Screen creatingScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font72Green = new BitmapFont(Gdx.files.internal("fonts/exo272Green.fnt"));

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        lobbyScreen = new LobbyScreen(this);
        settingsScreen = new SettingsScreen(this);
        resultsScreen = new ResultsScreen(this);
        creatingScreen = new CreatingScreen(this);

        setScreen(menuScreen);
    }

    @Override
    public void dispose(){
        batch.dispose();
        font72Green.dispose();
    }
}

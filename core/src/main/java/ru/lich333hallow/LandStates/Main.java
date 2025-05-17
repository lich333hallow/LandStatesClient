package ru.lich333hallow.LandStates;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import java.util.HashMap;

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
    private BitmapFont font72DarkGreenWithOutLine;

    private TextButton.TextButtonStyle menuTextButtonStyle;

    private Texture imageBackGround;
    private Texture coin;

    private Texture miner;
    private Texture defender;
    private Texture base;

    private final HashMap<Integer, Texture> types = new HashMap<>();

    private Screen gameScreen;
    private Screen menuScreen;
    private Screen lobbyScreen;
    private Screen settingsScreen;
    private Screen resultsScreen;
    private Screen creatingScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font72DarkGreenWithOutLine = new BitmapFont(Gdx.files.internal("fonts/exo272DarkGreenWithOutline.fnt"));

        imageBackGround = new Texture(Gdx.files.internal("backgrounds/menu_background.png"));

        NinePatch upPatch = new NinePatch(new Texture(Gdx.files.internal("button/up.png")), 10, 10, 10, 10);
        NinePatch downPatch = new NinePatch(new Texture(Gdx.files.internal("button/down.png")), 10, 10, 10, 10);

        menuTextButtonStyle = new TextButton.TextButtonStyle();
        menuTextButtonStyle.font = font72DarkGreenWithOutLine;
        menuTextButtonStyle.up = new NinePatchDrawable(upPatch);
        menuTextButtonStyle.down = new NinePatchDrawable(downPatch);

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        lobbyScreen = new LobbyScreen(this);
        settingsScreen = new SettingsScreen(this);
        resultsScreen = new ResultsScreen(this);
        creatingScreen = new CreatingScreen(this);

        setScreen(menuScreen);

        types.put(0, base);
        types.put(1, miner);
        types.put(2, defender);
    }

    @Override
    public void dispose(){
        batch.dispose();
        font72DarkGreenWithOutLine.dispose();
        imageBackGround.dispose();
    }
}

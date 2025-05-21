package ru.lich333hallow.LandStates;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.screens.AvailableLobbiesScreen;
import ru.lich333hallow.LandStates.screens.ChooseModeScreen;
import ru.lich333hallow.LandStates.screens.CreatingLobbyScreen;
import ru.lich333hallow.LandStates.screens.GameScreen;
import ru.lich333hallow.LandStates.screens.LobbyScreen;
import ru.lich333hallow.LandStates.screens.MenuScreen;
import ru.lich333hallow.LandStates.screens.PlayerDataScreen;
import ru.lich333hallow.LandStates.screens.ResultsScreen;
import ru.lich333hallow.LandStates.screens.SettingsScreen;

@Getter
@Setter
public class Main extends Game {

    public static final String url = "http://192.168.1.246:8080/api/";
    public static final String urlWebSocket = "ws://192.168.1.246:8080/ws/";

    public static final int cost = 25;

    private SpriteBatch batch;
    private BitmapFont font72DarkGreenWithOutLine;

    private TextButton.TextButtonStyle menuTextButtonStyle;
    private TextField.TextFieldStyle enterNameTextAreaStyle;

    private Player player;

    private Texture imageBackGround;
    private Texture map;
    private Texture coin;

    private Texture miner_1;
    private Texture defender_1;
    private Image base_1;

    private Texture miner_2;
    private Texture defender_2;
    private Image base_2;

    private Texture miner_3;
    private Texture defender_3;
    private Image base_3;

    private Texture miner_4;
    private Texture defender_4;
    private Image base_4;


    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private SettingsScreen settingsScreen;
    private ResultsScreen resultsScreen;
    private ChooseModeScreen chooseModeScreen;
    private CreatingLobbyScreen creatingLobbyScreen;
    private PlayerDataScreen playerDataScreen;
    private LobbyScreen lobbyScreen;
    private AvailableLobbiesScreen availableLobbiesScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font72DarkGreenWithOutLine = new BitmapFont(Gdx.files.internal("fonts/exo272DarkGreenWithOutline.fnt"));

        imageBackGround = new Texture(Gdx.files.internal("backgrounds/menu_background.png"));
        map = new Texture(Gdx.files.internal("backgrounds/map2.png"));

        NinePatch upPatch = new NinePatch(new Texture(Gdx.files.internal("button/up.png")), 10, 10, 10, 10);
        NinePatch downPatch = new NinePatch(new Texture(Gdx.files.internal("button/down.png")), 10, 10, 10, 10);

        menuTextButtonStyle = new TextButton.TextButtonStyle();
        menuTextButtonStyle.font = font72DarkGreenWithOutLine;
        menuTextButtonStyle.up = new NinePatchDrawable(upPatch);
        menuTextButtonStyle.down = new NinePatchDrawable(downPatch);

        base_1 = new Image(new Texture(Gdx.files.internal("bases/1_1.png")));
        miner_1 = new Texture(Gdx.files.internal("bases/2_1.png"));
        defender_1 = new Texture(Gdx.files.internal("bases/3_1.png"));

        base_2 = new Image(new Texture(Gdx.files.internal("bases/1_2.png")));
        miner_2 = new Texture(Gdx.files.internal("bases/2_2.png"));
        defender_2 = new Texture(Gdx.files.internal("bases/3_2.png"));

        base_3 = new Image(new Texture(Gdx.files.internal("bases/1_2.png")));
        miner_3 = new Texture(Gdx.files.internal("bases/2_2.png"));
        defender_3 = new Texture(Gdx.files.internal("bases/3_2.png"));

        base_4 = new Image(new Texture(Gdx.files.internal("bases/1_2.png")));
        miner_4 = new Texture(Gdx.files.internal("bases/2_2.png"));
        defender_4 = new Texture(Gdx.files.internal("bases/3_2.png"));

        coin = new Texture(Gdx.files.internal("coin.png"));

        enterNameTextAreaStyle = new TextField.TextFieldStyle();
        enterNameTextAreaStyle.font = new BitmapFont();
        enterNameTextAreaStyle.fontColor = Color.CYAN;

        player = new Player();
        loadPrefs();

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        settingsScreen = new SettingsScreen(this);
        resultsScreen = new ResultsScreen(this);
        chooseModeScreen = new ChooseModeScreen(this);
        creatingLobbyScreen = new CreatingLobbyScreen(this);
        playerDataScreen = new PlayerDataScreen(this);
        lobbyScreen = new LobbyScreen(this);
        availableLobbiesScreen = new AvailableLobbiesScreen(this);

        setScreen(gameScreen);
    }

    private void loadPrefs(){
        Preferences preferences = Gdx.app.getPreferences("LandStatePrefs");

        String name = preferences.getString("name", null);
        String playerId = preferences.getString("playerId", null);
        if(name == null || playerId == null){
            return;
        }
        player.setName(name);
        player.setPlayerId(playerId);
    }

    @Override
    public void dispose(){
        batch.dispose();

        font72DarkGreenWithOutLine.dispose();

        imageBackGround.dispose();
        map.dispose();
        coin.dispose();

        miner_1.dispose();
        miner_2.dispose();
        miner_3.dispose();
        miner_4.dispose();

        defender_1.dispose();
        defender_2.dispose();
        defender_3.dispose();
        defender_4.dispose();
    }
}

package ru.lich333hallow.LandStates;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    // bbb.eduworks.ru:8080
    // 192.168.1.246
    public static final String url = "http://bbb.eduworks.ru:8080/api/";
    public static final String urlWebSocket = "ws://bbb.eduworks.ru:8080/ws/";

    public static final int cost = 25;

    private SpriteBatch batch;
    private BitmapFont font72DarkGreenWithOutLine;

    private TextButton.TextButtonStyle menuTextButtonStyle;
    private TextField.TextFieldStyle enterNameTextAreaStyle;

    private Player player;

    private Texture imageBackGround;
    private Texture map;
    private Texture coin;

    private Music music;

    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private SettingsScreen settingsScreen;
    private ResultsScreen resultsScreen;
    private ChooseModeScreen chooseModeScreen;
    private CreatingLobbyScreen creatingLobbyScreen;
    private PlayerDataScreen playerDataScreen;
    private LobbyScreen lobbyScreen;
    private AvailableLobbiesScreen availableLobbiesScreen;

    private String stateMusic;


    @Override
    public void create() {
        batch = new SpriteBatch();
        font72DarkGreenWithOutLine = new BitmapFont(Gdx.files.internal("fonts/exo272DarkGreenWithOutline.fnt"));

        imageBackGround = new Texture(Gdx.files.internal("backgrounds/menu_background.png"));
        map = new Texture(Gdx.files.internal("backgrounds/map.png"));

        NinePatch upPatch = new NinePatch(new Texture(Gdx.files.internal("button/up.png")), 10, 10, 10, 10);
        NinePatch downPatch = new NinePatch(new Texture(Gdx.files.internal("button/down.png")), 10, 10, 10, 10);

        music = Gdx.audio.newMusic(Gdx.files.internal("Mystic Cloud - Simpukka chilli.mp3"));
        player = new Player();
        loadPrefs();
        if (stateMusic.equals("Вкл")){
            music.play();
        }
        music.setLooping(true);

        menuTextButtonStyle = new TextButton.TextButtonStyle();
        menuTextButtonStyle.font = font72DarkGreenWithOutLine;
        menuTextButtonStyle.up = new NinePatchDrawable(upPatch);
        menuTextButtonStyle.down = new NinePatchDrawable(downPatch);

        coin = new Texture(Gdx.files.internal("coin.png"));

        enterNameTextAreaStyle = new TextField.TextFieldStyle();
        enterNameTextAreaStyle.font = new BitmapFont();
        enterNameTextAreaStyle.fontColor = Color.CYAN;

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        settingsScreen = new SettingsScreen(this);
        resultsScreen = new ResultsScreen(this);
        chooseModeScreen = new ChooseModeScreen(this);
        creatingLobbyScreen = new CreatingLobbyScreen(this);
        playerDataScreen = new PlayerDataScreen(this);
        lobbyScreen = new LobbyScreen(this);
        availableLobbiesScreen = new AvailableLobbiesScreen(this);


        setScreen(menuScreen);
    }

    private void loadPrefs(){
        Preferences preferences = Gdx.app.getPreferences("LandStatePrefs");

        String name = preferences.getString("name", null);
        String playerId = preferences.getString("playerId", null);
        String color = preferences.getString("color", null);
        stateMusic = preferences.getString("music", "Вкл");
        if(name == null || playerId == null){
            return;
        }
        player.setName(name);
        player.setPlayerId(playerId);
        player.setColor(color);
    }

    @Override
    public void dispose(){
        batch.dispose();

        font72DarkGreenWithOutLine.dispose();

        imageBackGround.dispose();
        map.dispose();
        coin.dispose();
    }
}

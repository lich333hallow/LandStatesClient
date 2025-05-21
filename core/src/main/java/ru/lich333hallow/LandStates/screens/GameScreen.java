package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import lombok.Setter;
import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.components.BaseClickListener;
import ru.lich333hallow.LandStates.models.Lobby;
import ru.lich333hallow.LandStates.models.PlayerInGame;
import ru.lich333hallow.LandStates.network.WebSocketClient;
import ru.lich333hallow.LandStates.utils.PlayerConverter;

public class GameScreen implements Screen {

    private final Main main;
    private final WebSocketClient webSocketClient;
    private final Batch batch;

    private Sprite map;

    private Stage stage;

    private Texture coin;
    private Label coinsLabel;

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

    private Timer timer;

    private PlayerInGame player;

    @Setter
    private Lobby lobby;


    public GameScreen(Main main) {
        this.main = main;
        this.webSocketClient = new WebSocketClient(Main.urlWebSocket + "game");
        batch = main.getBatch();

        lobby = new Lobby();
        lobby.setNumberOfPlayers(4);

        timer = new Timer();

        player = PlayerConverter.convert(main.getPlayer());

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = main.getFont72DarkGreenWithOutLine();
        coinsLabel = new Label("Coins: " + player.getBalance(), labelStyle);
        coinsLabel.setPosition(20, Gdx.graphics.getHeight() - 100);

        coin = main.getCoin();

        map = new Sprite(main.getMap());
        map.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        base_1 = main.getBase_1();
        base_2 = main.getBase_2();
        base_3 = main.getBase_3();
        base_4 = main.getBase_4();

        miner_1 = main.getMiner_1();
        miner_2 = main.getMiner_2();
        miner_3 = main.getMiner_3();
        miner_4 = main.getMiner_4();

        defender_1 = main.getDefender_1();
        defender_2 = main.getDefender_2();
        defender_3 = main.getDefender_3();
        defender_4 = main.getDefender_4();

        float baseSize = 200;
        base_1.setSize(baseSize, baseSize);
        base_2.setSize(baseSize, baseSize);
        base_3.setSize(baseSize, baseSize);
        base_4.setSize(baseSize, baseSize);

        float margin = 100;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        base_1.setPosition(margin, margin);
        base_2.setPosition(screenWidth - baseSize - margin, margin);
        base_3.setPosition(margin, screenHeight - baseSize - margin);
        base_4.setPosition(screenWidth - baseSize - margin, screenHeight - baseSize - margin);

        base_1.addListener(new BaseClickListener(main, base_1, miner_1, defender_1, player));

        stage.addActor(base_1);
        stage.addActor(base_2);
        stage.addActor(base_3);
        stage.addActor(base_4);

        stage.addActor(coinsLabel);
        startCoinGeneration(player);
    }

    private void startCoinGeneration(PlayerInGame player) {
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (player.getMiners() > 0) {
                    player.setBalance(player.getBalance() + 3 * player.getMiners());
                    updateCoins(player);
                } else if (player.getBases() > 0) {
                    player.setBalance(player.getBalance() + player.getBases());
                    updateCoins(player);
                }
            }
        }, 3, 3).run();
    }

    public void updateCoins(PlayerInGame player) {
        Gdx.app.postRunnable(() -> {
            coinsLabel.setText("Coins: " + player.getBalance());
            System.out.println("Updating coins for player: " + player.getBalance());
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        map.draw(batch);
        batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        timer.clear();
    }
}

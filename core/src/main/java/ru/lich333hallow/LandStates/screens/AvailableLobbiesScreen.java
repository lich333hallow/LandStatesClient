package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.components.Button;
import ru.lich333hallow.LandStates.models.Lobby;
import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.network.HttpResponseListener;
import ru.lich333hallow.LandStates.network.NetworkClient;
import ru.lich333hallow.LandStates.utils.JsonParser;

public class AvailableLobbiesScreen implements Screen {

    private final Main main;

    private Stage stage;
    private Table lobbyTable;
    private Table mainTable;

    private Batch batch;
    private Sprite backgroundSprite;

    private ScrollPane scrollPane;

    private Button backButton, refreshButton;

    private Array<Lobby> lobbies = new Array<>();
    private long lastTapTime = 0;
    private static final float DOUBLE_TAP_TIME_THRESHOLD = 0.3f; // 300ms
    private int selectedIndex = -1;

    private final Color HEADER_BG_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.8f);

    public AvailableLobbiesScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        batch = main.getBatch();
        backgroundSprite = new Sprite(main.getImageBackGround());
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Label.LabelStyle headerStyle = new Label.LabelStyle(main.getFont72DarkGreenWithOutLine(), Color.WHITE);

        updateLobbies();

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        lobbyTable = new Table();
        lobbyTable.defaults().pad(5).fillX();

        Table headerTable = new Table();
        headerTable.setBackground(new TextureRegionDrawable(new TextureRegion(createTexture(HEADER_BG_COLOR))));
        headerTable.add(new Label("Название лобби", headerStyle)).width(300).padBottom(10).padTop(10).padLeft(50).padRight(150);
        headerTable.add(new Label("Игроки", headerStyle)).width(150).padBottom(10).padTop(10).padLeft(150);
        headerTable.add(new Label("Хост", headerStyle)).width(300).padBottom(10).padTop(10).padLeft(150);

        scrollPane = new ScrollPane(lobbyTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setOverscroll(false, false);

        mainTable.add(headerTable).fillX().row();
        mainTable.add(scrollPane).expand().fill().pad(10).row();

        Table buttonTable = new Table();
        buttonTable.defaults().padLeft(100);

        backButton = new Button("Назад", main.getMenuTextButtonStyle());
        refreshButton = new Button("Обновить", main.getMenuTextButtonStyle());

        buttonTable.add(backButton).padRight(20);
        buttonTable.add(refreshButton).padRight(20);

        backButton.pack();
        refreshButton.pack();

        mainTable.add(buttonTable).padBottom(50).padTop(10);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getChooseModeScreen());
            }
        });

        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                lobbyTable.clear();
                updateLobbies();
            }
        });
    }

    private void updateLobbyTable() {
        lobbyTable.clear();

        Label.LabelStyle rowStyle = new Label.LabelStyle(main.getFont72DarkGreenWithOutLine(), Color.LIGHT_GRAY);

        for (int i = 0; i < lobbies.size; i++) {
            final int index = i;
            Lobby lobby = lobbies.get(i);

            Table row = new Table();

            row.add(new Label(lobby.getLobbyName(), rowStyle)).width(300).padBottom(10).padTop(10).padLeft(50).padRight(150);
            row.add(new Label(lobby.getNowPlayers() + "/" + lobby.getNumberOfPlayers(), rowStyle)).width(150).padBottom(10).padTop(10).padLeft(150);
            row.add(new Label(lobby.getHostName(), rowStyle)).width(300).padBottom(10).padTop(10).padLeft(150);

            row.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    selectedIndex = index;
                    long currentTime = TimeUtils.nanoTime();
                    float secondsSinceLastTap = (currentTime - lastTapTime) / 1000000000f;
                    if (secondsSinceLastTap < DOUBLE_TAP_TIME_THRESHOLD) {
                        connectToLobby();
                        main.getLobbyScreen().setJoin(true);
                        lastTapTime = 0;
                    } else {
                        lastTapTime = currentTime;
                    }
                    updateLobbyTable();
                }
            });

            lobbyTable.add(row).fillX().row();
            if (i < lobbies.size - 1) {
                lobbyTable.add().height(2).fillX().colspan(3)
                    .padBottom(5).padTop(5)
                    .row();
            }
        }

        scrollPane.layout();
    }

    private void updateLobbies(){
        NetworkClient.get(Main.url + "getActiveLobby", new HttpResponseListener() {
            @Override
            public void onSuccess(int statusCode, String response) {
                Gdx.app.postRunnable(() -> {
                    try {
                        JsonValue jsonValue = JsonParser.parse(response);

                        lobbies.clear();

                        for (JsonValue lobby : jsonValue){
                            Lobby lobby1 = new Lobby();
                            lobby1.setLobbyId(lobby.getString("lobbyId"));
                            lobby1.setHostId(lobby.getString("hostId"));
                            lobby1.setHostName(lobby.getString("hostName"));
                            lobby1.setNumberOfPlayers(lobby.getInt("numberOfPlayers"));
                            lobby1.setTimeInSeconds(lobby.getInt("timeInSeconds"));
                            List<Player> playerList = new ArrayList<>();
                            for (JsonValue player : lobby.get("playerDTOS")){
                                Player player1 = new Player();
                                player1.setPlayerId(player.getString("playerId"));
                                player1.setName(player.getString("name"));
                                playerList.add(player1);
                            }
                            lobby1.setPlayerDTOS(playerList);
                            lobby1.setActive(true);
                            lobby1.setNowPlayers(lobby.getInt("nowPlayers"));
                            lobby1.setLobbyName(lobby.getString("lobbyName"));
                            lobbies.add(lobby1);
                        }

                        updateLobbyTable();

                    } catch (Exception e){
                        Gdx.app.error("LobbyConnect", "Error parsing lobbies: " + e);
                    }
                });
            }

            @Override
            public void onError(int statusCode, String error) {
                Gdx.app.error("findLobbies", "Error: " + error);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(t);
                Gdx.app.error("findLobbies", "Error: " + t.getMessage());
            }
        });
    }

    private void connectToLobby() {
        if (selectedIndex >= 0) {
            Lobby selectedLobby = lobbies.get(selectedIndex);
            selectedLobby.setNowPlayers(selectedLobby.getNowPlayers() + 1);
            selectedLobby.getPlayerDTOS().add(main.getPlayer());
            JsonValue jsonValue = JsonParser.parse(JsonParser.toJson(selectedLobby));

            main.getLobbyScreen().setLobby(selectedLobby);

            NetworkClient.put(Main.url + "updateLobby/" + selectedLobby.getLobbyId(), jsonValue, new HttpResponseListener() {
                @Override
                public void onSuccess(int statusCode, String response) {
                    Gdx.app.postRunnable(() -> main.setScreen(main.getLobbyScreen()));
                }

                @Override
                public void onError(int statusCode, String error) {
                    Gdx.app.error("LobbyConnect", "Error: " + error);
                }

                @Override
                public void onFailure(Throwable t) {
                    Gdx.app.error("LobbyConnectFail", "Error: " + t.getMessage());
                }
            });
            Gdx.app.log("LobbyConnect", "Connecting to: " + selectedLobby.getLobbyName());
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();

        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    private Texture createTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundSprite.setSize(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}

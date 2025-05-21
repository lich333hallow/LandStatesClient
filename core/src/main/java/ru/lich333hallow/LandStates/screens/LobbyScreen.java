package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.components.Button;
import ru.lich333hallow.LandStates.models.Lobby;
import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.network.RunnableCallback;
import ru.lich333hallow.LandStates.network.WebSocketListener;
import ru.lich333hallow.LandStates.network.HttpResponseListener;
import ru.lich333hallow.LandStates.network.NetworkClient;
import ru.lich333hallow.LandStates.network.WebSocketClient;
import ru.lich333hallow.LandStates.utils.JsonParser;

public class LobbyScreen implements Screen {

    private final Main main;
    @Getter
    private final WebSocketClient webSocketClient;

    @Setter
    private boolean join = false;

    private Stage stage;

    private Table mainTable;
    private Table playersTable;

    private Batch batch;
    private Sprite backgroundSprite;

    private Texture whiteTexture;

    private List<Player> players = new ArrayList<>();

    @Setter
    private Lobby lobby;

    private Button readyButton;
    private Button backButton;

    private RunnableCallback runnableCallback;

    public LobbyScreen(Main main) {
        this.main = main;
        this.webSocketClient = main.getCreatingLobbyScreen().getWebSocketClient();

        runnableCallback = new RunnableCallback() {
            @Override
            public void onComplete() {
                webSocketAction(join ? "JOIN" : "CREATE", false);
            }

            @Override
            public void onError(Exception e) {

            }
        };

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1); // white color
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        webSocketClient.addListener(new WebSocketListener() {
            @Override
            public void connected() {
                Gdx.app.log("WebSocketLobbyConnect", "connected");
            }

            @Override
            public void disconnected() {
                Gdx.app.log("WebSocketLobbyConnect", "disconnected");
            }

            @Override
            public void messageReceived(String message) {
                System.out.println("something");
                Gdx.app.postRunnable(() -> handleWebSocketMessage(message));
            }

            @Override
            public void errorOccurred(Throwable error) {
                Gdx.app.error("WebSocketLobbyConnect", "Error: " + error.getMessage());
            }
        });

        batch = main.getBatch();
        backgroundSprite = new Sprite(main.getImageBackGround());
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mainTable = new Table();
        mainTable.setFillParent(true);

        playersTable = new Table();
        playersTable.defaults().pad(10);
        createTableHeader();

        createButtons();

        mainTable.add(playersTable).expand().fill().top();
        mainTable.row();
        mainTable.add(createButtonsTable()).padBottom(50).bottom();

        stage.addActor(mainTable);
        Gdx.input.setInputProcessor(stage);

        loadMockPlayers();
    }

    private void webSocketAction(String type, boolean ready){
        JsonValue joinMessage = new JsonValue(JsonValue.ValueType.object);
        joinMessage.addChild("type", new JsonValue(type));
        joinMessage.addChild("playerName", new JsonValue(main.getPlayer().getName()));
        joinMessage.addChild("lobbyId", new JsonValue(lobby.getLobbyId()));
        joinMessage.addChild("numberOfPlayers", new JsonValue(lobby.getNumberOfPlayers() + ""));
        joinMessage.addChild("ready", new JsonValue(ready));
        webSocketClient.sendMessage(joinMessage.toJson(JsonWriter.OutputType.json));
    }

    private void createTableHeader() {
        playersTable.clear();

        Label nameHeader = new Label("Имя", new Label.LabelStyle(main.getFont72DarkGreenWithOutLine(), Color.WHITE));
        nameHeader.setAlignment(Align.center);
        nameHeader.setColor(1, 1, 0, 1);


        playersTable.add(nameHeader).width(300).padBottom(15);
        playersTable.row();

        Image divider = new Image(whiteTexture);
        divider.setColor(0.3f, 0.3f, 0.3f, 1);
        playersTable.add(divider).colspan(2).height(1).growX().pad(5);
        playersTable.row();
    }

    private void loadMockPlayers() {
        if(join) {
            runnableCallback.onComplete();
            return;
        };
        NetworkClient.get(Main.url + "getLobbyByHost/" + main.getPlayer().getPlayerId() + "/true", new HttpResponseListener() {
            @Override
            public void onSuccess(int statusCode, String response) {
                Gdx.app.postRunnable(() -> {
                    try {
                        JsonValue jsonValue = JsonParser.parse(response);

                        lobby = new Lobby();
                        lobby.setLobbyId(jsonValue.getString("lobbyId"));
                        lobby.setHostId(jsonValue.getString("hostId"));
                        lobby.setHostName(jsonValue.getString("hostName"));
                        lobby.setNumberOfPlayers(jsonValue.getInt("numberOfPlayers"));
                        lobby.setTimeInSeconds(jsonValue.getInt("timeInSeconds"));

                        List<Player> playerList = new ArrayList<>();

                        for (JsonValue player : jsonValue.get("playerDTOS")){
                            Player player1 = new Player();
                            player1.setPlayerId(player.getString("playerId"));
                            player1.setName(player.getString("name"));
                            playerList.add(player1);
                        }

                        lobby.setPlayerDTOS(playerList);
                        lobby.setActive(true);
                        lobby.setNowPlayers(jsonValue.getInt("nowPlayers"));
                        lobby.setLobbyName(jsonValue.getString("lobbyName"));
                        Gdx.app.log("LobbyScreenGetLobby", "Lobby success");
                    } catch (Exception e){
                        Gdx.app.error("LobbyScreenGetLobby", "Error parsing lobbies: " + e);
                    } finally {
                        runnableCallback.onComplete();
                    }
                });
            }

            @Override
            public void onError(int statusCode, String error) {
                Gdx.app.error("LobbyScreenGetLobby", "Error: " + error);
            }

            @Override
            public void onFailure(Throwable t) {
                Gdx.app.error("LobbyScreenGetLobby", "Error: " + t.getMessage());
            }
        });
    }

    private void createButtons() {
        readyButton = new Button("Готов", main.getMenuTextButtonStyle());
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                webSocketAction("READY", true);
            }
        });

        backButton = new Button("Назад", main.getMenuTextButtonStyle());
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                webSocketAction("DISCONNECTED", false);
                webSocketClient.disconnect();
                main.setScreen(main.getChooseModeScreen());
            }
        });
        backButton.pack();
        readyButton.pack();
    }

    private Table createButtonsTable() {
        Table buttonsTable = new Table();

        buttonsTable.add(backButton);
        buttonsTable.add(readyButton).padLeft(40);

        return buttonsTable;
    }

    private void handleWebSocketMessage(String message) {
        try {
            JsonValue jsonD = JsonParser.parse(message);
            String type = jsonD.getString("type");

            if ("LOBBY_STATE".equals(type)) {

                List<Player> newPlayers = new ArrayList<>();

                for (JsonValue player : jsonD.get("players")){
                    Player player1 = new Player();
                    player1.setName(player.getString("name"));
                    newPlayers.add(player1);
                }

                System.out.println(lobby.getLobbyId());

                updatePlayersList(newPlayers);

            } else if ("START_GAME".equals(type)){

                webSocketClient.disconnect();
                main.getGameScreen().setLobby(lobby);
                main.setScreen(main.getGameScreen());
                System.out.println("Start a game");

            }
        } catch (Exception e) {
            Gdx.app.error("WebSocketLobby", "Error: " + e.getMessage());
        }
    }

    private void updatePlayersList(List<Player> newPlayers) {
        players = new ArrayList<>();
        System.out.println(players);
        System.out.println(newPlayers);
        players.addAll(newPlayers);
        createTableHeader();

        for(Player player : players) {
            addPlayerToTable(player.getName());
        }
    }

    private void addPlayerToTable(String name) {
        Label nameLabel = new Label(name, new Label.LabelStyle(main.getFont72DarkGreenWithOutLine(), Color.WHITE));
        nameLabel.setAlignment(Align.center);

        playersTable.add(nameLabel).width(300);
        playersTable.row();

        Image divider = new Image(whiteTexture);
        divider.setColor(0.3f, 0.3f, 0.3f, 1);
        playersTable.add(divider).colspan(2).height(1).growX().pad(5);
        playersTable.row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundSprite.setSize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}

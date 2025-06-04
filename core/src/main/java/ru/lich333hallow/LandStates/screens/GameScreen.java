package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Setter;
import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.clientDTO.PlayerDTO;
import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.models.State;
import ru.lich333hallow.LandStates.components.Base;
import ru.lich333hallow.LandStates.models.Lobby;
import ru.lich333hallow.LandStates.models.PlayerInGame;
import ru.lich333hallow.LandStates.clientDTO.StateDTO;
import ru.lich333hallow.LandStates.network.WebSocketClient;
import ru.lich333hallow.LandStates.network.WebSocketListener;
import ru.lich333hallow.LandStates.utils.CreateClickListener;
import ru.lich333hallow.LandStates.utils.JsonParser;
import ru.lich333hallow.LandStates.utils.PlayerConverter;
import ru.lich333hallow.LandStates.utils.StateConverter;

public class GameScreen implements Screen {

    private final Main main;
    private WebSocketClient webSocketClient;
    private Image map;
    private Stage stage;
    private PlayerInGame player;
    private ShapeRenderer shapeRenderer;
    private Base sourceImage;
    private Base targetImage;
    private Vector2 touchPos;
    private boolean isDragging;
    private ScreenViewport screenViewport;
    private HashMap<Integer, List<State>> playerBases = new HashMap<>();
    private HashMap<Integer, Base> neutrals = new HashMap<>();

    @Setter
    private Lobby lobby;

    private List<PlayerInGame> players = new ArrayList<>();

    public GameScreen(Main main) {
        this.main = main;

        map = new Image(main.getMap());

        map.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    private void actionPlayer(String type, PlayerInGame player, PlayerInGame target, StateDTO state){
        JsonValue joinMessage = new JsonValue(JsonValue.ValueType.object);
        joinMessage.addChild("type", new JsonValue(type));
        joinMessage.addChild("lobbyId", new JsonValue(lobby.getLobbyId()));
        joinMessage.addChild("gameTime", new JsonValue(lobby.getTimeInSeconds()));
        joinMessage.addChild("player", JsonParser.parse(JsonParser.toJson(PlayerConverter.convert(player))));
        joinMessage.addChild("target", JsonParser.parse(JsonParser.toJson(PlayerConverter.convert(target))));
        joinMessage.addChild("state", JsonParser.parse(JsonParser.toJson(state)));
        webSocketClient.sendMessage(joinMessage.toJson(JsonWriter.OutputType.json));
    }

    @Override
    public void show() {
        this.webSocketClient = new WebSocketClient(Main.urlWebSocket + "game");
        screenViewport = new ScreenViewport();
        stage = new Stage(screenViewport);

        /* Это убрать !! */
//        lobby = new Lobby();
//        lobby.setNumberOfPlayers(4);
//        List<Player> s = new ArrayList<>();
//        s.add(main.getPlayer());
//        s.add(new Player(UUID.randomUUID().toString(), "somebody", main.getPlayer().getColor()));
//        s.add(new Player(UUID.randomUUID().toString(), "somebody1", main.getPlayer().getColor()));
//        s.add(new Player(UUID.randomUUID().toString(), "somebody2", main.getPlayer().getColor()));
//        lobby.setPlayerDTOS(s);

        stage.addActor(map);

        shapeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(stage);
        System.out.println("THIS IS GAME LOGS!!!");

        players.addAll(lobby.getPlayerDTOS().stream()
            .map(PlayerConverter::convert)
            .collect(Collectors.toList()));
        player = players.stream().filter(p -> p.getName().equals(main.getPlayer().getName())).findFirst().get();
        CreateClickListener.setCurrentPlayerId(player.getNumber());

        initNeutrals();
        initBases();
        playerBases.forEach((k, v) -> v.forEach(j -> stage.addActor(j.getBase())));
        neutrals.forEach((k, v) -> stage.addActor(v));

        webSocketClient.connect();

        webSocketClient.addListener(new WebSocketListener() {
            @Override
            public void connected() {
                actionPlayer("JOIN", player, player, new StateDTO());
                Gdx.app.log("WebSocketLobbyConnect", "connected");
            }

            @Override
            public void disconnected() {
                Gdx.app.log("WebSocketLobbyConnect", "disconnected");
            }

            @Override
            public void messageReceived(String message) {
                Gdx.app.postRunnable(() -> handleWebSocketMessage(message));
            }

            @Override
            public void errorOccurred(Throwable error) {
                Gdx.app.error("WebSocketLobbyConnect", "Error: " + error.getMessage());
            }
        });
    }

    private void addBaseListener(Base base) {
        base.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(base.getOwnerId() != player.getNumber()) return false;
                sourceImage = (Base) event.getTarget();
                touchPos = new Vector2(
                    event.getStageX(),
                    event.getStageY()
                );
                isDragging = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if(sourceImage != null && sourceImage.getOwnerId() == player.getNumber()) {
                    touchPos.set(event.getStageX(), event.getStageY());
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isDragging) {
                    if (base.getSelectedSector() != -1) changeUnitType(base);
                    Vector2 stageCoordinates = new Vector2(x, y);
                    event.getListenerActor().localToStageCoordinates(stageCoordinates);
                    Actor hitActor = event.getListenerActor().getStage().hit(stageCoordinates.x, stageCoordinates.y, true);
                    if (hitActor != sourceImage &&
                        hitActor instanceof Base) {
                        targetImage = (Base) hitActor;
                        performAction(sourceImage, targetImage);
                    }
                }
                isDragging = false;
                sourceImage = null;
                targetImage = null;
            }
        });
    }

    private void changeUnitType(Base base){
        State state = playerBases.get(base.getOwnerId()).stream().filter(s -> s.getBase().equals(base)).findFirst().get();
        state.setType(state.getBase().getSelectedSector());
        actionPlayer("CHANGE_TYPE", player, player, StateConverter.convert(state));
    }

    private void handleWebSocketMessage(String message) {
        try {
            JsonValue jsonD = JsonParser.parse(message);
            String type = jsonD.getString("type");
            if (type.equals("UPDATE")) {
                PlayerDTO value = JsonParser.convertFromValue(jsonD.get("playerModelInGame"));
                PlayerInGame updatedPlayer = PlayerConverter.convert(value, players);

                players = players.stream()
                    .map(p -> p.getNumber() == updatedPlayer.getNumber() ? updatedPlayer : p)
                    .collect(Collectors.toList());

                updatedPlayer.getBases().forEach(base -> {
                    base.getBase().setTexts(
                        base.getPeasants() + "",
                        base.getWarriors() + "",
                        base.getMiners() + ""
                    );
                });
            }
//            Gdx.app.log("MESSAGE_LOBBY", message);
        } catch (Exception e) {
            Gdx.app.error("WebSocketLobby", "Error: " + e.getMessage());
        }
    }

    private void performAction(Base source, Base target) {
        if(source.getOwnerId() != player.getNumber()) return;
        System.out.println(source);
    }

    private void initNeutrals(){
        for (int i = 0; i < 16; i++) {
            neutrals.put(i, new Base(Color.GRAY, "0", "15", "0", -1, -1));
        }
        setCoordinatesForNeutrals();
    }

    private void setCoordinatesForNeutrals(){
        float width = screenViewport.getWorldWidth();
        float height = screenViewport.getWorldHeight();
        neutrals.get(0).setPosition(width * 0.15f, height * 0.3f);
        neutrals.get(1).setPosition(width * 0.15f, height * 0.4f);
        neutrals.get(2).setPosition(width * 0.15f, height * 0.55f);
        neutrals.get(3).setPosition(width * 0.18f, height * 0.65f);
        neutrals.get(4).setPosition(width * 0.27f, height * 0.73f);
        neutrals.get(5).setPosition(width * 0.25f, height * 0.565f);
        neutrals.get(6).setPosition(width * 0.3f, height * 0.5f);
        neutrals.get(7).setPosition(width * 0.25f, height * 0.39f);
        neutrals.get(8).setPosition(width * 0.32f, height * 0.28f);
        neutrals.get(9).setPosition(width * 0.4f, height * 0.22f);
        neutrals.get(10).setPosition(width * 0.45f, height * 0.08f);
        neutrals.get(11).setPosition(width * 0.6f, height * 0.22f);
        neutrals.get(12).setPosition(width * 0.5f, height * 0.33f);
        neutrals.get(13).setPosition(width * 0.4f, height * 0.4f);
        neutrals.get(14).setPosition(width * 0.45f, height * 0.54f);
        neutrals.get(15).setPosition(width * 0.35f, height * 0.68f);
    }

    private void initBases(){
        for (int i = 0; i < lobby.getNumberOfPlayers(); i++) {
            List<State> states = new ArrayList<>();
            int f = 0;
            if(!(player.getName().equals(players.get(i).getName()))) f = -1;
            states.add(new State(i, 0, 100, new Base(Color.valueOf(players.get(i).getColor().replace("#", "")), "10", "0", "0", i, f), 50, 0, 0));
            players.get(i).setBases(states);
            playerBases.put(i, states);
        }
        setCoordinatesForBases();
    }

    private void setCoordinatesForBases(){
        float width = screenViewport.getWorldWidth();
        float height = screenViewport.getWorldHeight();
        float[] positions = new float[]{
            0.25f, 0.15f,
            0.2f, 0.75f,
            0.73f, 0.73f,
            0.73f, 0.27f,
        };
        for (int i = 0; i < lobby.getNumberOfPlayers(); i++) {
            Base base = playerBases.get(i).get(0).getBase();
            int xIndex = i * 2;
            int yIndex = xIndex + 1;
            float x = width * positions[xIndex];
            float y = height * positions[yIndex];
            base.setPosition(x, y);
            base.addListener(CreateClickListener.createListener(base));
            addBaseListener(base);
        }
    }

    private void drawArrowHead(ShapeRenderer renderer, Vector2 start, Vector2 end) {
        float arrowSize = 15;
        float angle = (float)Math.atan2(end.y - start.y, end.x - start.x);

        Vector2 arrow1 = new Vector2(
            end.x + arrowSize * (float)Math.cos(angle + 2.5f),
            end.y + arrowSize * (float)Math.sin(angle + 2.5f)
        );

        Vector2 arrow2 = new Vector2(
            end.x + arrowSize * (float)Math.cos(angle - 2.5f),
            end.y + arrowSize * (float)Math.sin(angle - 2.5f)
        );

        renderer.triangle(
            end.x, end.y,
            arrow1.x, arrow1.y,
            arrow2.x, arrow2.y
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if (isDragging && sourceImage != null) {
            Vector2 imageCenter = new Vector2(
                sourceImage.getX() + sourceImage.getWidth() / 2f,
                sourceImage.getY() + sourceImage.getHeight() / 2f
            );

            Vector2 touchInStage = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

            float radius = (float) Math.sqrt(Math.pow(sourceImage.getWidth() / 2f, 2) + Math.pow(sourceImage.getHeight() / 2f, 2));
            float distance = imageCenter.dst(touchInStage);

            if (distance > radius) {
                Vector2 direction = new Vector2(touchInStage).sub(imageCenter).nor();
                Vector2 startPoint = new Vector2(imageCenter).mulAdd(direction, radius);

                shapeRenderer.setProjectionMatrix(stage.getCamera().combined);

                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(1, 1, 1, 0.8f);
                shapeRenderer.rectLine(startPoint, touchInStage, 3);
                drawArrowHead(shapeRenderer, startPoint, touchInStage);
                shapeRenderer.end();
                Gdx.gl.glDisable(GL20.GL_BLEND);
            }
        }


    }

    @Override
    public void resize(int width, int height) {
        screenViewport.update(width, height, true);
        setCoordinatesForNeutrals();
        setCoordinatesForBases();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
    }
}


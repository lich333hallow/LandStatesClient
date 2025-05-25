package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.Setter;
import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.models.Lobby;
import ru.lich333hallow.LandStates.models.PlayerInGame;
import ru.lich333hallow.LandStates.network.RunnableCallback;
import ru.lich333hallow.LandStates.network.WebSocketClient;
import ru.lich333hallow.LandStates.network.WebSocketListener;
import ru.lich333hallow.LandStates.utils.JsonParser;
import ru.lich333hallow.LandStates.utils.PlayerConverter;

public class GameScreen implements Screen {

    private final Main main;
    private final WebSocketClient webSocketClient;
    private final Batch batch;

    private final Sprite map;

    private Stage stage;

    private Label coinsLabel;

    private Image base_1;
    private Image base_2;
    private Image base_3;
    private Image base_4;

    private final List<Image> neutrals = new ArrayList<>();
    private final HashMap<String, List<Image>> playerBases = new HashMap<>();

    private PlayerInGame player;

    private ShapeRenderer shapeRenderer;
    private Image sourceImage;
    private Image targetImage;
    private Vector2 touchPos;
    private boolean isDragging;

    private final RunnableCallback runnableCallback;

    @Setter
    private Lobby lobby;

    private Image attacked;

    private final float margin = 100;
    private final float baseSize = 200;
    private final float screenWidth = Gdx.graphics.getWidth();
    private final float screenHeight = Gdx.graphics.getHeight();


    public GameScreen(Main main) {
        this.main = main;
        this.webSocketClient = new WebSocketClient(Main.urlWebSocket + "game");
        batch = main.getBatch();

        map = new Sprite(main.getMap());

        map.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        runnableCallback = new RunnableCallback() {
            @Override
            public void onComplete() {
                actionPlayer("JOIN", player, "none");
            }

            @Override
            public void onError(Exception e) {

            }
        };

    }

    private void createNeutralBases(){
        for (int i = 0; i < 12; i++) {
            Image neutral = new Image(main.getDefender_1().getDrawable());
            neutral.setName("neutral");
            neutral.setSize(baseSize, baseSize);
            neutrals.add(neutral);
        }
        setPosition();
    }


    private void setPosition(){
        float[][] positions = {
            {margin + 200, (screenHeight / 2) - baseSize - 50},
            {(screenWidth / 2) - 300, (screenHeight / 2) - baseSize - 60},
            {(screenWidth / 2) - 450, screenHeight / 2},
            {(screenWidth / 2) - 320, screenHeight - baseSize},
            {screenWidth / 2, margin - 90},
            {(screenWidth / 2) - 120, screenHeight / 2},
            {(screenWidth / 2) + 150, (screenHeight / 2) - baseSize - 30},
            {(screenWidth / 2) + 160, (screenHeight / 2) + margin + 20},
            {screenWidth - baseSize - margin * 4, (screenHeight / 2) - 50},
            {screenWidth - baseSize - margin - 180, (screenHeight / 2) - margin * 3},
            {screenWidth - baseSize - margin * 5.8f, margin + 10},
            {(screenWidth / 2) - 70, screenHeight - baseSize}
        };
        for (int i = 0; i < neutrals.size() && i < positions.length; i++) {
            neutrals.get(i).setPosition(positions[i][0], positions[i][1]);
            stage.addActor(neutrals.get(i));
        }
    }

    private void actionPlayer(String type, PlayerInGame player, String target){
        JsonValue joinMessage = new JsonValue(JsonValue.ValueType.object);
        joinMessage.addChild("type", new JsonValue(type));
        joinMessage.addChild("lobbyId", new JsonValue(lobby.getLobbyId()));
        joinMessage.addChild("name", new JsonValue(player.getName()));
        joinMessage.addChild("target", new JsonValue(target));
        joinMessage.addChild("balance", new JsonValue(player.getBalance()));
        joinMessage.addChild("miners", new JsonValue(player.getMiners()));
        joinMessage.addChild("defenders", new JsonValue(player.getDefenders()));
        joinMessage.addChild("bases", new JsonValue(player.getBases()));
        webSocketClient.sendMessage(joinMessage.toJson(JsonWriter.OutputType.json));
    }

    @Override
    public void show() {
        webSocketClient.connect();
        stage = new Stage(new ScreenViewport());
        shapeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(stage);


        player = PlayerConverter.convert(main.getPlayer());

        System.out.println(player);

        base_1 = main.getBase_1();
        base_2 = main.getBase_2();
        base_3 = main.getBase_3();
        base_4 = main.getBase_4();

        base_1.setSize(baseSize, baseSize);
        base_2.setSize(baseSize, baseSize);
        base_3.setSize(baseSize, baseSize);
        base_4.setSize(baseSize, baseSize);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = main.getFont72DarkGreenWithOutLine();
        coinsLabel = new Label("Воины: 100" , labelStyle);
        coinsLabel.setPosition(20, Gdx.graphics.getHeight() - 100);

        webSocketClient.addListener(new WebSocketListener() {
            @Override
            public void connected() {
                runnableCallback.onComplete();
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

        base_1.setPosition(margin + 300, margin - 90);
        base_2.setPosition(screenWidth - baseSize - margin - 210, 0);
        base_3.setPosition(margin + 200, screenHeight - baseSize - margin - 50);
        base_4.setPosition(screenWidth - baseSize - margin - 210, screenHeight - baseSize - margin);

        base_1.setName("1");
        base_2.setName("2");
        base_3.setName("3");
        base_4.setName("4");

        playerBases.put("1", new ArrayList<>());
        playerBases.put("2", new ArrayList<>());
        playerBases.put("3", new ArrayList<>());
        playerBases.put("4", new ArrayList<>());

        stage.addActor(coinsLabel);
        updateBases();
        createNeutralBases();
    }

    private void updateBases(){
        int players = lobby.getNumberOfPlayers();

        if(players == 4){
            stage.addActor(base_1);
            stage.addActor(base_2);
            stage.addActor(base_3);
            stage.addActor(base_4);
            playerBases.get("1").add(base_1);
            playerBases.get("2").add(base_2);
            playerBases.get("3").add(base_3);
            playerBases.get("4").add(base_4);
        } else if (players == 3){
            stage.addActor(base_1);
            stage.addActor(base_2);
            stage.addActor(base_3);
            playerBases.get("1").add(base_1);
            playerBases.get("2").add(base_2);
            playerBases.get("3").add(base_3);
        } else if (players == 2){
            stage.addActor(base_1);
            stage.addActor(base_2);
            playerBases.get("1").add(base_1);
            playerBases.get("2").add(base_2);
        }
    }

    private void updateListeners(boolean captured){
        if(captured) {
            switch (player.getNumber()) {
                case "1":
                    attacked.setDrawable(base_1.getDrawable());
                    playerBases.get("1").add(attacked);
                    addImageListeners(attacked);
                    break;
                case "2":
                    attacked.setDrawable(base_2.getDrawable());
                    playerBases.get("2").add(attacked);
                    addImageListeners(attacked);
                    break;
                case "3":
                    attacked.setDrawable(base_3.getDrawable());
                    playerBases.get("3").add(attacked);
                    addImageListeners(attacked);
                    break;
                default:
                    attacked.setDrawable(base_4.getDrawable());
                    playerBases.get("4").add(attacked);
                    addImageListeners(attacked);
                    break;
            }
        }
        else {
            switch (player.getNumber()) {
                case "1" -> addImageListeners(base_1);
                case "2" -> addImageListeners(base_2);
                case "3" -> addImageListeners(base_3);
                default -> addImageListeners(base_4);
            }
        }
    }

    private void addImageListeners(Image image) {
        image.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sourceImage = (Image) event.getTarget();
                touchPos = new Vector2(
                    event.getStageX(),
                    event.getStageY()
                );
                isDragging = true;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                touchPos.set(event.getStageX(), event.getStageY());
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isDragging) {
                    Vector2 stageCoords = new Vector2(x, y);
                    event.getListenerActor().localToStageCoordinates(stageCoords);
                    Actor hitActor = event.getListenerActor().getStage().hit(stageCoords.x, stageCoords.y, true);
                    if (hitActor != sourceImage &&
                        hitActor instanceof Image &&
                        playerBases.containsKey(sourceImage.getName()) &&
                        !playerBases.get(sourceImage.getName()).contains(hitActor) &&
                        player.getNumber().equals(sourceImage.getName())) {

                        targetImage = (Image) hitActor;
                        performAction(sourceImage, targetImage);
                    }
                }
                isDragging = false;
                sourceImage = null;
                targetImage = null;
            }
        });
    }

    private void handleWebSocketMessage(String message) {
        try {
            JsonValue jsonD = JsonParser.parse(message);
            String type = jsonD.getString("type");
            if ("CAPTURED".equals(type)){
                player.setBases(player.getBases() + 1);
                updateListeners(true);
            } else if ("FINISH_GAME".equals(type)){
                actionPlayer("FINISH_GAME", player, "none");
            } else if ("JOIN".equals(type)){
                player.setNumber(jsonD.get("playerModelInGame").getString("number"));
                updateListeners(false);
            } else if("WARRIORS_INCREASED".equals(type)) {
                coinsLabel.setText("Воины: " + jsonD.get("playerModelInGame").getString("warriors"));
            }
        } catch (Exception e) {
            System.out.println(message);
            Gdx.app.error("WebSocketLobby", "Error: " + e.getMessage());
        }
    }
    private void performAction(Image source, Image target) {
        actionPlayer("ATTACK", player, target.getName());
        target.setName(source.getName());
        attacked = target;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(map, 0, 0);
        batch.end();

        if (isDragging && sourceImage != null) {
            Vector2 imageCenter = new Vector2(
                sourceImage.getX() + sourceImage.getWidth() / 2,
                sourceImage.getY() + sourceImage.getHeight() / 2
            );

            Vector2 touchInStage = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, 0.8f);

            shapeRenderer.rectLine(imageCenter, touchInStage, 3);

            drawArrowHead(shapeRenderer, imageCenter, touchInStage);

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        stage.act(delta);
        stage.draw();
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
    }
}

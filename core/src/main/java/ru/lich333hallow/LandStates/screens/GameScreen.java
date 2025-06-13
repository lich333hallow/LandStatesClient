package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Setter;
import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.clientDTO.PlayerDTO;
import ru.lich333hallow.LandStates.models.AttackGroup;
import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.models.State;
import ru.lich333hallow.LandStates.components.Base;
import ru.lich333hallow.LandStates.models.Lobby;
import ru.lich333hallow.LandStates.models.PlayerInGame;
import ru.lich333hallow.LandStates.clientDTO.StateDTO;
import ru.lich333hallow.LandStates.models.WarriorProjectile;
import ru.lich333hallow.LandStates.network.WebSocketClient;
import ru.lich333hallow.LandStates.network.WebSocketListener;
import ru.lich333hallow.LandStates.utils.CreateClickListener;
import ru.lich333hallow.LandStates.utils.JsonParser;
import ru.lich333hallow.LandStates.utils.PlayerConverter;
import ru.lich333hallow.LandStates.utils.StateConverter;

public class GameScreen implements Screen {

    private final Main main;
    private WebSocketClient webSocketClient;
    private final Image map;
    private Stage stage;
    private PlayerInGame player;
    private ShapeRenderer shapeRenderer;
    private Base sourceImage;
    private Base targetImage;
    private Vector2 touchPos;
    private boolean isDragging;
    private ScreenViewport screenViewport;
    private final HashMap<Integer, List<State>> playerBases = new HashMap<>();
    private final HashMap<Integer, Base> neutrals = new HashMap<>();

    private BitmapFont balanceFont;
    private GlyphLayout balanceLayout;

    private Base selectedBase;
    private GlyphLayout baseInfoLayout1;
    private BitmapFont baseInfoFont;
    private ShapeRenderer selectionRenderer;
    private boolean showBaseInfo;
    @Setter
    @Getter
    private float selectionLineWidth = 4.0f;

    private final List<WarriorProjectile> warriorProjectiles = new ArrayList<>();
    private final List<AttackGroup> activeAttacks = new ArrayList<>();
    private final Random random = new Random();

    @Setter
    private Lobby lobby;

    private List<PlayerInGame> players = new ArrayList<>();
    private TextureRegion[] textures;

    public GameScreen(Main main) {
        this.main = main;
        map = new Image(main.getMap());
        map.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        map.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                deselectBase();
            }
        });

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

        baseInfoFont = main.getFont72DarkGreenWithOutLine();
        baseInfoLayout1 = new GlyphLayout();
        selectionRenderer = new ShapeRenderer();
        selectionRenderer.setAutoShapeType(true);
        showBaseInfo = false;

        TextureRegion peasantIcon = new TextureRegion(main.getFood());
        TextureRegion warriorIcon = new TextureRegion(main.getSword());
        TextureRegion minerIcon = new TextureRegion(main.getCoin());

        textures = new TextureRegion[]{peasantIcon, warriorIcon, minerIcon};

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

        balanceFont = main.getFont72DarkGreenWithOutLine();

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
        balanceLayout = new GlyphLayout();
        updateBalanceText();
    }

    private void addBaseListener(Base base) {
        base.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(base.getOwnerId() != player.getNumber()) return false;
                sourceImage = (Base) event.getTarget();
                selectBase(base);
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
        State state = playerBases.get(base.getOwnerId()).stream().filter(s -> s.getBase().getId() == base.getId()).findFirst().get();
        state.setType(state.getBase().getSelectedSector());
        actionPlayer("CHANGE_TYPE", player, player, StateConverter.convert(state));
    }

    private void updateBalanceText() {
        balanceLayout.setText(balanceFont, "Баланс: " + player.getBalance());
    }

    private void executeAttack(AttackGroup attack) {
        Optional<Base> targetBase = findBaseById(attack.getTargetId(), attack.getOwnerTarget());
        Optional<PlayerInGame> attacker = players.stream()
            .filter(p -> p.getNumber() == attack.getOwnerId())
            .findFirst();
        if (targetBase.isEmpty() || attacker.isEmpty()) return;
        Base target = targetBase.get();

        if (target.getOwnerId() == -1) {
            State attackState = new State(
                target.getId(), 0, 0, target,
                0, 0, Integer.parseInt(target.getTexts()[1]), attack.getSourceId()
            );
            actionPlayer("NEUTRAL", attacker.get(), attacker.get(),
                StateConverter.convert(attackState));
        }
        else if (target.getOwnerId() != attack.getOwnerId()) {
            Optional<PlayerInGame> defender = players.stream()
                .filter(p -> p.getNumber() == target.getOwnerId())
                .findFirst();
            System.out.println(defender);
            if (defender.isEmpty()) return;

            Optional<State> defendState = defender.get().getBases().stream()
                .filter(s -> s.getBase().getId() == target.getId())
                .findFirst();
            System.out.println(defendState);
            if (defendState.isEmpty()) return;
            System.out.println("ATTACK");
            defendState.get().setSourceId(attack.getSourceId());
            actionPlayer("ATTACK", attacker.get(), defender.get(),
                StateConverter.convert(defendState.get()));
        }
    }

    private void updateWarriorProjectiles(float delta) {
        for (WarriorProjectile projectile : warriorProjectiles) {
            projectile.update(delta);
        }

        Iterator<AttackGroup> attackIterator = activeAttacks.iterator();
        while (attackIterator.hasNext()) {
            AttackGroup attack = attackIterator.next();

            if (attack.isComplete()) {
                Gdx.app.log("Attack", "Executing attack on base " + attack.getTargetId());
                warriorProjectiles.removeIf(p -> p.isArrived() || p.isExpired());
                executeAttack(attack);
                attackIterator.remove();
                warriorProjectiles.removeAll(attack.getProjectiles());
            } else {
                int arrived = (int) attack.getProjectiles().stream()
                    .filter(WarriorProjectile::isArrived)
                    .count();
                Gdx.app.log("Attack", "Attack in progress: " + arrived + "/" + attack.getTotalWarriors() + " arrived");
            }
        }
    }

    private void handleWebSocketMessage(String message) {
        try {
            JsonValue jsonD = JsonParser.parse(message);
            String type = jsonD.getString("type");
            switch (type) {
                case "UPDATE" -> {
                    PlayerDTO value = JsonParser.convertFromValue(jsonD.get("playerModelInGame"));
                    PlayerInGame updatedPlayer = PlayerConverter.convert(value, players);

                    if ((updatedPlayer.getBalance() != player.getBalance()) && (updatedPlayer.getNumber() == player.getNumber())) {
                        player.setBalance(updatedPlayer.getBalance());
                        updateBalanceText();
                    }

                    updatedPlayer.getBases().forEach(base -> {
                        base.getBase().setTexts(
                            base.getPeasants() + "",
                            base.getWarriors() + "",
                            base.getMiners() + ""
                        );
                    });
                    playerBases.put(updatedPlayer.getNumber(), updatedPlayer.getBases());
                    if (selectedBase != null) {
                        if (selectedBase.getOwnerId() == updatedPlayer.getNumber()) {
                            selectedBase = playerBases.get(updatedPlayer.getNumber()).stream().filter(f -> f.getId() == selectedBase.getId()).findFirst().get().getBase();
                            selectBase(selectedBase);
                        }
                    }
                    players = players.stream()
                        .map(p -> p.getNumber() == updatedPlayer.getNumber() ? updatedPlayer : p)
                        .collect(Collectors.toList());
                    warriorProjectiles.forEach(w -> w.updatePlayers(players));
                }
                case "CAPTURED_NEUTRAL" -> {
                    PlayerDTO value = JsonParser.convertFromValue(jsonD.get("playerModelInGame"));
                    PlayerInGame updatedPlayer = PlayerConverter.convert(value, players);

                    State l = updatedPlayer.getBases().get(updatedPlayer.getBases().size() - 1);
                    System.out.println(l);
                    Base b = neutrals.get(l.getSourceId());
                    neutrals.remove(l.getSourceId());
                    b.setId(l.getId());
                    b.setColor(Color.valueOf(updatedPlayer.getColor()));
                    b.setOwnerId(updatedPlayer.getNumber());
                    b.addListener(CreateClickListener.createListener(b));
                    addBaseListener(b);
                    if (updatedPlayer.getName().equals(player.getName())) b.setSelectedSector(0);

                    l.setBase(b);
                    playerBases.get(updatedPlayer.getNumber()).add(l);
                    players = players.stream()
                        .map(p -> p.getNumber() == updatedPlayer.getNumber() ? updatedPlayer : p)
                        .collect(Collectors.toList());
                    warriorProjectiles.forEach(w -> w.updatePlayers(players));
                }
                case "CAPTURED" -> {
                    Gdx.app.log("MESSAGE_LOBBY", message);
                    PlayerDTO attackerDTO = JsonParser.convertFromValue(jsonD.get("playerModelInGame"));
                    PlayerDTO defenderDTO = JsonParser.convertFromValue(jsonD.get("target"));
                    PlayerInGame attacker = PlayerConverter.convert(attackerDTO, players);
                    PlayerInGame defender = PlayerConverter.convert(defenderDTO, players);
                    IntStream.range(0, defender.getBases().size()).forEach(i -> defender.getBases().get(i).setId(i));

                    State attackerState = attacker.getBases().get(attacker.getBases().size() - 1);
                    Base b = playerBases.get(defender.getNumber()).stream()
                        .filter(base -> base.getId() == attackerState.getSourceId())
                        .findFirst()
                        .get().getBase();
                    playerBases.get(defender.getNumber()).removeIf(l -> l.getBase().getId() == b.getId());
                    b.setId(attackerState.getId());
                    b.setColor(Color.valueOf(attacker.getColor()));
                    b.setOwnerId(attacker.getNumber());
                    b.setSelectedSector(-1);
                    if (attacker.getName().equals(player.getName())) b.setSelectedSector(0);

                    attackerState.setBase(b);
                    playerBases.get(attacker.getNumber()).add(attackerState);
                    players = players.stream()
                        .map(p -> p.getNumber() == attacker.getNumber() ? attacker : p)
                        .collect(Collectors.toList());
                    warriorProjectiles.forEach(w -> w.updatePlayers(players));
                    if (defender.getBases().isEmpty() && defender.getNumber() == player.getNumber()) {
                        playerBases.remove(defender.getNumber());
                        main.getResultsScreen().setLobby(lobby);
                    }
                }
                case "FINISH_GAME" -> {
                    List<PlayerDTO> value = new ArrayList<>();
                    jsonD.get("winners").forEach(j -> value.add(JsonParser.convertFromValue(j)));
                    List<PlayerInGame> winners = new ArrayList<>();
                    value.forEach(v -> winners.add(PlayerConverter.convert(v, players)));
                    webSocketClient.disconnect();
                    main.getResultsScreen().setLobby(lobby);
                    main.getResultsScreen().setWinner(PlayerConverter.convert(winners));
                    main.setScreen(main.getResultsScreen());
                }
                default -> Gdx.app.log("MESSAGE_LOBBY", message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Optional<Base> findBaseById(int id, int ownerId) {
        if(ownerId != -1){
            for (List<State> states : playerBases.values()) {
                for (State state : states) {
                    if (state.getBase().getId() == id) {
                        return Optional.of(state.getBase());
                    }
                }
            }
        } else if (ownerId == -1){
            for (Base base : neutrals.values()) {
                if (base.getId() == id) {
                    return Optional.of(base);
                }
            }
        }
        return Optional.empty();
    }

    private void performAction(Base source, Base target) {
        Optional<PlayerInGame> attacker = players.stream()
            .filter(pl -> pl.getNumber() == source.getOwnerId())
            .findFirst();

        if (attacker.isEmpty()) return;
        Optional<State> sourceState = attacker.get().getBases().stream()
            .filter(s -> s.getBase().getId() == source.getId())
            .findFirst();

        if(sourceState.isEmpty() || sourceState.get().getWarriors() <= 0) return;

        Vector2 sourceCenter = new Vector2(
            source.getX() + source.getWidth() / 2,
            source.getY() + source.getHeight() / 2
        );
        Vector2 targetCenter = new Vector2(
            target.getX() + target.getWidth() / 2,
            target.getY() + target.getHeight() / 2
        );

        int warriorsToSend = sourceState.get().getWarriors();
        for (int i = 0; i < warriorsToSend; i++) {
            Vector2 startPos = new Vector2(sourceCenter)
                .add(random.nextFloat() * 20 - 10, random.nextFloat() * 20 - 10);
            warriorProjectiles.add(new WarriorProjectile(
                startPos,
                targetCenter,
                source.getOwnerId(),
                target.getId(),
                players,
                players.stream().filter(p -> p.getNumber() == source.getOwnerId()).findFirst().get(),
                source.getId()
            ));
        }
        AttackGroup attack = new AttackGroup(
            source.getId(),
            target.getId(),
            source.getOwnerId(),
            warriorsToSend,
            target.getOwnerId()
        );

        activeAttacks.add(attack);

        String peas = sourceState.get().getBase().getTexts()[0];
        String miners = sourceState.get().getBase().getTexts()[2];
        String warriors = String.valueOf(sourceState.get().getWarriors() - warriorsToSend);
        sourceState.get().getBase().setTexts(peas, warriors, miners);
        sourceState.get().setWarriors(sourceState.get().getWarriors() - warriorsToSend);
    }

    private void initNeutrals(){
        for (int i = 0; i < 27; i++) {
            neutrals.put(i, new Base(i, Color.GRAY, "0", "5", "0", -1, -1, textures));
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
        neutrals.get(16).setPosition(width * 0.4f, height * 0.8f);
        neutrals.get(17).setPosition(width * 0.47f, height * 0.7f);
        neutrals.get(18).setPosition(width * 0.68f, height * 0.85f);
        neutrals.get(19).setPosition(width * 0.65f, height * 0.73f);
        neutrals.get(20).setPosition(width * 0.6f, height * 0.6f);
        neutrals.get(21).setPosition(width * 0.55f, height * 0.43f);
        neutrals.get(22).setPosition(width * 0.73f, height * 0.6f);
        neutrals.get(23).setPosition(width * 0.7f, height * 0.48f);
        neutrals.get(24).setPosition(width * 0.8f, height * 0.48f);
        neutrals.get(25).setPosition(width * 0.78f, height * 0.37f);
        neutrals.get(26).setPosition(width * 0.65f, height * 0.35f);
    }

    private void initBases(){
        for (int i = 0; i < lobby.getNumberOfPlayers(); i++) {
            List<State> states = new ArrayList<>();
            int f = 0;
            if(!(player.getName().equals(players.get(i).getName()))) f = -1;
            states.add(new State(i, 0, 100, new Base(i, Color.valueOf(players.get(i).getColor().replace("#", "")), "10", "0", "0", i, f, textures), 10, 0, 0, 0));
            players.get(i).setBases(states);
            playerBases.put(i, states);
        }

        /* Убрать */
//        playerBases.get(0).get(0).setWarriors(16);
//        playerBases.get(0).get(0).getBase().setTexts("10", "16", "0");

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

    private void selectBase(Base base) {
        deselectBase();
        selectedBase = base;
        showBaseInfo = true;
        updateSelectBase();
    }

    private void deselectBase() {
        selectedBase = null;
        showBaseInfo = false;
        baseInfoLayout1.setText(baseInfoFont, "");
    }

    private void updateSelectBase(){
        if(selectedBase != null){
            Optional<State> state = playerBases.get(selectedBase.getOwnerId()).stream()
                .filter(s -> s.getBase().getId() == selectedBase.getId())
                .findFirst();
            state.ifPresent(value -> baseInfoLayout1.setText(baseInfoFont, "Еда: " + value.getFood()));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateWarriorProjectiles(delta);

        stage.act(delta);
        stage.draw();

        float textX = 20;
        float textY = Gdx.graphics.getHeight() - 20;
        stage.getBatch().begin();
        balanceFont.draw(stage.getBatch(), balanceLayout, textX, textY);
        if (showBaseInfo && selectedBase != null) {
            float infoX = Gdx.graphics.getWidth() - 300;
            float infoY = Gdx.graphics.getHeight() - 50;
            baseInfoFont.draw(stage.getBatch(), baseInfoLayout1, infoX, infoY);
        }
        stage.getBatch().end();

        if (selectedBase != null) {
            drawBaseSelection();
        }

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (WarriorProjectile projectile : warriorProjectiles) {
            projectile.render(shapeRenderer);
        }

        for (AttackGroup attack : activeAttacks) {
            if (attack.isComplete()) continue;

            Optional<Base> targetBase = findBaseById(attack.getTargetId(), attack.getTargetId());
            if (targetBase.isEmpty()) continue;

            Base target = targetBase.get();
            float radius = Math.max(target.getWidth(), target.getHeight()) / 2;
            Vector2 center = new Vector2(
                target.getX() + target.getWidth()/2,
                target.getY() + target.getHeight()/2
            );

            int arrivedCount = (int) attack.getProjectiles().stream()
                .filter(WarriorProjectile::isArrived).count();
            for (int i = 0; i < arrivedCount; i++) {
                float angle = (float)i / arrivedCount * 360f;
                Vector2 pos = new Vector2(
                    center.x + (radius + 15) * (float)Math.cos(Math.toRadians(angle)),
                    center.y + (radius + 15) * (float)Math.sin(Math.toRadians(angle))
                );

                shapeRenderer.setColor(attack.getProjectiles().get(0).getOwnerColor());
                shapeRenderer.circle(pos.x, pos.y, 5);
            }
        }

        shapeRenderer.end();
        if (isDragging && sourceImage != null) {
            drawDragArrow();
        }
    }

    private void drawBaseSelection() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glLineWidth(selectionLineWidth);

        selectionRenderer.setProjectionMatrix(stage.getCamera().combined);
        selectionRenderer.begin(ShapeRenderer.ShapeType.Line);
        selectionRenderer.setColor(Color.YELLOW);

        float centerX = selectedBase.getX() + selectedBase.getWidth() / 2;
        float centerY = selectedBase.getY() + selectedBase.getHeight() / 2;
        float radius = Math.max(selectedBase.getWidth(), selectedBase.getHeight()) / 2;

        selectionRenderer.circle(centerX, centerY, radius, 100);
        selectionRenderer.end();

        Gdx.gl.glLineWidth(1.0f);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawDragArrow() {
        Vector2 imageCenter = new Vector2(
            sourceImage.getX() + sourceImage.getWidth() / 2f,
            sourceImage.getY() + sourceImage.getHeight() / 2f
        );
        Vector2 touchInStage = stage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        float radius = (float) Math.sqrt(Math.pow(sourceImage.getWidth() / 2f, 2) + Math.pow(sourceImage.getHeight() / 2f, 2));
        float distance = imageCenter.dst(touchInStage);

        if (distance > radius) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, 0.8f);

            Vector2 direction = new Vector2(touchInStage).sub(imageCenter).nor();
            Vector2 startPoint = new Vector2(imageCenter).mulAdd(direction, radius);
            shapeRenderer.rectLine(startPoint, touchInStage, 3);
            drawArrowHead(shapeRenderer, startPoint, touchInStage);

            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
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
        balanceFont.dispose();
        baseInfoFont.dispose();
        selectionRenderer.dispose();
    }
}


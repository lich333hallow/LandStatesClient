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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketListener;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.components.Button;
import ru.lich333hallow.LandStates.models.Lobby;
import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.network.HttpResponseListener;
import ru.lich333hallow.LandStates.network.NetworkClient;
import ru.lich333hallow.LandStates.network.WebSocketClient;
import ru.lich333hallow.LandStates.utils.JsonParser;

public class CreatingLobbyScreen implements Screen {

    private final Main main;
    @Getter
    private final WebSocketClient webSocketClient;
    private Stage stage;

    private Table rootTable;
    private Table leftTable;
    private Table rightTable;
    private Table bottomTable;

    private Sprite backgroundSprite;
    private Batch batch;

    private Label lobbyNameLabel;
    private Label gameTimeLabel;
    private Label playersCountLabel;

    private TextField lobbyNameField;
    private SelectBox<String> gameTimeSelect;
    private SelectBox<String> playersCountSelect;

    private Pixmap textField;
    private Pixmap bgPixmap;
    private Pixmap selectionPixmap;

    private Button createButton;

    public CreatingLobbyScreen(Main main){
        this.main = main;
        this.webSocketClient = new WebSocketClient(Main.urlWebSocket + "lobby");
        webSocketClient.connect();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        rootTable = new Table();
        leftTable = new Table();
        rightTable = new Table();
        bottomTable = new Table();

        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        createButton = new Button("Создать", main.getMenuTextButtonStyle());
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Lobby lobby = new Lobby();
                List<Player> playerList = new ArrayList<>();
                playerList.add(main.getPlayer());
                lobby.setLobbyName(lobbyNameField.getText());
                lobby.setNumberOfPlayers(Integer.parseInt(playersCountSelect.getSelected()));
                lobby.setTimeInSeconds((gameTimeSelect.getSelected().equals("5 минут")) ? 300 : 900);
                lobby.setHostId(main.getPlayer().getPlayerId());
                lobby.setPlayerDTOS(playerList);

                String s = JsonParser.toJson(lobby);

                NetworkClient.post(Main.url + "createLobby", JsonParser.parse(s), new HttpResponseListener() {
                    @Override
                    public void onSuccess(int statusCode, String response) {
                        main.setScreen(main.getLobbyScreen());
                        Gdx.app.log("CreateLobby", "success");
                    }

                    @Override
                    public void onError(int statusCode, String error) {
                        Gdx.app.error("CreateLobby", "Error: " + error);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Gdx.app.error("CreateLobby", "Error: " + t.getMessage());
                    }
                });
            }
        });
        createButton.pack();

        lobbyNameLabel = new Label("Название лобби:", new Label.LabelStyle(main.getFont72DarkGreenWithOutLine(), Color.WHITE));
        gameTimeLabel = new Label("Время игры:", new Label.LabelStyle(main.getFont72DarkGreenWithOutLine(), Color.WHITE));
        playersCountLabel = new Label("Количество игроков:", new Label.LabelStyle(main.getFont72DarkGreenWithOutLine(), Color.WHITE));

        leftTable.add(lobbyNameLabel).padBottom(100).row();
        leftTable.add(gameTimeLabel).padBottom(100).row();
        leftTable.add(playersCountLabel).row();

        textField = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        textField.setColor(0, 0, 0, 0.5f);
        textField.fill();
        TextureRegionDrawable text = new TextureRegionDrawable(new Texture(textField));

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = main.getFont72DarkGreenWithOutLine();
        textFieldStyle.fontColor = Color.GREEN;
        textFieldStyle.background = text;

        lobbyNameField = new TextField("", textFieldStyle);
        lobbyNameField.setMaxLength(15);
        lobbyNameField.setAlignment(Align.center);

        bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.9f);
        bgPixmap.fill();
        TextureRegionDrawable background = new TextureRegionDrawable(new Texture(bgPixmap));

        selectionPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        selectionPixmap.setColor(0, 0, 0, 0.9f);
        selectionPixmap.fill();
        TextureRegionDrawable selection = new TextureRegionDrawable(new Texture(selectionPixmap));

        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();

        selectBoxStyle.font = main.getFont72DarkGreenWithOutLine();
        selectBoxStyle.fontColor = Color.WHITE;

        selectBoxStyle.background = background;

        selectBoxStyle.listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle();
        selectBoxStyle.listStyle.font = main.getFont72DarkGreenWithOutLine();
        selectBoxStyle.listStyle.fontColorSelected = Color.WHITE;
        selectBoxStyle.listStyle.fontColorUnselected = Color.LIGHT_GRAY;
        selectBoxStyle.listStyle.selection = selection;

        selectBoxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();
        selectBoxStyle.scrollStyle.background = background;
        selectBoxStyle.scrollStyle.vScroll = background;
        selectBoxStyle.scrollStyle.vScrollKnob = selection;

        gameTimeSelect = new SelectBox<>(selectBoxStyle);
        Array<String> gameTimeOptions = new Array<>();
        gameTimeOptions.addAll("5 минут", "15 минут");
        gameTimeSelect.setItems(gameTimeOptions);

        playersCountSelect = new SelectBox<>(selectBoxStyle);
        Array<String> playersCountOptions = new Array<>();
        playersCountOptions.addAll("2", "3", "4");
        playersCountSelect.setItems(playersCountOptions);

        bottomTable.add(createButton).padTop(50).row();

        rightTable.add(lobbyNameField).width(300).height(60).padBottom(100).row();
        rightTable.add(gameTimeSelect).width(300).height(60).padBottom(100).row();
        rightTable.add(playersCountSelect).width(300).height(60).row();

        rootTable.add(leftTable).width(stage.getWidth() / 2).expandY().fill();
        rootTable.add(rightTable).width(stage.getWidth() / 2).expandY().fill();
        rootTable.row();
        rootTable.add(bottomTable).colspan(2).center().padBottom(50);

        batch = main.getBatch();

        backgroundSprite = new Sprite(main.getImageBackGround());
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

        rootTable.clear();
        rootTable.add(leftTable).width(width / 2f).expandY().fill();
        rootTable.add(rightTable).width(width / 2f).expandY().fill();
        rootTable.row();
        rootTable.add(bottomTable).colspan(2).center().padBottom(50);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        bgPixmap.dispose();
        selectionPixmap.dispose();
    }
}

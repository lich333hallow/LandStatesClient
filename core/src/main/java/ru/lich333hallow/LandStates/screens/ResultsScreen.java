package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Setter;
import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.models.Lobby;
import ru.lich333hallow.LandStates.models.Player;

public class ResultsScreen implements Screen {

    private final Main main;

    @Setter
    private Lobby lobby;
    @Setter
    private List<Player> winner;

    private Image background;
    private Stage stage;
    private Table playersTable;
    private Label winnerLabel;
    private TextButton backButton;

    private Texture whiteTexture;
    public ResultsScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1); // white color
        pixmap.fill();
        whiteTexture = new Texture(pixmap);

        background = new Image(main.getImageBackGround());
        background.setSize(stage.getWidth(), stage.getHeight());

        /* Это убрать !! */
//        lobby = new Lobby();
//        lobby.setNumberOfPlayers(4);
//        List<Player> s = new ArrayList<>();
//        s.add(main.getPlayer());
//        s.add(new Player(UUID.randomUUID().toString(), "somebody", main.getPlayer().getColor()));
//        s.add(new Player(UUID.randomUUID().toString(), "somebody1", main.getPlayer().getColor()));
//        s.add(new Player(UUID.randomUUID().toString(), "somebody2", main.getPlayer().getColor()));
//        lobby.setPlayerDTOS(s);
//        winner = new ArrayList<>();
//        winner.add(main.getPlayer());

        BitmapFont font = main.getFont72DarkGreenWithOutLine();
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.GREEN);

        TextButton.TextButtonStyle textButtonStyle = main.getMenuTextButtonStyle();

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        String winnersText;
        if (winner.isEmpty()) {
            winnersText = "Нет победителей";
        } else if (winner.size() == 1) {
            winnersText = "Победитель - " + winner.get(0).getName();
        } else {
            winnersText = "Победители - " + winner.stream()
                .map(Player::getName)
                .collect(Collectors.joining(", "));
        }

        winnerLabel = new Label(winnersText, labelStyle);
        winnerLabel.setAlignment(Align.center);
        mainTable.add(winnerLabel).colspan(2).padBottom(30).row();

        playersTable = new Table();
        playersTable.defaults().pad(10);

        Label numberHeader = new Label("N", labelStyle);
        Label nameHeader = new Label("Имя игрока", labelStyle);

        playersTable.add(numberHeader).width(100).center();
        addVerticalSeparator(playersTable);
        playersTable.add(nameHeader).width(400).center();
        playersTable.row();

        addHorizontalSeparator(playersTable);

        for (int i = 0; i < lobby.getPlayerDTOS().size(); i++) {
            Player player = lobby.getPlayerDTOS().get(i);

            playersTable.add(new Label(String.valueOf(i + 1), labelStyle)).width(100).center();
            addVerticalSeparator(playersTable);
            playersTable.add(new Label(player.getName(), labelStyle)).width(400).center();
            playersTable.row();

            if (i < lobby.getPlayerDTOS().size() - 1) {
                addHorizontalSeparator(playersTable);
            }
        }

        mainTable.add(playersTable).colspan(2).pad(20).row();

        backButton = new TextButton("Главное меню", textButtonStyle);
        backButton.pack();
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getMenuScreen());
            }
        });

        mainTable.add(backButton).colspan(2).padTop(30);

        stage.addActor(background);
        stage.addActor(mainTable);
    }

    private void addHorizontalSeparator(Table table) {
        Table separator = new Table();
        separator.setBackground(new Image(whiteTexture).getDrawable());
        table.add(separator).colspan(3).height(2).width(600).padBottom(5).row();
    }

    private void addVerticalSeparator(Table table) {
        Table separator = new Table();
        separator.setBackground(new Image(whiteTexture).getDrawable());
        table.add(separator).width(2).height(72).padRight(5);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        background.setSize(width, height);
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
        whiteTexture.dispose();
    }
}

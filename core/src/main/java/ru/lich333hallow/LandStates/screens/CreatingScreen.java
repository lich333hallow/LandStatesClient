package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.lich333hallow.LandStates.Main;

public class CreatingScreen implements Screen {

    private final Main main;
    private Stage stage;

    private BitmapFont font72Green;

    private Label findGame;
    private Label createMultiplayerGame;
    private Label createSoloGame;

    public CreatingScreen(Main main){
        this.main = main;

        font72Green = main.getFont72Green();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle style = new Label.LabelStyle(font72Green, Color.GREEN);

        findGame = new Label("Найти игру", style);
        createMultiplayerGame = new Label("Создать лобби", style);
        createSoloGame = new Label("Одиночная игра", style);

        findGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        createMultiplayerGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        createSoloGame.addListener(new ClickListener(){
            // TO-DO
        });

        stage.addActor(findGame);
        stage.addActor(createMultiplayerGame);
        stage.addActor(createSoloGame);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {

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

    }
}

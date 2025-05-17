package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.lich333hallow.LandStates.Main;

public class SettingsScreen implements Screen {
    private final Main main;

    private Stage stage;
    private BitmapFont font72Green;

    private Label title;
    private Label language;
    private Label music;
    private Label back;

    private String currentLanguage = "Русский";
    private String currentMusic = "Вкл";

    public SettingsScreen(Main main) {
        this.main = main;
        font72Green = main.getFont72Green();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font72Green, Color.GREEN);

        title = new Label("Настройки", labelStyle);
        language = new Label("Язык: " + currentLanguage, labelStyle);
        music = new Label("Музыка: " + currentMusic, labelStyle);
        back = new Label("Вернуться", labelStyle);

        table.add(title).padBottom(40).row();
        table.add(language).padBottom(20).row();
        table.add(music).padBottom(20).row();
        table.add(back);

        language.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentLanguage = currentLanguage.equals("Русский") ? "Английский" : "Русский";
                language.setText("Язык: " + currentLanguage);
            }
        });

        music.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentMusic = currentMusic.equals("Вкл") ? "Выкл" : "Вкл";
                music.setText("Музыка: " + currentMusic);
            }
        });

        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getMenuScreen());
            }
        });
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
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

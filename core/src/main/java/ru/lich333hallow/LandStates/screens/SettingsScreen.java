package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.components.Button;

public class SettingsScreen implements Screen {
    private final Main main;

    private Stage stage;

    private Button language;
    private Button music;
    private Button back;

    private Sprite backgroundSprite;
    private Batch batch;

    private String currentLanguage = "English";
    private String currentMusic = "Вкл";

    public SettingsScreen(Main main) {
        this.main = main;

        batch = main.getBatch();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);


        backgroundSprite = new Sprite(main.getImageBackGround());
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        language = new Button("Язык: " + currentLanguage, main.getMenuTextButtonStyle());
        music = new Button("Музыка: " + currentMusic, main.getMenuTextButtonStyle());
        back = new Button("Назад", main.getMenuTextButtonStyle());

        language.pack();
        music.pack();
        back.pack();

        table.add(language).padBottom(20).padRight(20).row();
        table.add(music).padBottom(20).padRight(20).row();
        table.add(back);

        language.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentLanguage = currentLanguage.equals("Русский") ? "English" : "Русский";
                language.setText("Язык: " + currentLanguage);
                language.pack();
            }
        });

        music.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentMusic = currentMusic.equals("Вкл") ? "Выкл" : "Вкл";
                music.setText("Музыка: " + currentMusic);
                music.pack();
            }
        });

        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getMenuScreen());
            }
        });
        stage.addActor(table);
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

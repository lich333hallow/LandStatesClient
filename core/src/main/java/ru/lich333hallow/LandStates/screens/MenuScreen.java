package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.lich333hallow.LandStates.Main;


public class MenuScreen implements Screen {

    private final Main main;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font72Green;

    private Texture imageBackGround;

    private Stage stage;
    private Table table;

    private Label play;
    private Label settings;
    private Label exit;

    public MenuScreen(Main main) {
        this.main = main;

        font72Green = main.getFont72Green();
        batch = main.getBatch();

        table = new Table();
        stage = new Stage(new ScreenViewport());

        Label.LabelStyle labelStyle = new Label.LabelStyle(font72Green, Color.GREEN);

        play = new Label("Создать лобби", labelStyle);
        settings = new Label("Настройки", labelStyle);
        exit = new Label("Выйти", labelStyle);

        imageBackGround = new Texture(Gdx.files.internal("backgrounds/space0.png"));

        play.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getCreatingScreen());
            }
        });

        settings.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getSettingsScreen());
            }
        });

        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(play).padBottom(50).row();
        table.add(settings).padBottom(50).row();
        table.add(exit).row();
        table.setFillParent(true);
        table.center();

        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.graphics.setForegroundFPS(10);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,  0,  0 , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);
        stage.act();
        stage.draw();
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
        stage.dispose();
    }
}

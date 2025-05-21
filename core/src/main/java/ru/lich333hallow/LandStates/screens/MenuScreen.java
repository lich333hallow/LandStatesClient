package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.components.Button;
import ru.lich333hallow.LandStates.models.Player;


public class MenuScreen implements Screen {

    private final Main main;
    private final SpriteBatch batch;

    private final Sprite backgroundSprite;

    private final Stage stage;
    private Table table;

    private Button play;
    private Button settings;
    private Button exit;

    public MenuScreen(Main main) {
        this.main = main;

        batch = main.getBatch();

        table = new Table();
        stage = new Stage(new ScreenViewport());

        backgroundSprite = new Sprite(main.getImageBackGround());
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        play = new Button("Играть", main.getMenuTextButtonStyle());
        settings = new Button("Настройки", main.getMenuTextButtonStyle());
        exit = new Button("Выйти", main.getMenuTextButtonStyle());

        play.pack();
        settings.pack();
        exit.pack();

        play.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                Player player = main.getPlayer();

                if (player.getName() == null || player.getPlayerId() == null){
                   main.setScreen(main.getPlayerDataScreen());
                   return;
                }

                main.setScreen(main.getChooseModeScreen());
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
        Gdx.graphics.setForegroundFPS(30);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,  0,  0 , 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.input.setInputProcessor(stage);

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
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}

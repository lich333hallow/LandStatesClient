package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.components.Button;

public class ChooseModeScreen implements Screen {

    private final Main main;
    private Stage stage;
    private Table table;
    private Batch batch;

    private Sprite backgroundSprite;

    private Button findGame;
    private Button createMultiplayerGame;
//    private Button createSoloGame;
    private Button back;

    public ChooseModeScreen(Main main){
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        table = new Table();
        Gdx.input.setInputProcessor(stage);

        batch = main.getBatch();

        backgroundSprite = new Sprite(main.getImageBackGround());
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        findGame = new Button("Найти игру", main.getMenuTextButtonStyle());
        createMultiplayerGame = new Button("Создать лобби", main.getMenuTextButtonStyle());
//        createSoloGame = new Button("Одиночная игра", main.getMenuTextButtonStyle());
        back = new Button("Назад", main.getMenuTextButtonStyle());

        findGame.pack();
        createMultiplayerGame.pack();
//        createSoloGame.pack();
        back.pack();

        findGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getAvailableLobbiesScreen());
            }
        });

        createMultiplayerGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getCreatingLobbyScreen());
            }
        });

//        createSoloGame.addListener(new ClickListener(){
//            // TO-DO
//        });

        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                main.setScreen(main.getMenuScreen());
            }
        });

        table.add(findGame).padBottom(50).row();
        table.add(createMultiplayerGame).padBottom(50).row();
//        table.add(createSoloGame).padBottom(50).row();
        table.add(back);
        table.setFillParent(true);
        table.center();

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
    }
}

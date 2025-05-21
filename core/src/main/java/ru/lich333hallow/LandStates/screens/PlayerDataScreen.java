package ru.lich333hallow.LandStates.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.UUID;

import ru.lich333hallow.LandStates.Main;
import ru.lich333hallow.LandStates.components.NameInputWidget;
import ru.lich333hallow.LandStates.models.Player;
import ru.lich333hallow.LandStates.network.HttpResponseListener;
import ru.lich333hallow.LandStates.network.NetworkClient;
import ru.lich333hallow.LandStates.utils.JsonParser;

public class PlayerDataScreen implements Screen {

    private final Main main;

    private Stage stage;

    private Batch batch;
    private Sprite backgroundSprite;

    private Player player;

    private NameInputWidget nameInputWidget;

    public PlayerDataScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        batch = main.getBatch();
        backgroundSprite = new Sprite(main.getImageBackGround());
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        player = main.getPlayer();

        nameInputWidget = new NameInputWidget(main.getFont72DarkGreenWithOutLine());

        nameInputWidget.getSubmitButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = nameInputWidget.getTextField().getText();
                player.setName(name);
                player.setPlayerId(UUID.randomUUID().toString());

                String jsonValue = JsonParser.toJson(main.getPlayer());

                NetworkClient.post(Main.url + "createPlayer", JsonParser.parse(jsonValue), new HttpResponseListener() {

                    @Override
                    public void onSuccess(int statusCode, String response) {
                        Gdx.app.log("PlayerData", "success");
                    }

                    @Override
                    public void onError(int statusCode, String error) {
                        Gdx.app.error("PlayerData", "Error: " + error);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Gdx.app.error("PlayerData", "Error:" + t.getMessage());
                    }
                });

                savePrefs();
                main.setScreen(main.getChooseModeScreen());
            }
        });

        stage.addActor(nameInputWidget);
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

    private void savePrefs(){
        Preferences preferences = Gdx.app.getPreferences("LandStatePrefs");
        preferences.putString("name", main.getPlayer().getName());
        preferences.putString("playerId", main.getPlayer().getPlayerId());
        preferences.flush();
    }
}

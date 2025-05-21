package ru.lich333hallow.LandStates.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import lombok.Getter;

@Getter
public class NameInputWidget extends Table {
    private TextField textField;
    private TextButton submitButton;
    private Label label;

    public NameInputWidget(BitmapFont font) {
        super();
        createLargeStyles(font);
        setupLargeWidgets();
        layoutLargeWidgets();
    }

    private void createLargeStyles(BitmapFont font) {
        int fieldWidth = 600;
        int fieldHeight = 120;
        int buttonSize = 120;

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.background = createLargeRoundedRectangle(fieldWidth, fieldHeight, Color.LIGHT_GRAY);
        textFieldStyle.cursor = createLargeCursor((int) font.getLineHeight());

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = createLargeRoundedRectangle(buttonSize, buttonSize, Color.GREEN);
        buttonStyle.down = createLargeRoundedRectangle(buttonSize, buttonSize, Color.DARK_GRAY);

        Skin tempSkin = new Skin();
        tempSkin.add("default", labelStyle);
        tempSkin.add("default", textFieldStyle);
        tempSkin.add("default", buttonStyle);

        this.setSkin(tempSkin);
    }

    private TextureRegionDrawable createLargeRoundedRectangle(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);

        int borderSize = 4;
        pixmap.setColor(new Color(color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, 1));
        pixmap.fillRectangle(0, 0, width, borderSize);
        pixmap.fillRectangle(0, height-borderSize, width, borderSize);
        pixmap.fillRectangle(0, 0, borderSize, height);
        pixmap.fillRectangle(width-borderSize, 0, borderSize, height);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private TextureRegionDrawable createLargeCursor(int height) {
        Pixmap pixmap = new Pixmap(4, (int)(height * 0.8f), Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private void setupLargeWidgets() {
        label = new Label("Введите имя", getSkin());

        textField = new TextField("", getSkin());
        textField.setMessageText("Макс. 8 символов");
        textField.setMaxLength(8);

        submitButton = new TextButton("→", getSkin());
    }

    private void layoutLargeWidgets() {
        this.setFillParent(true);
        this.center();

        this.defaults().pad(15);

        this.add(label).colspan(2).padBottom(30).row();
        this.add(textField).width(600).height(120).padRight(20);
        this.add(submitButton).size(120, 120);
    }

}

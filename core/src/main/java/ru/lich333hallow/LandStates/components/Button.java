package ru.lich333hallow.LandStates.components;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class Button extends TextButton {

    private final GlyphLayout layout = new GlyphLayout();
    private boolean sizeInvalid = true;
    private float minWidth = 0;
    private float minHeight = 0;

    public Button(String text, TextButtonStyle style) {
        super(text, style);
        updateSize();
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        sizeInvalid = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        super.draw(batch, parentAlpha);
    }

    @Override
    public void validate() {
        if (sizeInvalid) {
            updateSize();
            sizeInvalid = false;
        }
        super.validate();
    }

    private void updateSize() {
        layout.setText(getStyle().font, getText());

        float width = layout.width + getStyle().up.getLeftWidth() + getStyle().up.getRightWidth();
        float height = layout.height + getStyle().up.getTopHeight() + getStyle().up.getBottomHeight();

        setSize(Math.max(width, minWidth), Math.max(height, minHeight));
    }
}

package ru.lich333hallow.LandStates.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

import lombok.Getter;
import lombok.Setter;

public class Base extends Actor {
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Color color;
    private String[] texts;
    @Setter
    @Getter
    private int selectedSector = 0;
    private final float markerRadius = 10f;
    @Getter
    private int ownerId;

    // 0 - peasants
    // 1 - warriors
    // 2 - miners

    public Base(Color color, String peasants, String warriors, String miners, int ownerId, int selectedSector) {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        this.color = color;
        this.texts = new String[]{peasants, warriors, miners};
        this.ownerId = ownerId;
        this.selectedSector = selectedSector;

        setSize(75, 75);
    }

    public void setTexts(String peasants, String warriors, String miners){
        this.texts = new String[]{peasants, warriors, miners};
    }

    public void selectUnit(float x, float y){
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float radius = Math.min(getWidth(), getHeight()) / 2;

        float distance = (float) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));

        if (distance <= radius) {
            float angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
            if (angle < 0) angle += 360;

            selectedSector = (int) (angle / 120);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float centerX = getX() + getWidth() / 2;
        float centerY = getY() + getHeight() / 2;
        float radius = Math.min(getWidth(), getHeight()) / 2;

        batch.end();

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(color);
        shapeRenderer.arc(centerX, centerY, radius, 0, 120, 100);
        shapeRenderer.arc(centerX, centerY, radius, 120, 120, 100);
        shapeRenderer.arc(centerX, centerY, radius, 240, 120, 100);

        if (selectedSector >= 0) {
            float markerAngle = selectedSector * 120 + 60;
            float markerX = (float) (centerX + (radius + markerRadius * 1.5) * Math.cos(Math.toRadians(markerAngle)));
            float markerY = (float) (centerY + (radius + markerRadius * 1.5) * Math.sin(Math.toRadians(markerAngle)));

            shapeRenderer.setColor(Color.LIGHT_GRAY);
            shapeRenderer.circle(markerX, markerY, markerRadius);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        for (int i = 0; i < 3; i++) {
            float angle = i * 120;
            float x = (float) (centerX + radius * Math.cos(Math.toRadians(angle)));
            float y = (float) (centerY + radius * Math.sin(Math.toRadians(angle)));
            shapeRenderer.line(centerX, centerY, x, y);
        }

        shapeRenderer.circle(centerX, centerY, radius);

        shapeRenderer.end();

        batch.begin();

        for (int i = 0; i < 3; i++) {
            float textAngle = i * 120 + 60;
            float textRadius = radius * 0.5f;
            float textX = centerX + (float) (textRadius * Math.cos(Math.toRadians(textAngle))) - radius / 2;
            float textY = centerY + (float) (textRadius * Math.sin(Math.toRadians(textAngle))) + font.getCapHeight() / 2;

            font.draw(batch, texts[i], textX, textY, radius, Align.center, false);
        }
    }

    @Override
    public boolean remove() {
        shapeRenderer.dispose();
        font.dispose();
        return super.remove();
    }
}

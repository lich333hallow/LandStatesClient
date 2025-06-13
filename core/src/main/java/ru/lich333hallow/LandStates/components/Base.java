package ru.lich333hallow.LandStates.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Base extends Actor {
    private int id;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Color color;
    private String[] texts;
    private int selectedSector = 0;
    private final float markerRadius = 10f;
    private final float markerSize = 35f;
    private int ownerId;
    private TextureRegion[] sectorIcons;

    public static final int PEASANTS_SECTOR = 0;
    public static final int WARRIORS_SECTOR = 1;
    public static final int MINERS_SECTOR = 2;

    public Base(int id, Color color, String peasants, String warriors, String miners, int ownerId, int selectedSector, TextureRegion[] sectorIcons) {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        this.id = id;
        this.color = color;
        this.texts = new String[]{peasants, warriors, miners};
        this.ownerId = ownerId;
        this.selectedSector = selectedSector;
        this.sectorIcons = sectorIcons;

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

        if (selectedSector >= 0 && sectorIcons != null && selectedSector < sectorIcons.length
            && sectorIcons[selectedSector] != null) {
            float markerAngle = selectedSector * 120 + 60;
            float markerX = (float) (centerX + (radius + markerSize / 2) * Math.cos(Math.toRadians(markerAngle)) - markerSize / 2);
            float markerY = (float) (centerY + (radius + markerSize / 2) * Math.sin(Math.toRadians(markerAngle)) - markerSize / 2);

            batch.draw(sectorIcons[selectedSector], markerX, markerY, markerSize, markerSize);
        }

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

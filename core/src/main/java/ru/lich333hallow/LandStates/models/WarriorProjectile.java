package ru.lich333hallow.LandStates.models;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Optional;

import lombok.Data;

@Data
public class WarriorProjectile {
    private Vector2 position;
    private Vector2 target;
    private Vector2 direction;
    private float speed;
    private int ownerId;
    private int targetId;
    private int sourceId;
    private boolean arrived;
    private float radius = 5f;
    private List<PlayerInGame> players;
    private PlayerInGame player;
    private Color ownerColor;
    private float timeAlive;
    private static final float MAX_TIME_ALIVE = 10f;

    public WarriorProjectile(Vector2 start, Vector2 target, int ownerId, int targetId, List<PlayerInGame> players, PlayerInGame player, int sourceId) {
        this.position = new Vector2(start);
        this.target = new Vector2(target);
        this.direction = new Vector2(target).sub(start).nor();
        this.speed = random.nextFloat() * 50 + 100;
        this.ownerId = ownerId;
        this.targetId = targetId;
        this.arrived = false;
        this.players = players;
        this.player = player;
        this.sourceId = sourceId;
        this.ownerColor = Color.valueOf(player.getColor());
    }

    public void updatePlayers(List<PlayerInGame> players) {
        this.players = players;
        this.player = players.stream().filter(p -> p.getName().equals(player.getName())).findFirst().get();
    }

    public void update(float delta) {
        if (arrived) return;

        timeAlive += delta;

        float distance = position.dst(target);
        if (distance < 5.0f) {
            arrived = true;
            return;
        }

        Vector2 direction = new Vector2(target).sub(position).nor();
        position.add(direction.x * speed * delta, direction.y * speed * delta);
    }

    public void render(ShapeRenderer renderer) {
        Color color = Color.WHITE;
        if (ownerId == player.getNumber()) {
            color = Color.valueOf(player.getColor());
        } else {
            Optional<PlayerInGame> owner = players.stream()
                .filter(p -> p.getNumber() == ownerId)
                .findFirst();
            if (owner.isPresent()) {
                color = Color.valueOf(owner.get().getColor());
            }
        }

        renderer.setColor(color);
        renderer.circle(position.x, position.y, radius);
    }

    public boolean isExpired() {
        return timeAlive >= MAX_TIME_ALIVE;
    }
}

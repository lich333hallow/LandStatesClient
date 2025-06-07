package ru.lich333hallow.LandStates.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AttackGroup {
    private int sourceId;
    private int targetId;
    private int ownerId;
    private int ownerTarget;
    private List<WarriorProjectile> projectiles = new ArrayList<>();
    private int totalWarriors;

    public AttackGroup(int sourceId, int targetId, int ownerId, int totalWarriors, int ownerTarget) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.ownerId = ownerId;
        this.totalWarriors = totalWarriors;
        this.ownerTarget = ownerTarget;
    }

    public boolean isComplete() {
        return projectiles.stream().allMatch(WarriorProjectile::isArrived);
    }
}

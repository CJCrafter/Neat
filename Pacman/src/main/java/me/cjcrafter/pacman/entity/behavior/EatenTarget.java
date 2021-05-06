package me.cjcrafter.pacman.entity.behavior;

import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.entity.Entity;
import me.cjcrafter.pacman.entity.Ghost;

public class EatenTarget implements Behavior {

    public static final EatenTarget INSTANCE = new EatenTarget();

    private EatenTarget() {
    }

    @Override
    public Vector2i getTarget(Ghost ghost) {
        return ghost.getSpawnLocation().clone().divide(Entity.TILE_SIZE);
    }
}

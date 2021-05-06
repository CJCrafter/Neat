package me.cjcrafter.pacman.entity.behavior;

import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.entity.Ghost;

public class ScatterTarget implements Behavior {

    public static final ScatterTarget INSTANCE = new ScatterTarget();

    private ScatterTarget() {
    }

    @Override
    public Vector2i getTarget(Ghost ghost) {
        return ghost.getScatterLocation();
    }
}

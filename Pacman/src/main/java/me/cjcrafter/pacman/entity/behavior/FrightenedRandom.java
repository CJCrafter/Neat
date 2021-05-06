package me.cjcrafter.pacman.entity.behavior;

import me.cjcrafter.pacman.Direction;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.entity.Ghost;

public class FrightenedRandom implements Behavior {

    public static final FrightenedRandom INSTANCE = new FrightenedRandom();

    private FrightenedRandom() {
    }

    @Override
    public Direction getDirection(Ghost ghost) {
        return random(ghost);
    }

    @Override
    public Vector2i getTarget(Ghost ghost) {
        return null;
    }
}

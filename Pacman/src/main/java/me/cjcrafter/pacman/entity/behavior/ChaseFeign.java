package me.cjcrafter.pacman.entity.behavior;

import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.entity.Ghost;

public class ChaseFeign implements Behavior {

    public static final ChaseFeign INSTANCE = new ChaseFeign();

    private ChaseFeign() {
    }

    @Override
    public Vector2i getTarget(Ghost ghost) {
        Vector2i location = ghost.getBoard().getPlayer().getTile();

        if (location.distanceSquared(ghost.getTile()) > 64) {
            return location;
        } else {
            return ghost.getScatterLocation();
        }
    }
}

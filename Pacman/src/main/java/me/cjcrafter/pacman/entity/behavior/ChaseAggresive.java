package me.cjcrafter.pacman.entity.behavior;

import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.entity.Ghost;

public class ChaseAggresive implements Behavior {

    public static final ChaseAggresive INSTANCE = new ChaseAggresive();

    private ChaseAggresive() {
    }

    @Override
    public Vector2i getTarget(Ghost ghost) {
        return ghost.getBoard().getPlayer().getTile();
    }
}

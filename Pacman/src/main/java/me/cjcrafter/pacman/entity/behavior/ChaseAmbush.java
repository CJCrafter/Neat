package me.cjcrafter.pacman.entity.behavior;

import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.entity.Ghost;
import me.cjcrafter.pacman.entity.Player;

public class ChaseAmbush implements Behavior {

    public static final ChaseAmbush INSTANCE = new ChaseAmbush();

    private ChaseAmbush() {
    }

    @Override
    public Vector2i getTarget(Ghost ghost) {
        Player player = ghost.getBoard().getPlayer();
        return player.getDirection().getVector().multiply(4).add(player.getTile());
    }
}

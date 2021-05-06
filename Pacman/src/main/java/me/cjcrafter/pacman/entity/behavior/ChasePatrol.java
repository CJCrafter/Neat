package me.cjcrafter.pacman.entity.behavior;

import me.cjcrafter.pacman.Direction;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.entity.Ghost;
import me.cjcrafter.pacman.entity.Player;

public class ChasePatrol implements Behavior {

    public static final ChasePatrol INSTANCE = new ChasePatrol();

    private ChasePatrol() {
    }

    @Override
    public Vector2i getTarget(Ghost ghost) {
        Ghost temp = null;

        for (Ghost g : ghost.getBoard().getGhosts()) {
            if (g != ghost && g.chase instanceof ChaseAggresive){
                temp = g;
                break;
            }
        }

        if (temp == null)
            throw new IllegalStateException("No aggressive ghosts found");


        Player player = ghost.getBoard().getPlayer();
        Direction dir = player.getDirection();
        Vector2i playerTarget = player.getTile().clone().add(dir.getDx() * 2, dir.getDy() * 2);

        Vector2i between = playerTarget.subtract(temp.getTile());
        between.multiply(2).add(temp.getTile());
        return between;
    }
}

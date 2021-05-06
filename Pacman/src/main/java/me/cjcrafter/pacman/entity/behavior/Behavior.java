package me.cjcrafter.pacman.entity.behavior;

import me.cjcrafter.neat.ui.Screen;
import me.cjcrafter.pacman.Direction;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.board.TileState;
import me.cjcrafter.pacman.entity.Entity;
import me.cjcrafter.pacman.entity.Ghost;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static me.cjcrafter.pacman.entity.Entity.TILE_SIZE;

public interface Behavior {

    default Direction getDirection(Ghost ghost) {
        return chooseClosest(ghost, getTarget(ghost));
    }

    default Direction chooseClosest(Ghost ghost, Vector2i target) {
        Direction temp = null;
        int smallest = Integer.MAX_VALUE;

        for (Direction direction : getPossibleDirections(ghost)) {
            Vector2i location = ghost.getTile().clone().add(direction.getDx(), direction.getDy());
            int distance = location.distanceSquared(target);

            if (distance <= smallest) {
                smallest = distance;
                temp = direction;
            }
        }

        return temp;
    }

    default Direction random(Ghost ghost) {
        List<Direction> directions = getPossibleDirections(ghost);
        return directions.get(ThreadLocalRandom.current().nextInt(directions.size()));
    }

    default List<Direction> getPossibleDirections(Ghost ghost) {
        Vector2i tile = ghost.getTile();
        List<Direction> temp = new ArrayList<>(3);

        for (Direction direction : Direction.values()) {
            TileState relative = ghost.getBoard().getTile(tile.getX() + direction.getDx(), tile.getY() + direction.getDy());
            if (direction == ghost.getDirection().behind())
                continue;
            else if (!ghost.canPass(relative))
                continue;

            temp.add(direction);
        }

        if (temp.isEmpty()) {
            System.out.println(" === ");
            System.out.println(" Failed to find 1 direction ");
            System.out.println(" Tile: " + ghost.getTile());
            System.out.println(" Offset: " + ghost.getOffset());
            System.out.println(" === ");
        }

        return temp;
    }

    Vector2i getTarget(Ghost ghost);

    @SuppressWarnings("all")
    default void render(Ghost ghost, Screen screen) {
        Ghost before = ghost;
        ghost = new Ghost(ghost);

        // Render a tile "outline". I could make this a sprite, but this is
        // more for testing.
        Vector2i target = getTarget(before);
        if (target == null)
            return;

        int x = target.getX() * TILE_SIZE;
        int y = target.getY() * TILE_SIZE;
        int c = ghost.getColors()[1];

        screen.setPixel(x + 0, y + 0, c);
        screen.setPixel(x + 1, y + 0, c);
        screen.setPixel(x + 0, y + 1, c);

        screen.setPixel(x + 7, y + 0, c);
        screen.setPixel(x + 8, y + 0, c);
        screen.setPixel(x + 8, y + 1, c);

        screen.setPixel(x + 8, y + 7, c);
        screen.setPixel(x + 8, y + 8, c);
        screen.setPixel(x + 7, y + 8, c);

        screen.setPixel(x + 1, y + 8, c);
        screen.setPixel(x + 0, y + 8, c);
        screen.setPixel(x + 0, y + 7, c);

        // Draw lines to each intersection to map out the path of the ghost.
        ghost.getOffset().zero().add(Entity.MIDPOINT, Entity.MIDPOINT);

        Graphics2D g = screen.getGraphics();
        g.setColor(new Color(c));

        int max = 40;
        while (!getTarget(before).equals(ghost.getTile()) && max-- > 0) {
            Direction direction = getDirection(ghost);
            Vector2i old = ghost.getTile().clone()
                    .multiply(TILE_SIZE)
                    .add((int) ghost.getOffset().getX(), (int) ghost.getOffset().getY());
            ghost.move(direction, TILE_SIZE);
            Vector2i now = ghost.getTile().clone()
                    .multiply(TILE_SIZE)
                    .add((int) ghost.getOffset().getX(), (int) ghost.getOffset().getY());

            g.drawLine(old.getX(), old.getY(), now.getX(), now.getY());
        }
    }
}

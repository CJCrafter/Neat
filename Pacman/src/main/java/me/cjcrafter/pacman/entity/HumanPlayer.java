package me.cjcrafter.pacman.entity;

import me.cjcrafter.pacman.Direction;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.board.Board;
import me.cjcrafter.pacman.ui.Keyboard;

public class HumanPlayer extends Player {

    private final Keyboard keyboard;

    public HumanPlayer(Board board, Vector2i location, Keyboard keyboard) {
        super(board, location);
        this.keyboard = keyboard;
    }

    @Override
    public void tick() {
        super.tick();

        if (freezeTicks > 0) {
            freezeTicks--;
            return;
        }

        Direction direction = keyboard.peek();
        boolean temp = false;
        if (direction != null && (temp = canMove(direction))) {
            keyboard.pop();
            if (this.direction != direction && this.direction != direction.behind())
                previousDirection = this.direction;
        } else {
            direction = getDirection();
        }

        double speed = getSpeed();
        if (temp || canMove(direction)) {
            move(direction, speed);
        }

        // Cornering. Allows pacman to turn "before" he aligns onto a grid, and
        // he starts moving in 2 directions at once
        if (previousDirection != null) {

            double delta;
            if (previousDirection.isHorizontal()) {
                delta = MIDPOINT - offset.getX();
                move(delta > 0 ? Direction.RIGHT : Direction.LEFT, Math.min(Math.abs(delta), speed));
            } else {
                delta = MIDPOINT - offset.getY();
                move(delta > 0 ? Direction.DOWN : Direction.UP, Math.min(Math.abs(delta), speed));
            }

            // Reset the direction since we just moved perpendicularly
            this.direction = direction;
            if (Math.abs(delta) < speed) {
                previousDirection = null;

            }
        }
    }
}

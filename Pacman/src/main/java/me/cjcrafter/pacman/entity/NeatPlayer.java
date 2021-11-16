package me.cjcrafter.pacman.entity;

import me.cjcrafter.neat.Client;
import me.cjcrafter.pacman.Direction;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.board.Board;

public class NeatPlayer extends Player {

    private Client client;
    private int ticksSinceLastScore = 0;

    public NeatPlayer(Board board, Vector2i location, Client client) {
        super(board, location);

        this.client = client;
    }

    @Override
    public void reset() {
        super.reset();

        ticksSinceLastScore = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (freezeTicks > 0) {
            freezeTicks--;
            return;
        }

        double[] input = new double[] {
                look(getDirection()),
                look(getDirection().left()),
                look(getDirection().right()),
                look(getDirection().behind())
        };

        double[] output = client.getCalculator().calculate(input);
        Direction direction = switch (getLargest(output)) {
            case 0 -> getDirection();
            case 1 -> getDirection().left();
            case 2 -> getDirection().right();
            case 3 -> getDirection().behind();
            default -> throw new IllegalStateException("what the hell");
        };

        double speed = getSpeed();
        if (canMove(direction)) {
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

        int newScore = getScore();
        if (newScore != client.getScore())
            ticksSinceLastScore = 0;
        else
            ticksSinceLastScore++;

        if (ticksSinceLastScore > 100)
            kill();

        client.setScore(newScore);
    }

    private double look(Direction direction) {
        return canMove(direction) ? 1.0 : 0.0;
    }

    private static int getLargest(double[] output) {
        int index = 0;
        double largest = Integer.MIN_VALUE;

        for (int i = 0; i < output.length; i++) {
            if (output[i] > largest) {
                largest = output[i];
                index = i;
            }
        }

        return index;
    }
}

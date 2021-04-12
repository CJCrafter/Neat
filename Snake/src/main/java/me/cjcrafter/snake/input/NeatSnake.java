package me.cjcrafter.snake.input;

import me.cjcrafter.neat.Client;
import me.cjcrafter.snake.board.Board;
import me.cjcrafter.snake.board.Vector2d;

public class NeatSnake extends Snake {

    private final Client client;
    private int ticksSinceLastApple;

    public NeatSnake(Board board, Client client) {
        super(board);

        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void reset() {
        super.reset();
        ticksSinceLastApple = 0;

        if (client != null)
            client.setScore(0);
    }

    @Override
    public void move(Direction dir) {
        if (dir == null)
            return;

        Vector2d vector = getHead().clone().add(dir.x, dir.y);
        boolean ateApple = vector.equals(board.getApple());
        double previousDistance = getHead().clone().subtract(board.getApple()).lengthSquared();

        super.move(dir);
        if (!isAlive())
            return;

        ticksSinceLastApple++;

        if (ateApple) {
            ticksSinceLastApple = 0;
            client.setScore(client.getScore() + 1);
        } else {
            double newDistance = getHead().clone().subtract(board.getApple()).lengthSquared();
            client.setScore(client.getScore() + (newDistance > previousDistance ? -0.002 : 0.0025));
        }

        if (ticksSinceLastApple > 150)
            die();
    }

    @Override
    public Direction tick() {
        if (!isAlive())
            return null;

        double forward = look(getLastDirection());
        double left = look(getLastDirection().left());
        double right = look(getLastDirection().right());

        boolean appleForward;
        boolean appleLeft;
        boolean appleRight;

        int dx = getHead().dx(board.getApple());
        int dy = getHead().dy(board.getApple());
        switch (getLastDirection()) {
            case UP:
                appleForward = dy < 0 && dx == 0;
                appleLeft    = dx < 0 && dy == 0;
                appleRight   = dx > 0 && dy == 0;
                break;
            case DOWN:
                appleForward = dy < 0 && dx == 0;
                appleLeft    = dx > 0 && dy == 0;
                appleRight   = dx < 0 && dy == 0;
                break;
            case LEFT:
                appleForward = dx < 0 && dy == 0;
                appleLeft    = dy > 0 && dx == 0;
                appleRight   = dy < 0 && dx == 0;
                break;
            case RIGHT:
                appleForward = dx > 0 && dy == 0;
                appleLeft    = dy < 0 && dx == 0;
                appleRight   = dy > 0 && dx == 0;
                break;
            default:
                throw new RuntimeException();
        }

        double[] output = client.getCalculator().calculate(forward, left, right, appleForward ? 1 : 0, appleLeft ? 1 : 0, appleRight ? 1 : 0);
        switch (getLargest(output)) {
            case 0:
                return getLastDirection();
            case 1:
                return getLastDirection().left();
            case 2:
                return getLastDirection().right();
            default:
                throw new RuntimeException();
        }
    }

    private int look(Direction dir) {
        Vector2d head = getHead().clone().add(dir.x, dir.y);
        return body.contains(head.add(dir.x, dir.y)) || !board.inBounds(head) ? 1 : 0;
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

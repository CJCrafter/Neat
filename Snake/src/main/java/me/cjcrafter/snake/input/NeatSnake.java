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
            client.setScore(client.getScore() + (newDistance > previousDistance ? -0.02 : 0.02));
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

        switch (getLastDirection()) {
            case UP:
                appleForward = board.getApple().getY() - getHead().getY() < 0 && board.getApple().getX() == getHead().getX();
                appleLeft = board.getApple().getX() - getHead().getX() < 0;
                appleRight = board.getApple().getX() - getHead().getX() > 0;
                break;
            case DOWN:
                appleForward = board.getApple().getY() - getHead().getY() > 0 && board.getApple().getX() == getHead().getX();
                appleLeft = board.getApple().getX() - getHead().getX() > 0;
                appleRight = board.getApple().getX() - getHead().getX() < 0;
                break;
            case LEFT:
                appleForward = board.getApple().getX() - getHead().getX() < 0 && board.getApple().getY() == getHead().getY();
                appleLeft = board.getApple().getY() - getHead().getY() > 0;
                appleRight = board.getApple().getY() - getHead().getY() < 0;
                break;
            case RIGHT:
                appleForward = board.getApple().getX() - getHead().getX() > 0 && board.getApple().getY() == getHead().getY();
                appleLeft = board.getApple().getY() - getHead().getY() < 0;
                appleRight = board.getApple().getY() - getHead().getY() > 0;
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
        Vector2d head = getHead();
        int temp = !body.contains(head.add(dir.x, dir.y)) && board.inBounds(head) ? 1 : 0;
        head.add(-dir.x, -dir.y);
        return temp;
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
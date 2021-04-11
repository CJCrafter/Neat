package me.cjcrafter.snake.input;

import me.cjcrafter.snake.board.Vector2d;

public enum Direction {

    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

    final int x, y;

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d getVector() {
        return new Vector2d(x, y);
    }

    public Direction left() {
        switch (this) {
            case UP:
                return LEFT;
            case DOWN:
                return RIGHT;
            case LEFT:
                return DOWN;
            case RIGHT:
                return UP;
            default:
                throw new RuntimeException();
        }
    }

    public Direction right() {
        switch (this) {
            case UP:
                return RIGHT;
            case DOWN:
                return LEFT;
            case LEFT:
                return UP;
            case RIGHT:
                return DOWN;
            default:
                throw new RuntimeException();
        }
    }

    public boolean isOpposite(Direction other) {
        switch (this) {
            case UP:
                return other == DOWN;
            case DOWN:
                return other == UP;
            case LEFT:
                return other == RIGHT;
            case RIGHT:
                return other == LEFT;
            default:
                throw new RuntimeException();
        }
    }
}

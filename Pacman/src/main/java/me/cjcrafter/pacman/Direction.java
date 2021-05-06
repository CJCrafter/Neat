package me.cjcrafter.pacman;

public enum Direction {

    UP(0, -1, 0),
    DOWN(0, 1, 1),
    LEFT(-1, 0, 2),
    RIGHT(1, 0, 3);

    private final int dx;
    private final int dy;
    private final int animationFrame;

    Direction(int dx, int dy, int animationFrame) {
        this.dx = dx;
        this.dy = dy;
        this.animationFrame = animationFrame;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public int getAnimationFrame() {
        return animationFrame;
    }

    public boolean isHorizontal() {
        return dy == 0;
    }

    public boolean isVertical() {
        return dx == 0;
    }

    public Direction left() {
        return switch (this) {
            case UP -> LEFT;
            case DOWN -> RIGHT;
            case LEFT -> DOWN;
            case RIGHT -> UP;
        };
    }

    public Direction right() {
        return switch (this) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
        };
    }

    public Direction behind() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case RIGHT -> LEFT;
            case LEFT -> RIGHT;
        };
    }

    public Vector2i getVector() {
        return new Vector2i(dx, dy);
    }
}

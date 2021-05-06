package me.cjcrafter.pacman.entity;

import me.cjcrafter.neat.ui.Screen;
import me.cjcrafter.pacman.Direction;
import me.cjcrafter.pacman.Vector2d;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.board.Board;
import me.cjcrafter.pacman.board.TileState;

public abstract class Entity {

    public static final int TILE_SIZE = 8;
    public static final int MIDPOINT = TILE_SIZE / 2;
    public static final double PIXELS_PER_TICK = 1.0 + 1.0 / 3.0;

    protected final Board board;
    protected Vector2i tile;
    protected Vector2d offset;
    protected Direction direction;
    protected double epsilon = 0.00001;

    private int ticksAlive;

    public Entity(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public Vector2i getTile() {
        return tile;
    }

    public Vector2d getOffset() {
        return offset;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getTicksAlive() {
        return ticksAlive;
    }

    public void wrap() {
        tile.setX((tile.getX() + board.getWidth()) % board.getWidth());
        tile.setY((tile.getY() + board.getHeight()) % board.getHeight());
    }

    public boolean isAlignX() {
        return Math.abs(MIDPOINT - offset.getX()) < epsilon;
    }

    public boolean isAlignY() {
        return Math.abs(MIDPOINT - offset.getY()) < epsilon;
    }

    public void move(Direction dir, double speed) {

        double dx = offset.getX() + dir.getDx() * speed;
        double dy = offset.getY() + dir.getDy() * speed;

        while (dx >= TILE_SIZE) {
            tile.add(1, 0);
            dx -= TILE_SIZE;
        }
        while (dx < 0.0) {
            tile.add(-1, 0);
            dx += TILE_SIZE;
        }
        while (dy >= TILE_SIZE) {
            tile.add(0, 1);
            dy -= TILE_SIZE;
        }
        while (dy < 0.0) {
            tile.add(0, -1);
            dy += TILE_SIZE;
        }

        offset = new Vector2d(dx, dy);
        wrap();
        this.direction = dir;
    }

    public void reset() {
        ticksAlive = 0;
    }

    public abstract boolean canPass(TileState state);

    public void tick() {
        ticksAlive++;
    }

    public abstract void render(Screen screen);
}

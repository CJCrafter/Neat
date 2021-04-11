package me.cjcrafter.snake.input;

import me.cjcrafter.snake.board.Board;
import me.cjcrafter.snake.board.Vector2d;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class Snake implements Iterable<Vector2d> {

    public static final int INITIAL_SIZE = 4;

    protected Board board;
    protected LinkedList<Vector2d> body;

    private Direction lastDirection;
    private boolean alive;

    public Snake(Board board) {
        this.board = board;
        reset();
    }

    public void reset() {
        this.body = new LinkedList<>();
        this.lastDirection = Direction.RIGHT;
        this.alive = true;

        int startY = board.getSize() / 2;
        int startX = board.getSize() / 4;
        for (int i = 0; i < INITIAL_SIZE; i++) {
            body.add(new Vector2d(startX - i, startY));
        }
    }

    public Vector2d getHead() {
        return body.getFirst();
    }

    public Vector2d getTail() {
        return body.getLast();
    }

    public boolean contains(Vector2d point) {
        return body.contains(point);
    }

    public int size() {
        return body.size();
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(Direction lastDirection) {
        this.lastDirection = lastDirection;
    }

    public void die() {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void move(Direction dir) {
        if (dir == null)
            return;

        Vector2d vector = getHead().clone().add(dir.x, dir.y);
        boolean ateApple = vector.equals(board.getApple());

        if (!board.inBounds(vector) || body.contains(vector)) {
            die();
            return;
        }

        body.addFirst(vector);
        if (!ateApple)
            body.removeLast();
        else
            board.newApple();

        setLastDirection(dir);
    }

    public abstract Direction tick();

    @Override
    public Iterator<Vector2d> iterator() {
        return body.iterator();
    }
}

package me.cjcrafter.snake.board;

import me.cjcrafter.snake.input.Snake;

import java.util.concurrent.ThreadLocalRandom;

public class Board {

    private Snake snake;
    private Vector2d apple;
    private int size;

    public Board(int size) {
        this.size = size;
    }

    public void reset() {
        this.snake.reset();
        newApple();
    }

    public Snake getSnake() {
        return snake;
    }

    public void setSnake(Snake snake) {
        this.snake = snake;
        newApple();
    }

    public Vector2d getApple() {
        return apple;
    }

    public int getSize() {
        return size;
    }

    public void newApple() {
        Vector2d vector;
        do {
            int x = ThreadLocalRandom.current().nextInt(size);
            int y = ThreadLocalRandom.current().nextInt(size);
            vector = new Vector2d(x, y);
        } while (snake.contains(vector));

        apple = vector;
    }

    public void tick() {
        snake.move(snake.tick());
    }

    public boolean inBounds(Vector2d vector) {
        return vector.getX() >= 0 && vector.getX() < size && vector.getY() >= 0 && vector.getY() < size;
    }
}
package me.cjcrafter.snake.ui;

import me.cjcrafter.neat.Client;
import me.cjcrafter.neat.ui.ClientScreen;
import me.cjcrafter.snake.board.Board;
import me.cjcrafter.snake.board.Vector2d;
import me.cjcrafter.snake.input.Snake;

import java.awt.*;

public class SnakeScreen extends ClientScreen {

    private final Board board;
    private final Snake snake;

    private final int cellWidth;
    private final int cellHeight;

    private boolean wasAlive = true;

    public SnakeScreen(int width, int height, Client client, Board board) {
        super(width, height, client);
        this.board = board;
        this.snake = board.getSnake();

        if (width % board.getSize() != 0 || height % board.getSize() != 0)
            throw new IllegalArgumentException("You fool, your ratios are bad! " + new Dimension(width, height) + " % " + board.getSize());

        this.cellWidth = width / board.getSize();
        this.cellHeight = height / board.getSize();
    }

    @Override
    public void renderGenome() {

        // Make sure to render the client next chance we get
        wasAlive = true;
        super.renderGenome();
    }

    @Override
    public void renderClient() {
        if (!wasAlive) {
            if (snake.isAlive())
                wasAlive = true;
            else
                return;
        }

        madeChanges = true;
        fill(snake.isAlive() ? BACKGROUND : 0xff0000);

        Vector2d vector = board.getApple();
        Rectangle rectangle = new Rectangle(vector.getX() * cellWidth, vector.getY() * cellHeight, cellWidth, cellHeight);
        fill(rectangle, Color.RED);

        for (Vector2d vector2d : snake) {
            rectangle = new Rectangle(vector2d.getX() * cellWidth, vector2d.getY() * cellHeight, cellWidth, cellHeight);
            fill(rectangle, client.getSpecies().getBase().speciesColor);
            outline(rectangle, BACKGROUND);
        }

        if (!snake.isAlive()) {
            wasAlive = false;
            drawCenteredString(getGraphics(), String.valueOf((int) client.getScore()), new Rectangle(getWidth(), getHeight()), new Font("Engravers MT", Font.BOLD, 24), Color.WHITE);
        }
    }
}

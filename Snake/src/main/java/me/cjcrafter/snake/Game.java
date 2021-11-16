package me.cjcrafter.snake;

import me.cjcrafter.neat.Client;
import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.ui.NeatFrame;
import me.cjcrafter.snake.board.Board;
import me.cjcrafter.snake.input.NeatSnake;
import me.cjcrafter.snake.ui.GamePanel;
import me.cjcrafter.snake.ui.SnakeFrame;
import me.cjcrafter.snake.ui.SnakeScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

    private static final int CLIENTS = 18 * 12;

    private final NeatFrame frame;
    private final Neat neat;
    private final List<Board> boards;

    private int tickRate = 10;
    private double timeBetweenTicks = 1000000000.0 / tickRate;

    public Game() {
        neat = new Neat(6, 3, CLIENTS);
        frame = new SnakeFrame("Snake", neat, this, new Dimension(1080, 720), new Dimension(18, 12));
        boards = new ArrayList<>();

        // Evenly distribute possible species colors, and shuffle them to help
        // avoid having very similar colors being present at the same time.
        List<Color> colors = new ArrayList<>(CLIENTS);
        for (int i = 0; i < CLIENTS; i++) {
            colors.add(Color.getHSBColor((float) i / CLIENTS, 1f, 1f));
        }
        Collections.shuffle(colors);

        AtomicInteger i = new AtomicInteger();
        frame.fillClients(rectangle -> {
            Board board = new Board(20);
            Color color = colors.get(i.get());
            Client client = neat.getClients().get(i.getAndIncrement());
            client.speciesColor = color;
            board.setSnake(new NeatSnake(board, client));
            boards.add(board);
            return new SnakeScreen(rectangle.width, rectangle.height, client, board);
        });
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
        this.timeBetweenTicks = 1000000000.0 / tickRate;
    }

    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        boolean tickedOnce = false;
        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / this.timeBetweenTicks;
            lastTime = now;
            while (delta >= 1) {
                tick();
                tickedOnce = true;
                delta--;
            }

            if (tickedOnce) {
                render();
                frames++;
            }

            if(System.currentTimeMillis() - timer > 10000) {
                timer += 10000;
                GamePanel.speciesColorMap.clear();
                neat.printSpecies();
                System.out.println();
                System.out.println(neat.debugGenome());
                frames = 0;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        boolean allDead = true;
        for (Board board : boards) {
            board.tick();
            allDead &= !board.getSnake().isAlive();
        }

        // Handle evolution and resetting. This call will only happen when
        // every snake has died. Each time this is called, there is a new
        // generation.
        if (allDead) {
            neat.evolve();
            for (Board board : boards) {
                board.reset();
            }
        }
    }

    private void render() {
        frame.render();
    }
}

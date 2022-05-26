package me.cjcrafter.pacman;

import me.cjcrafter.neat.Client;
import me.cjcrafter.neat.Neat;
import me.cjcrafter.pacman.board.Board;
import me.cjcrafter.pacman.board.SpriteBoard;
import me.cjcrafter.pacman.entity.Ghost;
import me.cjcrafter.pacman.entity.NeatPlayer;
import me.cjcrafter.pacman.entity.Player;
import me.cjcrafter.pacman.entity.behavior.ChaseAggresive;
import me.cjcrafter.pacman.entity.behavior.ChaseAmbush;
import me.cjcrafter.pacman.entity.behavior.ChaseFeign;
import me.cjcrafter.pacman.entity.behavior.ChasePatrol;
import me.cjcrafter.pacman.file.TiledSpriteSheet;
import me.cjcrafter.pacman.ui.PacmanFrame;
import me.cjcrafter.pacman.ui.PacmanScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Pacman {

    public static float TICKS_PER_SECOND = 60.0f;
    public static final int CLIENTS = 100;

    private Timer tickTimer;
    private boolean running;
    private boolean pause;

    private final Neat neat;
    private final List<Board> boards;
    private final PacmanFrame frame;

    public Pacman() {
        tickTimer = new Timer(TICKS_PER_SECOND);
        TiledSpriteSheet sprite = new TiledSpriteSheet("ghost-sprite.png", (byte) 4, 20, 20);

        boards = new LinkedList<>();
        neat = new Neat(8, 4, CLIENTS);
        frame = new PacmanFrame(neat, this);

        for (int i = 0; i < CLIENTS; i++) {
            Board board = new SpriteBoard("default-board.png");

            // Blinky is a red ghost that chases directly after pacman by targeting
            // pacman's location. Blinky scatters to the top right.
            Ghost blinky = new Ghost(board, sprite, new Vector2i(14, 14), Ghost.RED);
            blinky.chase = ChaseAggresive.INSTANCE;
            blinky.setScatterLocation(new Vector2i(26, 4));
            //board.addGhost(blinky);

            // Pinky is a pink ghost that attempts to cut pacman off by targeting
            // 4 blocks in front of pacman. Pinky scatters to the top left.
            Ghost pinky = new Ghost(board, sprite, new Vector2i(14, 17), Ghost.PINK);
            pinky.setSpawnLocation(new Vector2i(112, 112));
            pinky.chase = ChaseAmbush.INSTANCE;
            pinky.setScatterLocation(new Vector2i(1, 4));
            //board.addGhost(pinky);

            // Inky is a cyan ghost that patrols an area in front of pacman by
            // targeting 2x the "eyesight" of blinky. Inky scatters to the bottom
            // right.
            Ghost inky = new Ghost(board, sprite, new Vector2i(12, 17), Ghost.CYAN);
            pinky.setSpawnLocation(new Vector2i(112, 112));
            inky.chase = ChasePatrol.INSTANCE;
            inky.setScatterLocation(new Vector2i(26, 32));
            //board.addGhost(inky);

            // Clyde is an orange ghost that gets close to pacman, but then backs
            // off. Clyde scatters to the bottom left.
            Ghost clyde = new Ghost(board, sprite, new Vector2i(16, 17), Ghost.ORANGE);
            pinky.setSpawnLocation(new Vector2i(112, 112));
            clyde.chase = ChaseFeign.INSTANCE;
            clyde.setScatterLocation(new Vector2i(1, 32));
            //board.addGhost(clyde);

            //Keyboard keys = new Keyboard();
            Player player = new NeatPlayer(board, new Vector2i(14, 26), neat.getClients().get(i));
            board.setPlayer(player);
            boards.add(board);
        }

        // Evenly distribute possible species colors, and shuffle them to help
        // avoid having very similar colors being present at the same time.
        List<Color> colors = new ArrayList<>(CLIENTS);
        for (int i = 0; i < CLIENTS; i++) {
            colors.add(Color.getHSBColor((float) i / CLIENTS, 1f, 1f));
        }
        Collections.shuffle(colors);

        AtomicInteger i = new AtomicInteger();
        Iterator<Board> iterator = boards.iterator();
        frame.fillClients(rectangle -> {
            Color color = colors.get(i.get());
            Client client = neat.getClients().get(i.getAndIncrement());
            client.speciesColor = color;

            return new PacmanScreen(rectangle.width, rectangle.height, client, iterator.next());
        });

        //frame.addKeyListener(keys);
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public void start() {
        running = true;
        run();
    }

    private void run() {
        int updates = 0;
        Timer second = new Timer(1);

        while (running) {
            if (tickTimer.tick() && !pause) {
                tick();
                render();
                updates++;
            }

            if (second.tick()) {
                System.out.println("Updates: " + updates);
                updates = 0;
            }
        }
    }

    private void tick() {
        boolean oneAlive = false;

        for (Board board : boards) {
            board.tick();
            oneAlive |= board.getPlayer().isAlive();
        }

        if (!oneAlive) {
            neat.evolve();
            reset();
        }
    }

    public void reset() {
        boards.forEach(Board::reset);
    }

    private void render() {
        frame.render();
    }

    public void stop() {
        running = false;
    }

    public void setTickRate(float tickRate) {
        System.out.println("New tickrate: " + tickRate);
        tickTimer = new Timer(tickRate);
        TICKS_PER_SECOND = tickRate;
    }

    public List<Board> getBoards() {
        return boards;
    }
}

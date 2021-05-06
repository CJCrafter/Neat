package me.cjcrafter.pacman;

import me.cjcrafter.pacman.board.Board;
import me.cjcrafter.pacman.board.SpriteBoard;
import me.cjcrafter.pacman.entity.Entity;
import me.cjcrafter.pacman.entity.Ghost;
import me.cjcrafter.pacman.entity.HumanPlayer;
import me.cjcrafter.pacman.entity.Player;
import me.cjcrafter.pacman.entity.behavior.ChaseAggresive;
import me.cjcrafter.pacman.entity.behavior.ChaseAmbush;
import me.cjcrafter.pacman.entity.behavior.ChaseFeign;
import me.cjcrafter.pacman.entity.behavior.ChasePatrol;
import me.cjcrafter.pacman.file.TiledSpriteSheet;
import me.cjcrafter.pacman.ui.Frame;
import me.cjcrafter.pacman.ui.Keyboard;

import java.awt.*;

public class Pacman {

    public static final float TICKS_PER_SECOND = 30.0f;

    private final Timer tickTimer;
    private boolean running;

    private final Board board;
    private final Frame frame;

    public Pacman() {
        tickTimer = new Timer(TICKS_PER_SECOND);

        board = new SpriteBoard("default-board.png");
        TiledSpriteSheet sprite = new TiledSpriteSheet("ghost-sprite.png", (byte) 4, 20, 20);

        // Blinky is a red ghost that chases directly after pacman by targeting
        // pacman's location. Blinky scatters to the top right.
        Ghost blinky = new Ghost(board, sprite, new Vector2i(14, 14), Ghost.RED);
        blinky.chase = ChaseAggresive.INSTANCE;
        blinky.setScatterLocation(new Vector2i(26,  4));
        board.addGhost(blinky);

        // Pinky is a pink ghost that attempts to cut pacman off by targeting
        // 4 blocks in front of pacman. Pinky scatters to the top left.
        Ghost pinky = new Ghost(board, sprite, new Vector2i(14, 17), Ghost.PINK);
        pinky.chase = ChaseAmbush.INSTANCE;
        pinky.setScatterLocation(new Vector2i(1, 4));
        board.addGhost(pinky);

        // Inky is a cyan ghost that patrols an area in front of pacman by
        // targeting 2x the "eyesight" of blinky. Inky scatters to the bottom
        // right.
        Ghost inky = new Ghost(board, sprite, new Vector2i(12, 17), Ghost.CYAN);
        inky.chase = ChasePatrol.INSTANCE;
        inky.setScatterLocation(new Vector2i(26, 32));
        board.addGhost(inky);

        // Clyde is an orange ghost that gets close to pacman, but then backs
        // off. Clyde scatters to the bottom left.
        Ghost clyde = new Ghost(board, sprite, new Vector2i(16, 17), Ghost.ORANGE);
        clyde.chase = ChaseFeign.INSTANCE;
        clyde.setScatterLocation(new Vector2i(1, 32));
        board.addGhost(clyde);

        Keyboard keys = new Keyboard();
        Player player = new HumanPlayer(board, new Vector2i(14, 26), keys);
        board.setPlayer(player);

        this.frame = new Frame(new Dimension(Board.DEFAULT_WIDTH * Entity.TILE_SIZE, Board.DEFAULT_HEIGHT * Entity.TILE_SIZE), board);

        frame.addKeyListener(keys);
    }

    public void start() {
        running = true;
        run();
    }

    private void run() {
        int updates = 0;
        Timer second = new Timer(1);

        while (running) {
            if (tickTimer.tick()) {
                tick();
                render();
                updates++;
            }

            if (second.tick()) {
                //System.out.println("Updates: " + updates);
                updates = 0;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        board.tick();
    }

    private void render() {
        frame.getPanel().paintImmediately(frame.getPanel().getBounds());
    }

    public void stop() {
        running = false;
    }
}

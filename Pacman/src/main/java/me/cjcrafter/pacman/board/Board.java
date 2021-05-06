package me.cjcrafter.pacman.board;

import me.cjcrafter.neat.ui.Screen;
import me.cjcrafter.pacman.Pacman;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.entity.Ghost;
import me.cjcrafter.pacman.entity.GhostState;
import me.cjcrafter.pacman.entity.Player;
import me.cjcrafter.pacman.file.TextSheet;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Board {

    public static final int DEFAULT_WIDTH = 28;
    public static final int DEFAULT_HEIGHT = 36;

    public static final int[][] LEVEL_STATES = new int[][]{
            new int[]{ 420, 1200, 420, 1200, 300, 1200, 300, -1 },
            new int[]{ 420, 1200, 120, 1200, 300, 61980, 1, -1 },
            new int[]{ 300, 1200, 300, 1200, 300, 62220, 1, -1 }
    };
    public static final int[] LEVEL_FRIGHT_TIMES = new int[] { 360, 300, 240, 180, 120, 300, 120, 120, 60, 300, 120, 60, 60, 180, 60, 60, 0, 60, 0};

    public static final TextSheet TEXT = new TextSheet("text.png", 8);

    protected TileState[][] tiles;
    protected List<Ghost> ghosts;
    protected Player player;

    private final BufferedImage img;
    protected int width;
    protected int height;

    private int startTicks = (int) Pacman.TICKS_PER_SECOND * 5;
    private int level = 1;
    private boolean chase = true;
    private int pellets;
    private int timer = 1;
    private int index;

    private int frightTimer;


    public Board() {
        ghosts = new ArrayList<>(4);

        try {
            img = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("default-board-render.png")));
        } catch (IOException e) {
            throw new InternalError();
        }
    }

    protected abstract void initBoard();

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Ghost> getGhosts() {
        return ghosts;
    }

    public void addGhost(Ghost ghost) {
        ghost.insertionIndex = ghosts.size();
        ghosts.add(ghost);
        ghost.reset();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isChase() {
        return chase;
    }

    public TileState getTile(Vector2i vector) {
        return getTile(vector.getX(), vector.getY());
    }

    public TileState getTile(int x, int y) {
        return tiles[(y + height) % height][(x + width) % width];
    }

    public void setTile(Vector2i vector, TileState tile) {
        setTile(vector.getX(), vector.getY(), tile);
    }

    public void setTile(int x, int y, TileState tile) {
        tiles[(y + height) % height][(x + width) % width] = tile;
    }

    public int getPellets() {
        return pellets;
    }

    public void setPellets(int pellets) {
        this.pellets = pellets;
    }

    public int getLevel() {
        return level;
    }

    public int getFrightTimer() {
        return frightTimer;
    }

    public void eatPellet() {

        // Insertion order is important here. Blinky (red) -> Pinky (pink) ->
        // Inky (cyan) -> Clyde (orange)
        int index = 0;
        int[] bounds = switch (level) {
            case 1  -> new int[]{ 0, 0, 30, 60 };
            case 2  -> new int[]{ 0, 0, 0, 50 };
            default -> new int[]{ 0, 0, 0, 0 };
        };

        for (Ghost ghost : ghosts) {
            int bound = bounds[index++];
            if (ghost.isReleased())
                continue;

            if (ghost.getDotCounter() == bound) {
                ghost.release();
                System.out.println("Releasing ghost");
            } else {
                ghost.setDotCounter(ghost.getDotCounter() + 1);
            }
        }
    }

    public void eatPowerPellet() {
        if (level >= LEVEL_FRIGHT_TIMES.length) {
            frightTimer = LEVEL_FRIGHT_TIMES[LEVEL_FRIGHT_TIMES.length - 1];
        } else {
            frightTimer = LEVEL_FRIGHT_TIMES[level];
        }

        for (Ghost ghost : ghosts) {
            ghost.setState(GhostState.FRIGHTENED);
        }
    }

    public void tick() {
        if (startTicks-- > 0)
            return;

        if (frightTimer > 0)
            frightTimer--;

        if (--timer == 0) {
            chase = !chase;

            int levelIndex;
            if (level >= 5)
                levelIndex = 2;
            else if (level >= 2)
                levelIndex = 1;
            else
                levelIndex = 0;

            timer = LEVEL_STATES[levelIndex][index++];
        }

        player.tick();
        for (Ghost ghost : ghosts)
            ghost.tick();
    }

    public void render(Screen screen) {

        Graphics2D g = screen.getGraphics();
        g.setColor(new Color(0xffb897));
        g.drawImage(img, 0, 0, screen.getWidth(), screen.getHeight(), null);

        TEXT.render(screen, "1UP", new Vector2i(3, 0).multiply(8), 0xFFFFFF);
        TEXT.renderLeft(screen, String.valueOf(player.getScore()), new Vector2i(5, 1).multiply(8), 0xFFFFFF);
        TEXT.render(screen, "HIGH SCORE", new Vector2i(9, 0).multiply(8), 0xFFFFFF);

        int tileWidth = screen.getWidth() / width;
        int tileHeight = screen.getHeight() / height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Rectangle rectangle = new Rectangle(x * tileWidth, y * tileHeight, tileWidth, tileHeight);

                if (tiles[y][x] == TileState.PELLET) {
                    screen.fill(new Rectangle(rectangle.x + tileWidth / 2 - 1, rectangle.y + tileHeight / 2 - 1, 2, 2), 0xffb897);
                } else if (tiles[y][x] == TileState.POWER_PELLET && player.getTicksAlive() % 20 < 10) {
                    screen.fill(rectangle, 0xffb897);
                }
            }
        }

        g.dispose();

        for (Ghost ghost : ghosts) {
            ghost.render(screen);
        }

        player.render(screen);
    }
}

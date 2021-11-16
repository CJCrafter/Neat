package me.cjcrafter.pacman.entity;

import me.cjcrafter.neat.ui.Screen;
import me.cjcrafter.pacman.Direction;
import me.cjcrafter.pacman.Vector2d;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.board.Board;
import me.cjcrafter.pacman.board.TileState;
import me.cjcrafter.pacman.file.TiledSpriteSheet;

public abstract class Player extends Entity {

    private final TiledSpriteSheet sprite = new TiledSpriteSheet("pacman-sprite.png", (byte) 2, 20, 20);
    private final int[] colorsAlive = new int[] { -1, 0xFFFF00 };
    private final int[] colorsDead = new int[] { -1, 0xFF0000 };
    private int animationFrame;

    private static final double[][] LEVEL_SPEEDS = new double[][]{
            new double[]{ 0.80, 0.90 },
            new double[]{ 0.90, 0.95 },
            new double[]{ 1.00, 1.00 },
            new double[]{ 0.90, 0.90 }
    };

    private boolean isAlive = true;
    protected int score;
    protected int freezeTicks;
    protected Direction previousDirection;

    public Player(Board board, Vector2i location) {
        super(board);

        this.tile = location;
        this.offset = new Vector2d(0, MIDPOINT);
        this.direction = Direction.LEFT;
    }

    public int getScore() {
        return score;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void kill() {
        isAlive = false;
    }

    public double getSpeed() {
        int speedIndex;
        if (board.getLevel() >= 21) {
            speedIndex = 3;
        } else if (board.getLevel() >= 5) {
            speedIndex = 2;
        } else if (board.getLevel() >= 2) {
            speedIndex = 1;
        } else {
            speedIndex = 0;
        }

        return LEVEL_SPEEDS[speedIndex][board.getFrightTimer() == 0 ? 0 : 1] * PIXELS_PER_TICK;
    }

    public boolean canMove(Direction direction) {
        return canPass(board.getTile(tile.getX() + direction.getDx(), tile.getY() + direction.getDy()));
    }

    @Override
    public boolean canPass(TileState state) {
        return switch (state) {
            case PELLET, POWER_PELLET, TUNNEL, SPACE -> true;
            case WALL, MEMBRANE -> false;
        };
    }

    @Override
    public void tick() {
        super.tick();

        // For each animation frame value, the following x should be output:
        //   0: 0
        //   1: 1
        //   2: 2
        //   3: 1
        //   *Repeat*
        if (getTicksAlive() % 2 == 0)
            animationFrame++;

        // Attempt to collide with pellets
        if (board.getTile(tile) == TileState.PELLET) {
            score += 10;
            freezeTicks += 1;
            board.setTile(tile, TileState.SPACE);

            board.eatPellet();
        } else if (board.getTile(tile) == TileState.POWER_PELLET) {
            score += 50;
            freezeTicks += 3;
            board.setTile(tile, TileState.SPACE);

            board.eatPowerPellet();
        }
    }

    @Override
    public void reset() {
        super.reset();

        isAlive = true;
        score = 0;
        freezeTicks = 0;
        previousDirection = null;
        this.tile = new Vector2i(14, 26);
        this.offset = new Vector2d(0, MIDPOINT);
    }

    @Override
    public void render(Screen screen) {

        int xSprite = switch (animationFrame % 4) {
            case 0 -> 2;
            case 1, 3 -> 0;
            case 2 -> 1;
            default -> throw new IllegalStateException("Unexpected value: " + animationFrame % 4);
        };
        int ySprite = direction.getAnimationFrame();

        // The starting (x, y) coordinates of the sprite tile
        int xOffset = xSprite * sprite.getTileWidth();
        int yOffset = ySprite * sprite.getTileHeight();
        byte[] pixels = sprite.getPixels();

        int tileWidth = screen.getWidth() / board.getWidth();
        int tileHeight = screen.getHeight() / board.getHeight();

        int scale = 1;
        int xScreen;
        int yScreen;

        {
            double boardPositionX = tile.getX() * tileWidth + offset.getX();
            double boardPositionY = tile.getY() * tileHeight + offset.getY();
            double middleOffsetX = sprite.getTileWidth() * scale / 2.0;
            double middleOffsetY = sprite.getTileHeight() * scale / 2.0;

            xScreen = (int) (boardPositionX - middleOffsetX);
            yScreen = (int) (boardPositionY - middleOffsetY);
        }

        for (int y = 0; y < sprite.getTileHeight(); y++) {
            int yPos = yScreen + scale * y;
            if (yPos < 0 || yPos >= screen.getHeight())
                yPos %= screen.getHeight();

            for (int x = 0; x < sprite.getTileWidth(); x++) {
                int xPos = xScreen + scale * x;
                if (xPos < 0 || xPos >= screen.getWidth())
                    xPos %= screen.getWidth();

                int colorIndex = pixels[(y + yOffset) * sprite.getWidth() + (x + xOffset)];
                int color = isAlive ? colorsAlive[colorIndex] : colorsDead[colorIndex];

                if (color == -1)
                    continue;

                for (int scaleY = 0; scaleY < scale; scaleY++) {
                    for (int scaleX = 0; scaleX < scale; scaleX++) {
                        screen.setPixel(xPos + scaleX, yPos + scaleY, color);
                    }
                }
            }
        }
    }
}

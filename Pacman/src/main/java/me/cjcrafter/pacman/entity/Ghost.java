package me.cjcrafter.pacman.entity;

import me.cjcrafter.neat.ui.Screen;
import me.cjcrafter.pacman.Direction;
import me.cjcrafter.pacman.Vector2d;
import me.cjcrafter.pacman.Vector2i;
import me.cjcrafter.pacman.board.Board;
import me.cjcrafter.pacman.board.TileState;
import me.cjcrafter.pacman.entity.behavior.Behavior;
import me.cjcrafter.pacman.entity.behavior.EatenTarget;
import me.cjcrafter.pacman.entity.behavior.FrightenedRandom;
import me.cjcrafter.pacman.entity.behavior.ScatterTarget;
import me.cjcrafter.pacman.file.TiledSpriteSheet;

public class Ghost extends Entity {

    public static final int[] RED    = new int[]{ -1, 0xFF0000, 0x0031FF, 0xFFFFFF };
    public static final int[] PINK   = new int[]{ -1, 0xFF9CCE, 0x0031FF, 0xFFFFFF };
    public static final int[] CYAN   = new int[]{ -1, 0x31FFFF, 0x0031FF, 0xFFFFFF };
    public static final int[] ORANGE = new int[]{ -1, 0xFFCE31, 0x0031FF, 0xFFFFFF };
    public static final int[] FRIGHT = new int[]{ -1, 0x0031FF, 0xFFCE31 };
    public static final int[] FLASH  = new int[]{ -1, 0xFFFFFF, 0xFF0000 };
    public static final int[] EATEN  = new int[]{ -1, 0x0000FF, 0xFFFFFF };

    private static final double[][] LEVEL_SPEEDS = new double[][]{
            new double[]{ 0.75, 0.50, 0.40 },
            new double[]{ 0.85, 0.55, 0.45 },
            new double[]{ 0.95, 0.60, 0.50 }
    };

    // Rendering/Animation stuff
    protected TiledSpriteSheet sprite;
    protected int animationFrame;
    private int[] colors;

    // General variables/information
    private GhostState state;
    private GhostState previousState;
    private Vector2i spawnLocation;
    private Vector2i scatterLocation;

    // Personal dot counter to determine when to leave the ghost pen. This should
    // be reset to 0 in between level/lives.
    private int dotCounter;
    private boolean released;
    private boolean wasReleased;
    public int insertionIndex;

    // Helps the ghost find the center of a tile and turn
    private Direction nextDirection;

    // Behavior variables
    public Behavior chase;
    public Behavior scatter;
    public Behavior frightened;
    public Behavior eaten;

    public Ghost(Board board, TiledSpriteSheet sprite, Vector2i spawnLocation, int[] colors) {
        super(board);

        this.sprite = sprite;
        this.colors = colors;
        this.spawnLocation = spawnLocation.clone().multiply(TILE_SIZE);
        this.state = GhostState.CHASE;
        this.direction = Direction.LEFT;

        scatter = ScatterTarget.INSTANCE;
        frightened = FrightenedRandom.INSTANCE;
        eaten = EatenTarget.INSTANCE;
    }

    @SuppressWarnings("all")
    public Ghost(Ghost other) {
         super(other.getBoard());

        this.colors = other.colors;
        this.spawnLocation = other.spawnLocation;
        this.scatterLocation = other.scatterLocation;
        this.tile = other.tile.clone();
        this.offset = other.offset.clone();
        this.state = other.state;
        this.direction = other.direction;
    }

    public void reset() {
        super.reset();

        tile = spawnLocation.clone().divide(TILE_SIZE);
        offset = new Vector2d(0, MIDPOINT);

        animationFrame = 0;
        state = GhostState.CHASE;
        previousState = null;
        dotCounter = 0;
        released = false;
        wasReleased = false;
        nextDirection = null;

        switch (insertionIndex) {
            case 0:
                direction = Direction.LEFT;
                released = wasReleased = true;
                break;
            case 1:
                direction = Direction.UP;
                released = true;
                break;
            case 2:
                if (board.getLevel() > 1) {
                    released = true;
                    break;
                }
            case 3:
                if (board.getLevel() > 2) {
                    released = true;
                } else {
                    direction = Direction.DOWN;
                }
                break;
        }
    }

    public GhostState getState() {
        return state;
    }

    public void setState(GhostState state) {
        if (state == GhostState.FRIGHTENED && (this.state == GhostState.FRIGHTENED || this.state == GhostState.EATEN))
            return;

        this.direction = direction.behind();
        this.previousState = this.state;
        this.state = state;

        // Sometimes a ghost will regenerate with a block behind it, so the
        // ghost will move into a wall. To avoid this, we use our new state to
        // determine which direction we should be moving.
        nextDirection = getCurrentBehavior().getDirection(this);
        if (nextDirection == direction)
            nextDirection = null;
    }

    public int[] getColors() {
        return colors;
    }

    public Vector2i getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Vector2i spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Vector2i getScatterLocation() {
        return scatterLocation;
    }

    public void setScatterLocation(Vector2i scatterLocation) {
        this.scatterLocation = scatterLocation;
    }

    public int getDotCounter() {
        return dotCounter;
    }

    public void setDotCounter(int dotCounter) {
        this.dotCounter = dotCounter;
    }

    public boolean isReleased() {
        return released;
    }

    public void release() {
        this.released = true;
    }

    public void center() {
        offset.setX(MIDPOINT);
        offset.setY(MIDPOINT);
    }

    public double getSpeed() {

        if (state == GhostState.EATEN)
            return PIXELS_PER_TICK * 2.0;

        int speedIndex1;
        if (board.getLevel() > 4)
            speedIndex1 = 2;
        else if (board.getLevel() > 1)
            speedIndex1 = 1;
        else
            speedIndex1 = 0;

        int speedIndex2;
        if (board.getTile(tile) == TileState.TUNNEL)
            speedIndex2 = 2;
        else if (state == GhostState.FRIGHTENED)
            speedIndex2 = 1;
        else
            speedIndex2 = 0;

        return LEVEL_SPEEDS[speedIndex1][speedIndex2] * PIXELS_PER_TICK;
    }

    public void collide(Player player) {
        if (state == GhostState.FRIGHTENED) {
            state = GhostState.EATEN;
        } else if (state == GhostState.CHASE || state == GhostState.SCATTER) {
            player.kill();
        }
    }

    public Behavior getCurrentBehavior() {
        return switch (state) {
            case SCATTER -> scatter;
            case CHASE -> chase;
            case EATEN -> eaten;
            case FRIGHTENED -> frightened;
        };
    }

    @Override
    public boolean canPass(TileState state) {
        return switch (state) {
            case WALL -> false;
            case SPACE, PELLET, POWER_PELLET -> true;
            case TUNNEL -> this.state != GhostState.FRIGHTENED || board.getTile(tile) == TileState.TUNNEL;
            case MEMBRANE -> this.state == GhostState.EATEN || board.getTile(tile) == TileState.MEMBRANE;
        };
    }

    @Override
    public void tick() {
        super.tick();

        if (getTicksAlive() % 5 == 0)
            animationFrame++;

        if (state == GhostState.FRIGHTENED && board.getFrightTimer() == 0) {
            state = previousState;
            System.out.println("Timer ran out, no longer frightened");
        } else if (state == GhostState.EATEN && spawnLocation.equals(tile)) {
            state = previousState;
            System.out.println("Ghost regenerated at " + spawnLocation);
        }

        // If a ghost is not yet released, it should simply bounce up and down
        // before being released. Ghosts have an internal timer determining
        // when they should be released. This method has compatibility to bounce
        // side to side, should "custom maps" be made
        if (!released) {
            if (offset.getY() == MIDPOINT) {
                direction = direction.behind();
            }
            move(direction, 0.5);
            return;
        }

        // If a ghost is now released, but was not previously released, we need
        // the ghost to follow a specific path in order to exit the ghost pen.
        else if (!wasReleased) {
            int dx = board.getWidth() * MIDPOINT - (tile.getX() * TILE_SIZE + (int) offset.getX());
            Direction dir;
            if (dx < 0) {
                dir = Direction.LEFT;
            } else if (dx > 0) {
                dir = Direction.RIGHT;
            } else {
                dir = Direction.UP;
            }

            System.out.println("ESCAPE " + dir);
            move(dir, 0.50);

            if (isAlignY() && board.getTile(tile) != TileState.MEMBRANE) {
                wasReleased = true;
                nextDirection = Direction.LEFT;
            }
            return;
        }

        // If the board tells the ghost to change states, change states.
        if (state == GhostState.CHASE && !board.isChase()) {
            setState(GhostState.SCATTER);
        } else if (state == GhostState.SCATTER && board.isChase()) {
            setState(GhostState.CHASE);
        }

        if (state == GhostState.EATEN && spawnLocation.equals(tile.clone().multiply(TILE_SIZE)))
            setState(board.isChase() ? GhostState.CHASE : GhostState.SCATTER);


        Player player = board.getPlayer();
        if (player.tile.equals(tile)) {
            collide(player);
        }

        Behavior behavior = getCurrentBehavior();
        double speed = getSpeed();

        boolean changeDir = false;
        if (nextDirection != null && nextDirection != direction) {
            double distance = Math.abs(MIDPOINT - (direction.getDx() != 0.0 ? offset.getX() : offset.getY()));

            if (distance < speed) {
                speed = distance;
                changeDir = true;
            }
        }

        Vector2i old = tile.clone();
        move(direction, speed);

        if (changeDir) {
            direction = nextDirection;
            nextDirection = null;
        }

        if (!old.equals(tile)) {
            nextDirection = behavior.getDirection(this);
            if (nextDirection == direction)
                nextDirection = null;
        }
    }

    public void render(Screen screen) {

        // Determine which tile on the spritesheet to render.
        Vector2i spriteTile = state.getSpriteOffset();
        if (state.isDirectional())
            spriteTile.add((nextDirection != null ? nextDirection : direction).getAnimationFrame() * state.getAnimationFrames(), 0);
        if (state.getAnimationFrames() != 0)
            spriteTile.add(animationFrame % state.getAnimationFrames(), 0);

        // The starting (x, y) coordinates of the sprite tile
        int xOffset = spriteTile.getX() * sprite.getTileWidth();
        int yOffset = spriteTile.getY() * sprite.getTileHeight();
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

        getCurrentBehavior().render(this, screen);

        for (int y = 0; y < sprite.getTileHeight(); y++) {
            int yPos = yScreen + scale * y;
            if (yPos < 0 || yPos >= screen.getHeight())
                yPos %= screen.getHeight();

            for (int x = 0; x < sprite.getTileWidth(); x++) {
                int xPos = xScreen + scale * x;
                if (xPos < 0 || xPos >= screen.getWidth())
                    xPos %= screen.getWidth();

                int[] colors = switch (state) {
                    case FRIGHTENED -> board.getFrightTimer() < 100 ? (board.getFrightTimer() % 20 < 10 ? FLASH : FRIGHT) : FRIGHT;
                    case EATEN -> EATEN;
                    case CHASE, SCATTER -> this.colors;
                };
                int colorIndex = pixels[(y + yOffset) * sprite.getWidth() + (x + xOffset)];
                int color = colors[colorIndex];

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

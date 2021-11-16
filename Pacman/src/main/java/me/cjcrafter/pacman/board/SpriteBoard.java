package me.cjcrafter.pacman.board;

import me.cjcrafter.pacman.file.SpriteSheet;

public class SpriteBoard extends Board {

    private final String resourceLocation;

    public SpriteBoard(String resourceLocation) {
        this.resourceLocation = resourceLocation;

        initBoard();
        //finalizeBoard();
    }

    @Override
    protected void initBoard() {
        SpriteSheet sprite = new SpriteSheet(resourceLocation, (byte) 6);

        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        this.tiles = new TileState[sprite.getHeight()][sprite.getWidth()];
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                int col = sprite.getPixels()[y * sprite.getWidth() + x];
                tiles[y][x] = switch (col) {
                    case 0 -> TileState.SPACE;
                    case 1 -> TileState.TUNNEL;
                    case 2 -> {remainingDots++; yield TileState.PELLET; }
                    case 3 -> TileState.POWER_PELLET;
                    case 4 -> TileState.MEMBRANE;
                    case 5 -> TileState.WALL;
                    default -> throw new InternalError("Grayscale value is outdated or otherwise invalid");
                };
            }
        }
    }
}

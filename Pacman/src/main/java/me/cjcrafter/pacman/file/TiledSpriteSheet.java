package me.cjcrafter.pacman.file;

public class TiledSpriteSheet extends SpriteSheet {

    private final int tileWidth;
    private final int tileHeight;

    public TiledSpriteSheet(String name, byte grayScale, int tileWidth, int tileHeight) {
        super(name, grayScale);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int countTilesWidth() {
        return getWidth() / getTileWidth();
    }

    public int countTilesHeight() {
        return getHeight() / getTileHeight();
    }
}

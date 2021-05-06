package me.cjcrafter.pacman.file;

import me.cjcrafter.neat.ui.Screen;
import me.cjcrafter.pacman.Vector2i;

public class TextSheet {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARS = "1234567890 !?,.";

    private final TiledSpriteSheet sprite;
    private final int size;

    public TextSheet(String file, int size) {
        this.sprite = new TiledSpriteSheet(file, (byte) 2, size, size);
        this.size = size;
    }

    public TextSheet(TiledSpriteSheet sprite) {
        this.sprite = sprite;
        this.size = sprite.getTileWidth();
    }

    public Vector2i getPos(char c) {
        Vector2i pos = new Vector2i();

        if (Character.isAlphabetic(c)) {
            if (Character.isUpperCase(c)) {
                pos.setX(UPPER.indexOf(c));
                pos.setY(0);
            } else if (Character.isLowerCase(c)) {
                pos.setX(LOWER.indexOf(c));
                pos.setY(1);
            }
        } else {
            int index = CHARS.indexOf(c);
            if (index == -1) {
                return null;
            } else {
                pos.setX(index);
                pos.setY(2);
            }
        }

        return pos;
    }

    public void render(Screen screen, String text, Vector2i pos, int col) {
        byte[] pixels = sprite.getPixels();

        for (int i = 0; i < text.length(); i++) {
            Vector2i spritePosition = getPos(text.charAt(i)).multiply(size);

            if (spritePosition == null)
                throw new IllegalArgumentException("Unexpected Character: " + text.charAt(i));

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (pixels[(spritePosition.getY() + y) * sprite.getWidth() + spritePosition.getX() + x] != 0)
                        screen.setPixel(pos.getX() + i * size + x, pos.getY() + y, col);
                }
            }
        }
    }

    public void renderLeft(Screen screen, String text, Vector2i pos, int col) {
        byte[] pixels = sprite.getPixels();
        pos.add(-(text.length() - 1) * size, 0);

        for (int i = text.length() - 1; i >= 0; i--) {
            Vector2i spritePosition = getPos(text.charAt(i)).multiply(size);

            if (spritePosition == null)
                throw new IllegalArgumentException("Unexpected Character: " + text.charAt(i));

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (pixels[(spritePosition.getY() + y) * sprite.getWidth() + spritePosition.getX() + x] != 0)
                        screen.setPixel(pos.getX() + i * size + x, pos.getY() + y, col);
                }
            }
        }
    }
}

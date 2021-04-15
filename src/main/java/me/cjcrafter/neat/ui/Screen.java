package me.cjcrafter.neat.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public abstract class Screen {

    private final BufferedImage img;
    private final int[] pixels;
    protected final Dimension bound;

    public Screen(int width, int height) {
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        this.bound = new Dimension(width, height);
    }

    public int getWidth() {
        return img.getWidth();
    }

    public int getHeight() {
        return img.getHeight();
    }

    public void fill(Color color) {
        fill(color.getRGB());
    }

    public void fill(int color) {
        Arrays.fill(pixels, color);
    }

    public void setPixel(int x, int y, Color color) {
        setPixel(x, y, color.getRGB());
    }

    public void setPixel(int x, int y, int color) {
        if (x >= 0 && x < bound.width && y >= 0 && y < bound.height)
            pixels[y * bound.width + x] = color;
    }

    public void fill(Rectangle rectangle, Color color) {
        fill(rectangle, color.getRGB());
    }

    public void fill(Rectangle rectangle, int color) {
        int yBound = rectangle.y + rectangle.height;
        for (int y = rectangle.y; y < yBound; y++) {
            int xBound = rectangle.x + rectangle.width;
            for (int x = rectangle.x; x < xBound; x++) {
                setPixel(x, y, color);
            }
        }
    }

    public void outline(Rectangle rectangle, Color color) {
       outline(rectangle, color.getRGB());
    }

    public void outline(Rectangle rectangle, int color) {
        int x = rectangle.x;
        int y = rectangle.y;

        int yBound = rectangle.y + rectangle.height;
        int xBound = rectangle.x + rectangle.width;

        // Limited to 1 pixel wide, but is more efficient then looping through
        // every pixel in a rectangle.
        for (; y < yBound; y++)
            setPixel(x, y, color);
        for (; x < xBound; x++)
            setPixel(x, y, color);
        for (; y >= rectangle.y; y--)
            setPixel(x, y, color);
        for (; x >= rectangle.x; x--)
            setPixel(x, y, color);
    }

    public Graphics2D getGraphics() {
        return img.createGraphics();
    }

    public void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font, Color color) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        if (font != null) g.setFont(font);
        if (color != null) g.setColor(color);
        g.drawString(text, x, y);
    }

    public int[] getPixels() {
        return pixels;
    }

    public abstract void render();
}

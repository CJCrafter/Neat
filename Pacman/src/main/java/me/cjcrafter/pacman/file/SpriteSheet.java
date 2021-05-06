package me.cjcrafter.pacman.file;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class SpriteSheet {

    private final byte[] pixels;
    private final int width, height;

    public SpriteSheet(String name, byte grayScale) {
        URL resource = getClass().getClassLoader().getResource(name);
        if (resource == null)
            throw new IllegalArgumentException("Unknown resource: " + name);

        BufferedImage img;
        try {
            img = ImageIO.read(resource);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid image: " + name, e);
        }

        byte gray = (byte) (0xFF / --grayScale);

        pixels = new byte[img.getWidth() * img.getHeight()];
        for (int y = 0; y < img.getHeight(); y++) {
            //System.out.println();
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                byte scaled = (byte) ((rgb & 0xFF) / gray);
                pixels[y * img.getWidth() + x] = scaled;
                //System.out.print(scaled);
                //System.out.print('\t');
            }
        }

        this.width = img.getWidth();
        this.height = img.getHeight();
    }

    public byte[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

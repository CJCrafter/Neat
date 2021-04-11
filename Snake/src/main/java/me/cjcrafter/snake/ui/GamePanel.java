package me.cjcrafter.snake.ui;

import me.cjcrafter.neat.Species;
import me.cjcrafter.snake.board.Board;
import me.cjcrafter.snake.board.Vector2d;
import me.cjcrafter.snake.input.NeatSnake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GamePanel extends JPanel {

    public static final Map<Species, Color> speciesColorMap = Collections.synchronizedMap(new HashMap<>());

    private final Board board;
    private final BufferedImage img;
    private final int[] pixels;

    private Image head;

    public GamePanel(Board board) {
        this.board = board;
        this.img = new BufferedImage(board.getSize(), board.getSize(), BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

        try {
            head = ImageIO.read(getClass().getClassLoader().getResource("collin.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fill(Color color) {
        fill(color.getRGB());
    }

    public void fill(int rgb) {
        Arrays.fill(pixels, rgb);
    }

    public void drawSquare(int xBoard, int yBoard, int rgb) {
        int width = img.getWidth() / board.getSize();
        int height = img.getHeight() / board.getSize();

        int xOff = xBoard * width;
        int yOff = yBoard * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                paint(xOff + x, yOff + y, rgb);
            }
        }
    }

    public void paint(int x, int y, int rgb) {
        int loc = y * img.getWidth() + x;
        if (loc < 0 || loc >= pixels.length)
            return;

        pixels[loc] = rgb;
    }

    @Override
    protected void paintComponent(Graphics g) {

        fill(board.getSnake().isAlive() ? 0 : 0xFF0000);

        Color snakeColor = board.getSnake().isAlive() ? new Color(0x3B9C11) : new Color(0xFF8900);
        if (board.getSnake() instanceof NeatSnake) {
            snakeColor = speciesColorMap.computeIfAbsent(((NeatSnake) board.getSnake()).getClient().getSpecies(),
                    s -> new Color(ThreadLocalRandom.current().nextInt(0xFFFFFF)));
        }
        try {
            for (Vector2d vector : board.getSnake()) {
                if (vector == null) continue;

                drawSquare(vector.getX(), vector.getY(), snakeColor.getRGB());
            }
        } catch (ConcurrentModificationException ignore) {
            return;
        }

        drawSquare(board.getApple().getX(), board.getApple().getY(), 0xFF0000);
        g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

        if (!board.getSnake().isAlive()) {
            int score = board.getSnake() instanceof NeatSnake
                    ? (int) ((NeatSnake) board.getSnake()).getClient().getScore()
                    : board.getSnake().size();
            g.setColor(Color.WHITE);
            drawCenteredString(g, String.valueOf(score), getVisibleRect(), new Font("Engravers MT", Font.BOLD, 24));
        }

        //for (Vector2d vector : board.getSnake()) {
        //    //Vector2d vector = board.getSnake().getFirst();
        //    int width = getWidth() / board.getSize();
        //    int height = getHeight() / board.getSize();
        //    int bufferX = width / 8;
        //    int bufferY = width / 8;
        //    g.drawImage(head, vector.getX() * width - bufferX,
        //            vector.getY() * height - bufferY,
        //            width + 2 * bufferX, height + 2 * bufferY, null);
        //}
    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setFont(font);
        g.drawString(text, x, y);
    }
}

package me.cjcrafter.neat.ui;

import me.cjcrafter.neat.Neat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

public class NeatFrame extends JFrame {

    public static final int BORDER_WIDTH = 5;
    public static final int BORDER_HEIGHT = BORDER_WIDTH;

    protected final JPanel buttonHolder;
    private final JPanel clientPanel;
    protected final Neat neat;

    private final BufferedImage img;
    private final int[] pixels;
    private final Dimension grid;
    private final int cellWidth;
    private final int cellHeight;
    protected final ClientScreen[] clients;

    public NeatFrame(String name, Neat neat, Dimension resolution, Dimension grid) {
        super(name);

        Dimension imgRes = new Dimension(resolution.width + (grid.width - 1) * BORDER_WIDTH,
                resolution.height + (grid.height - 1) * BORDER_HEIGHT);

        this.neat = neat;

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 1000));
        setPreferredSize(new Dimension(900, 1000));

        clientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                paintScreen(g);
            }
        };
        clientPanel.setPreferredSize(new Dimension(grid.width * 10, grid.height * 10));
        add(clientPanel, BorderLayout.CENTER);

        buttonHolder = new JPanel();
        fillButtonHolder();

        this.img = new BufferedImage(imgRes.width, imgRes.height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        this.grid = grid;
        this.cellWidth = resolution.width / grid.width;
        this.cellHeight = resolution.height / grid.height;
        this.clients = new ClientScreen[grid.height * grid.width];

        System.out.println("Cell Size: " + cellWidth + ", " + cellHeight);

        Arrays.fill(pixels, 0x3c3f41);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void fillClients(Function<Rectangle, ? extends ClientScreen> supplier) {
        for (int i = 0; i < clients.length; i++) {
            int x = (i % grid.width) * (cellWidth + BORDER_WIDTH);
            int y = (i / grid.height) * (cellHeight + BORDER_HEIGHT);
            Rectangle rectangle = new Rectangle(x, y, cellWidth, cellHeight);
            clients[i] = supplier.apply(rectangle);
        }
    }

    protected void fillButtonHolder() {
        buttonHolder.setPreferredSize(new Dimension(900, 50));
        add(buttonHolder, BorderLayout.NORTH);
    }

    public void paintScreen(Graphics g) {
        g.drawImage(img, 0, 0, clientPanel.getWidth(), clientPanel.getHeight(), null);
    }

    public void render() {
        sortClients();

        for (int i = 0; i < clients.length; i++) {
            try {
                clients[i].render();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                continue;
            }

            if (!clients[i].isMadeChanges())
                continue;
            int[] pixels = clients[i].getPixels();

            int xOffset = (i % grid.width) * (cellWidth + BORDER_WIDTH);
            int yOffset = (i / grid.width) * (cellHeight + BORDER_HEIGHT);
            for (int y = 0; y < clients[i].getHeight(); y++) {
                for (int x = 0; x < clients[i].getWidth(); x++) {
                    int loc = (y + yOffset) * img.getWidth() + (x + xOffset);
                    this.pixels[loc] = pixels[y * clients[i].getWidth() + x];
                }
            }
        }

        clientPanel.repaint();
    }

    public void sortClients() {
        Arrays.sort(clients, Comparator.comparingInt(o -> o.client.getSpecies().getBase().getId()));
    }
}

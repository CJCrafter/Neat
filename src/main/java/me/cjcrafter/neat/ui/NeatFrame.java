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

    public static final int BORDER_WIDTH = 1;
    public static final int BORDER_HEIGHT = 1;

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

        this.img = new BufferedImage(resolution.width, resolution.height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        this.grid = grid;
        this.cellWidth = resolution.width / grid.width;
        this.cellHeight = resolution.height / grid.height;
        this.clients = new ClientScreen[grid.height * grid.width];

        Arrays.fill(pixels, 0x3c3f41);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void fillClients(Function<Rectangle, ? extends ClientScreen> supplier) {
        int cellWidth = (img.getWidth() - grid.width + 1) / grid.width;
        int cellHeight = (img.getHeight() - grid.height + 1) / grid.height;
        for (int i = 0; i < clients.length; i++) {
            Rectangle rectangle = new Rectangle(i % grid.width, i / grid.width, cellWidth, cellHeight);
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
        int customCellWidth = (img.getWidth() - grid.width + 1) / grid.width;
        int customCellHeight = (img.getHeight() - grid.height + 1) / grid.height;

        for (int i = 0; i < clients.length; i++) {
            try {
                clients[i].render();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                continue;
            }

            int[] pixels = clients[i].getPixels();

            int yOffset = i / grid.width * cellHeight;
            int xOffset = i % grid.width * cellWidth;
            for (int y = 0; y < customCellHeight; y++) {
                for (int x = 0; x < customCellWidth; x++) {
                    int loc = (y + yOffset) * img.getWidth() + (x + xOffset);
                    this.pixels[loc] = pixels[y * customCellWidth + x];
                }
            }
        }

        clientPanel.repaint();
    }

    public void sortClients() {
        Arrays.sort(clients, Comparator.comparingInt(o -> o.client.getSpecies().getBase().getId()));
    }
}

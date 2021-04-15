package me.cjcrafter.neat.ui;

import me.cjcrafter.neat.Client;
import me.cjcrafter.neat.genome.ConnectionGene;
import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.NodeGene;

import java.awt.*;

public class ClientScreen extends Screen {

    public static final int BACKGROUND = new Color(33, 33, 33).getRGB();
    private static final Color INPUT_COLOR = new Color(0x00FF51);
    private static final Color HIDDEN_COLOR = new Color(0x00AEFF);
    private static final Color OUTPUT_COLOR = new Color(0xFF002F);

    protected Client client;
    private boolean showGenome;
    protected boolean madeChanges;

    public ClientScreen(int width, int height, Client client) {
        super(width, height);

        this.client = client;
    }

    public boolean isShowGenome() {
        return showGenome;
    }

    public void setShowGenome(boolean showGenome) {
        this.showGenome = showGenome;
    }

    public boolean isMadeChanges() {
        return madeChanges;
    }

    @Override
    public void render() {
        madeChanges = false;
        Thread.yield();
        if (showGenome)
            renderGenome();
        else
            renderClient();
    }

    public void renderGenome() {
        madeChanges = true;
        fill(BACKGROUND);
        Genome genome = client.getGenome();
        if (genome == null)
            return;

        Iterable<ConnectionGene> connections = genome.getConnections();
        Iterable<NodeGene> nodes = genome.getNodes();
        int nodeRadius = 2;
        Graphics2D g = getGraphics();
        BasicStroke solid = new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        BasicStroke dashed = new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0);

        for (ConnectionGene connection : connections) {
            g.setStroke(connection.isEnabled() ? solid : dashed);
            int shade = (int) (Math.max(Math.min(1.0, connection.getWeight() / 10), 0.0) * (0xB7 - 0x3B) + 0x3B);
            g.setColor(new Color(shade, shade, shade));
            Point from = new Point((int) (connection.getFrom().getX() * getWidth()), (int) (connection.getFrom().getY() * getHeight()));
            Point to = new Point((int) (connection.getTo().getX() * getWidth()), (int) (connection.getTo().getY() * getHeight()));
            g.drawLine(from.x, from.y, to.x, to.y);
        }

        g.setStroke(solid);
        for (NodeGene node : nodes) {
            Color color;
            if (node.getX() <= 0.1) {
                color = INPUT_COLOR;
            } else if (node.getX() >= 0.9) {
                color = OUTPUT_COLOR;
            } else {
                color = HIDDEN_COLOR;
            }

            g.setColor(color);
            int x = (int) (node.getX() * getWidth());
            int y = (int) (node.getY() * getHeight());
            Rectangle rectangle = new Rectangle(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
            g.fillOval(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            //drawCenteredString(g, String.valueOf(node.getId()), rectangle, null, Color.WHITE);
        }
        g.dispose();
    }

    public void renderClient() {

    }
}

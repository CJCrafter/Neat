package me.cjcrafter.neat.ui;

import me.cjcrafter.neat.genome.ConnectionGene;
import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.NodeGene;

import javax.swing.*;
import java.awt.*;

public class GenePanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(0xEBEBEB);
    private static final Color DISABLED_COLOR = new Color(0x919191);
    private static final Color ENABLED_COLOR = new Color(0x141414);
    private static final Color INPUT_COLOR = new Color(0x00FF51);
    private static final Color HIDDEN_COLOR = new Color(0x00AEFF);
    private static final Color OUTPUT_COLOR = new Color(0xFF002F);

    private Genome genome;

    public GenePanel() {
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, 10000, 10000);
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, 10000, 10000);

        if (genome == null)
            return;

        for (ConnectionGene c : genome.getConnections()) {
            paintConnection(c, (Graphics2D) g);
        }

        for (NodeGene n : genome.getNodes()) {
            paintNode(n, (Graphics2D) g);
        }

    }

    private void paintNode(NodeGene n, Graphics2D g) {
        Color color;
        if (n.getX() <= 0.1) {
            color = INPUT_COLOR;
        } else if (n.getX() >= 0.9) {
            color = OUTPUT_COLOR;
        } else {
            color = HIDDEN_COLOR;
        }

        g.setColor(color);
        g.setStroke(new BasicStroke(3));
        g.fillOval((int) (this.getWidth() * n.getX()) - 10,
                (int) (this.getHeight() * n.getY()) - 10, 20, 20);

        g.setColor(color.darker().darker());
        g.drawString(String.valueOf(n.hashCode()), (int) (getWidth() * n.getX()) - 3, (int) (getHeight() * n.getY()) + 5);
    }

    private void paintConnection(ConnectionGene c, Graphics2D g) {
        g.setColor(c.isEnabled() ? ENABLED_COLOR : DISABLED_COLOR);
        g.setStroke(new BasicStroke(1));
        g.drawLine(
                (int) (this.getWidth() * c.getFrom().getX()),
                (int) (this.getHeight() * c.getFrom().getY()),
                (int) (this.getWidth() * c.getTo().getX()),
                (int) (this.getHeight() * c.getTo().getY()));

        g.setColor(g.getColor().brighter());
        g.drawString((c.getWeight() + "       ").substring(0, 7),
                (int) ((c.getTo().getX() + c.getFrom().getX()) * 0.5 * this.getWidth()),
                (int) ((c.getTo().getY() + c.getFrom().getY()) * 0.5 * this.getHeight()) + 15);
    }
}

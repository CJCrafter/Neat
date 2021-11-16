package me.cjcrafter.pacman.ui;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.ui.ClientScreen;
import me.cjcrafter.neat.ui.NeatFrame;
import me.cjcrafter.pacman.Pacman;

import javax.swing.*;
import java.awt.*;

public class PacmanFrame extends NeatFrame {

    private final Pacman pacman;

    public PacmanFrame(Neat neat, Pacman pacman) {
        super("Pac-Man", neat, new Dimension(28 * 50, 36 * 20), new Dimension(5, 2));

        this.pacman = pacman;
    }

    @Override
    protected void fillButtonHolder() {
        super.fillButtonHolder();

        buttonHolder.setLayout(new GridLayout(0, 3));

        JScrollBar tickSpeedBar = new JScrollBar(JScrollBar.HORIZONTAL, 60, 5, 0, 500);
        tickSpeedBar.setToolTipText("Tick Speed");
        tickSpeedBar.addAdjustmentListener(e -> pacman.setTickRate(e.getValue()));
        buttonHolder.add(tickSpeedBar);

        JCheckBox showGenome = new JCheckBox("Show Genomes");
        showGenome.addItemListener(e -> {
            for (ClientScreen client : clients) {
                client.setShowGenome(((JCheckBox) e.getItem()).isSelected());
            }
        });
        buttonHolder.add(showGenome);


    }
}

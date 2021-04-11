package me.cjcrafter.snake.ui;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.ui.ClientScreen;
import me.cjcrafter.neat.ui.NeatFrame;
import me.cjcrafter.snake.Game;

import javax.swing.*;
import java.awt.*;

public class SnakeFrame extends NeatFrame {

    private Game game;

    public SnakeFrame(String name, Neat neat, Game game, Dimension resolution, Dimension grid) {
        super(name, neat, resolution, grid);

        this.game = game;
    }

    @Override
    protected void fillButtonHolder() {
        super.fillButtonHolder();
        buttonHolder.setLayout(new GridLayout(0, 2));

        JScrollBar tickSpeedBar = new JScrollBar(JScrollBar.HORIZONTAL, 10, 5, 0, 500);
        tickSpeedBar.setToolTipText("Tick Speed");
        tickSpeedBar.addAdjustmentListener(e -> game.setTickRate(e.getValue()));
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

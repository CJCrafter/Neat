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

        JMenu game = new JMenu("Game");

        JSlider tickRate = new JSlider(JSlider.HORIZONTAL, 0, 800, 10);
        tickRate.addChangeListener(e -> this.game.setTickRate(tickRate.getValue()));
        game.add(tickRate);

        JCheckBox showGenome = new JCheckBox("Show Genomes");
        showGenome.addItemListener(e -> {
            for (ClientScreen client : clients) {
                client.setShowGenome(((JCheckBox) e.getItem()).isSelected());
            }
        });

        game.add(showGenome);
        menu.add(game);
    }
}

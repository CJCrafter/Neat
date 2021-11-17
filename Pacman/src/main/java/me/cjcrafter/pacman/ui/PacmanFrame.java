package me.cjcrafter.pacman.ui;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.ui.NeatFrame;
import me.cjcrafter.pacman.Pacman;

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


    }
}

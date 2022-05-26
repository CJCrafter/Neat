package me.cjcrafter.pacman.ui;

import me.cjcrafter.neat.Client;
import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.ui.NeatFrame;
import me.cjcrafter.pacman.Pacman;
import me.cjcrafter.pacman.board.Board;
import me.cjcrafter.pacman.entity.NeatPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class PacmanFrame extends NeatFrame {

    private final Pacman pacman;

    public PacmanFrame(Neat neat, Pacman pacman) {
        super("Pac-Man", neat, new Dimension(28 * 50, 36 * 20), new Dimension(5, 2));

        this.pacman = pacman;
    }

    @Override
    protected void fillButtonHolder() {
        super.fillButtonHolder();

        JMenu game = new JMenu("Game");

        JCheckBox pause = new JCheckBox("Pause");
        pause.addActionListener(e -> pacman.setPause(pause.isSelected()));
        game.add(pause);

        JSlider tickRate = new JSlider(JSlider.HORIZONTAL, 0, 1000, 60);
        tickRate.addChangeListener(e -> pacman.setTickRate((float) tickRate.getValue()));
        game.add(tickRate);

        menu.add(game);
    }

    @Override
    public void loadNeat(File file) {
        boolean previous = pacman.isPause();
        pacman.setPause(true);

        // synchronized for my own sanity/readability
        synchronized (pacman) {
            super.loadNeat(file);

            List<Board> boards = pacman.getBoards();
            List<Client> clients = neat.getClients();

            // There should be an equal number of clients and players
            assert boards.size() == clients.size();

            for (int i = 0; i < boards.size(); i++) {
                Board board = boards.get(i);
                Client client = clients.get(i);

                if (board.getPlayer() instanceof NeatPlayer player) {
                    player.setClient(client);
                } else {
                    System.err.println("Player was not a NeatPlayer? " + board.getPlayer());
                }
            }

            pacman.reset();
        }


        pacman.setPause(previous);
    }
}

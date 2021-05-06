package me.cjcrafter.pacman.ui;

import me.cjcrafter.neat.ui.Screen;
import me.cjcrafter.pacman.board.Board;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {

    private final Screen screen;
    private final Board board;
    private final JPanel panel;

    public Frame(Dimension dimension, Board board) {
        super("Pac-Man");

        Dimension frameSize = new Dimension(Board.DEFAULT_WIDTH * 20, Board.DEFAULT_HEIGHT * 20);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(frameSize);
        setMinimumSize(frameSize);
        setLocationRelativeTo(null);

        this.screen = new Screen(dimension.width, dimension.height) {public void render() {}};
        this.board = board;
        this.panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                render();
                g.drawImage(screen.getImg(), 0, 0, getWidth(), getHeight(), null);
            }
        };

        add(panel);
        setVisible(true);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public Screen getScreen() {
        return screen;
    }

    public Board getBoard() {
        return board;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void render() {
        board.render(screen);
    }
}

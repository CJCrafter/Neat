package me.cjcrafter.pacman.ui;

import me.cjcrafter.neat.Client;
import me.cjcrafter.neat.ui.ClientScreen;
import me.cjcrafter.pacman.board.Board;

public class PacmanScreen extends ClientScreen {

    private Board board;
    private boolean wasAlive;

    public PacmanScreen(int width, int height, Client client, Board board) {
        super(width, height, client);

        this.board = board;
    }

    @Override
    public void renderGenome() {
        wasAlive = true;
        super.renderGenome();
    }

    @Override
    public void renderClient() {
        if (!wasAlive) {
            if (board.getPlayer().isAlive())
                wasAlive = true;
            else
                return;
        }

        madeChanges = true;
        board.render(this);
    }
}

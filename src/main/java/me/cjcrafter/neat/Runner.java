package me.cjcrafter.neat;

import me.cjcrafter.neat.ui.Frame;

public class Runner {

    public static void main(String[] args) {
        Neat neat = new Neat(5, 3, 0);
        Frame frame = new Frame();
        frame.setGenome(neat.newGenome());
    }
}

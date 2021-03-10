package me.cjcrafter.neat;

import me.cjcrafter.neat.ui.Frame;

public class Runner {

    public static void main(String[] args) {
        Neat neat = new Neat(5, 1, 100);
        neat.temp();

        Frame frame = new Frame();
        //frame.setGenome(neat.getC);
    }
}

package me.cjcrafter.neat;

import me.cjcrafter.neat.ui.Frame;

public class Runner {

    public static void main(String[] args) {
        Neat neat = new Neat(30, 3, 0);
        Frame frame = new Frame();
        frame.setGenome(neat.newGenome());

        System.out.println("NEAT: " + Integer.toBinaryString(Neat.MAX_NODES));
        System.out.println("NEAT: " + Integer.toBinaryString(Neat.MAX_NODES * 7));
        System.out.println("COUNT: " + Integer.toBinaryString(Neat.MAX_NODES).length());
        System.out.println("MEMORY: " + (Integer.MAX_VALUE / 1e-9 * 4));
    }
}

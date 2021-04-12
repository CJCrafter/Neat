package me.cjcrafter.neat;

import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.Mutation;
import me.cjcrafter.neat.ui.GenePanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Neat neat = new Neat(3, 3, 1);
        Genome genome = neat.newGenome();
        for (int i = 0; i < 5; i++) {
            Mutation.ADD_LINK.mutate(genome);
        }
        for (int i = 0; i < 2; i++) {
            Mutation.ADD_NODE.mutate(genome);
        }
        for (int i = 0; i < 5; i++) {
            Mutation.ADD_LINK.mutate(genome);
        }

        Mutation.REMOVE_NODE.mutate(genome);

        JFrame frame = new JFrame();
        GenePanel panel = new GenePanel();
        panel.setGenome(genome);
        frame.add(panel);
        frame.setVisible(true);


    }
}

package me.cjcrafter.neat.ui;

import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.Mutation;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Frame extends JFrame {

    private Genome genome;
    private final GenePanel panel;

    public Frame() {
        super("NEAT Debugger");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(1000, 700));
        setPreferredSize(new Dimension(1000, 700));

        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
        try {
            UIManager.setLookAndFeel(looks[3].getClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel buttonHolder = new JPanel();
        buttonHolder.setPreferredSize(new Dimension(1000, 100));
        buttonHolder.setLayout(new GridLayout(1, 6));

        for (Mutation mutation : Mutation.values()) {
            buttonHolder.add(getButton(mutation.name, mutation::mutate));
        }

        this.add(buttonHolder, BorderLayout.NORTH);

        this.panel = new GenePanel();
        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
        this.panel.setGenome(genome);
    }

    private JButton getButton(String id, Consumer<Genome> function) {
        JButton button = new JButton(id);
        button.addActionListener(e -> {
            function.accept(Frame.this.genome);
            Frame.this.repaint();
        });
        return button;
    }
}

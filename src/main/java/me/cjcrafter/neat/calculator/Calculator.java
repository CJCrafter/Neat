package me.cjcrafter.neat.calculator;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.genome.ConnectionGene;
import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.NodeGene;
import me.cjcrafter.neat.genome.NodeType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calculator {

    private final List<Node> input;
    private final List<Node> output;
    private final List<Node> hidden;

    public Calculator(Genome genome) {
        Neat neat = genome.getNeat();
        int inputCount = neat.getInputNodes();
        int outputCount = neat.getOutputNodes();
        int totalVisible = inputCount + outputCount;

        input = new ArrayList<>(inputCount);
        output = new ArrayList<>(outputCount);
        hidden = new ArrayList<>(genome.getNodes().size() - totalVisible);

        Map<NodeGene, Node> temp = new HashMap<>();

        for (NodeGene gene : genome.getNodes()) {
            Node node = new Node(gene.getX());
            temp.put(gene, node);

            if (gene.getType() == NodeType.INPUT) {
                input.add(node);
            } else if (gene.getType() == NodeType.OUTPUT) {
                output.add(node);
            } else {
                hidden.add(node);
            }
        }

        hidden.sort(Comparator.naturalOrder());

        for (ConnectionGene gene : genome.getConnections()) {
            Connection connection = new Connection(temp.get(gene.getFrom()), temp.get(gene.getTo()));
            connection.setWeight(gene.getWeight());
            connection.setEnabled(gene.isEnabled());
            connection.getTo().addConnection(connection);
        }
    }

    public double[] calculate(double... input) {
        if (input.length != this.input.size())
            throw new IllegalArgumentException("Invalid amount of data. " + input.length + " vs " + this.input.size());

        for (int i = 0; i < input.length; i++)
            this.input.get(i).setOutput(input[i]);

        for (Node node : hidden)
            node.calculate();

        double[] output = new double[this.output.size()];
        for (int i = 0; i < output.length; i++) {
            Node node = this.output.get(i);
            node.calculate();
            output[i] = node.getOutput();
        }

        return output;
    }
}

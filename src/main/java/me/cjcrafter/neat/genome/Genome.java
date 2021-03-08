package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.calculator.Calculator;
import me.cjcrafter.neat.util.SortedList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {

    private final SortedList<ConnectionGene> connections;
    private final SortedList<NodeGene> nodes;
    private final Neat neat;

    private Calculator calculator;

    public Genome(Neat neat) {
        this.neat = neat;

        this.connections = new SortedList<>(1 << (Neat.MAX_NODE_BITS << 1));
        this.nodes = new SortedList<>(Neat.MAX_NODES);

        connections.setComparator(Comparator.comparingInt(Gene::getId));
        nodes.setComparator(Comparator.comparingInt(Gene::getId));
    }

    public SortedList<ConnectionGene> getConnections() {
        return connections;
    }

    public SortedList<NodeGene> getNodes() {
        return nodes;
    }

    public Neat getNeat() {
        return neat;
    }

    public void add(NodeGene gene) {
        nodes.add(gene);
    }

    public void add(ConnectionGene gene) {
        connections.add(gene);
    }

    public double distance(Genome g2) {
        Genome g1 = this;

        int highest1 = g1.connections.isEmpty() ? 0 : g1.connections.getTail().id;
        int highest2 = g2.connections.isEmpty() ? 0 : g2.connections.getTail().id;

        // We flip the genomes to make sure we use as much data as we can.
        if (highest1 < highest2) {
            g1 = g2;
            g2 = this;
        }

        int disjoint = 0, similar = 0;
        double weightDiff = 0.0;

        Iterator<ConnectionGene> iterator1 = g1.connections.iterator();
        Iterator<ConnectionGene> iterator2 = g2.connections.iterator();
        ConnectionGene node1 = iterator1.next(), node2 = iterator2.next();
        boolean check1 = true, check2 = true;
        int index = 0;
        while (iterator1.hasNext() && iterator2.hasNext()) {

            // Determine which lists to go to the next node on
            if (check1) {
                node1 = iterator1.next();
                index++;
            } else
                check1 = true;
            if (check2)
                node2 = iterator2.next();
            else
                check2 = true;

            // Determine the distance between the 2 nodes
            if (node1.getId() == node2.getId()) {
                weightDiff += Math.abs(node1.getWeight() - node2.getWeight());
                similar++;
            } else if (node1.getId() > node2.getId()) {
                check1 = false;
                disjoint++;
            } else {
                check2 = false;
                disjoint++;
            }
        }

        // 20 is a specific test case for the NEAT algorithm. Any genome under
        // 20 connections is considered to be small, and using this factor n
        // is redundant.
        int n = Math.max(g1.connections.size(), g2.connections.size());
        if (n < 20) {
            n = 1;
        }

        weightDiff /= similar;
        int excess = g1.connections.size() - index;

        return neat.getFactor1() * excess / n + neat.getFactor2() * disjoint / n + neat.getFactor3() * weightDiff;
    }

    public void mutate(Mutation mutation) {
        calculator = null;
        mutation.mutate(this);
    }

    public Calculator getCalculator() {
        return calculator == null ? (calculator = new Calculator(this)) : calculator;
    }

    public static Genome crossOver(Genome g1, Genome g2) {

        // We flip the genomes to make sure we use as much data as we can.
        if (g1.connections.getTail().id < g2.connections.getTail().id) {
            Genome temp = g1;
            g1 = g2;
            g2 = temp;
        }

        Genome child = g1.neat.newGenome();

        Iterator<ConnectionGene> iterator1 = g1.connections.iterator();
        Iterator<ConnectionGene> iterator2 = g2.connections.iterator();
        ConnectionGene node1 = iterator1.next(), node2 = iterator2.next();
        boolean check1 = true, check2 = true;
        while (iterator1.hasNext() && iterator2.hasNext()) {

            // Determine which lists to go to the next node on
            if (check1) node1 = iterator1.next();
            else        check1 = true;
            if (check2) node2 = iterator2.next();
            else        check2 = true;

            if (node1.getId() == node2.getId()) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    child.add(new ConnectionGene(node1));
                } else {
                    child.add(new ConnectionGene(node2));
                }

            } else if (node1.getId() > node2.getId()) {
                check1 = false;
            } else {
                child.add(new ConnectionGene(node1));
                check2 = false;
            }
        }

        // Copy the rest of the connections over
        while (iterator1.hasNext()) {
            child.add(new ConnectionGene(iterator1.next()));
        }

        child.connections.forEach(connection -> {
            child.nodes.add(connection.getFrom());
            child.nodes.add(connection.getTo());
        });

        return child;
    }

    @Override
    public String toString() {
        return "Genome{" +
                "\n\tconnections=" + connections +
                ", \n\tnodes=" + nodes +
                ", \n\tneat=" + neat +
                '}';
    }
}

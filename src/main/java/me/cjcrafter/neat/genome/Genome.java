package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.util.SortedList;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {

    private final SortedList<ConnectionGene> connections;
    private final SortedList<NodeGene> nodes;
    private final Neat neat;

    public Genome(Neat neat) {
        this.neat = neat;

        this.connections = new SortedList<>(Neat.MAX_NODES);
        this.nodes = new SortedList<>(Neat.MAX_NODES);
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

        // We flip the genomes to make sure we use as much data as we can.
        if (g1.connections.getTail().id < g2.connections.getTail().id) {
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
            if (check1) {
                node1 = iterator1.next();
                index++;
            } else
                check1 = true;
            if (check2)
                node2 = iterator2.next();
            else
                check2 = true;

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

    public void mutate() {
    }

    public static Genome crossOver(Genome g1, Genome g2) {
        Genome child = g1.neat.newGenome();

        int index1 = 0, index2 = 0;
        while (index1 < g1.connections.size() && index2 < g2.connections.size()) {

            ConnectionGene node1 = g1.connections.get(index1);
            ConnectionGene node2 = g2.connections.get(index2);

            if (node1.getId() == node2.getId()) {
                index1++;
                index2++;

                if (ThreadLocalRandom.current().nextBoolean()) {
                    child.add(new ConnectionGene(node1));
                } else {
                    child.add(new ConnectionGene(node2));
                }

            } else if (node1.getId() > node2.getId()) {
                index2++;
            } else {
                child.add(new ConnectionGene(node1));
                index1++;
            }
        }

        g1.connections.forEach(connection -> child.add(new ConnectionGene(connection)));

        child.connections.forEach(connection -> {
            child.nodes.add(connection.getFrom());
            child.nodes.add(connection.getTo());
        });

        return child;
    }
}

package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.file.Serializable;
import me.cjcrafter.neat.util.SortedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class Genome implements Serializable {

    private final SortedList<ConnectionGene> connections;
    private final SortedList<NodeGene> nodes;
    private final Neat neat;

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

        int highest1 = g1.connections.isEmpty() ? 0 : g1.connections.getLast().id;
        int highest2 = g2.connections.isEmpty() ? 0 : g2.connections.getLast().id;

        // We flip the genomes to make sure we use as much data as we can.
        if (highest1 < highest2) {
            g1 = g2;
            g2 = this;
        }

        int disjoint = 0, similar = 0;
        double weightDiff = 0.0;

        Iterator<ConnectionGene> iterator1 = g1.connections.iterator();
        Iterator<ConnectionGene> iterator2 = g2.connections.iterator();
        ConnectionGene node1 = null, node2 = null;
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

        weightDiff /= Math.max(similar, 1);
        int excess = g1.connections.size() - index;

        return neat.getProperty("excessFactor") * excess / n
                + neat.getProperty("disjointFactor") * disjoint / n
                + neat.getProperty("weightFactor") * weightDiff;
    }

    public void mutate(Mutation mutation) {
        mutation.mutate(this);
    }

    public static Genome crossOver(Genome g1, Genome g2) {

        int highest1 = g1.connections.isEmpty() ? 0 : g1.connections.getLast().id;
        int highest2 = g2.connections.isEmpty() ? 0 : g2.connections.getLast().id;

        // We flip the genomes to make sure we use as much data as we can.
        if (highest1 < highest2) {
            Genome temp = g1;
            g1 = g2;
            g2 = temp;
        }

        Genome child = g1.neat.newGenome();

        Iterator<ConnectionGene> iterator1 = g1.connections.iterator();
        Iterator<ConnectionGene> iterator2 = g2.connections.iterator();
        ConnectionGene node1 = null, node2 = null;
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
    @SuppressWarnings("unchecked")
    public JSONObject deserialize() {
        JSONObject json = new JSONObject();
        JSONArray arr;

        arr = new JSONArray();
        for (ConnectionGene connection : connections) {
            arr.add(connection.getId());
        }
        json.put("connections", arr);

        arr = new JSONArray();
        for (NodeGene node : nodes) {
            arr.add(node.id);
        }
        json.put("nodes", arr);

        return json;
    }

    @Override
    public void serialize(JSONObject data) {

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

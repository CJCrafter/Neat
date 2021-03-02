package me.cjcrafter.neat;

import me.cjcrafter.neat.genome.ConnectionGene;
import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.NodeGene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neat {

    public static final int MAX_NODE_BITS = 8;
    public static final int MAX_NODES = 1 << MAX_NODE_BITS;

    private double factor1 = 1.0, factor2 = 1.0, factor3 = 1.0;
    private double randomWeightStrength = 1.0, shiftWeightStrength = 0.3;

    private Map<ConnectionGene, ConnectionGene> connectionCache;
    private List<NodeGene> nodeCache;

    private int inputNodes;
    private int outputNodes;
    private int clients;

    public Neat(int inputNodes, int outputNodes, int clients) {
        reset(inputNodes, outputNodes, clients);
    }

    // TODO This looks like a violation of immutabnle objects to me. See the
    // TODO reset usage later on.
    public void reset(int inputNodes, int outputNodes, int clients) {
        this.connectionCache = new HashMap<>();
        this.nodeCache = new ArrayList<>();

        this.inputNodes = inputNodes;
        this.outputNodes = outputNodes;
        this.clients = clients;

        // Sets the input node layer. The number of input nodes will not change
        // unless the reset method is called. The input nodes are always
        // rendered on the left side
        for (int i = 0; i < inputNodes; i++) {
            NodeGene node = newNode();
            node.setX(0.1);
            node.setY((i + 1.0) / (inputNodes + 1.0));
        }

        // Sets the output node layer. The number of output nodes will not
        // change unless the reset method is called. The output nodes are
        // always rendered on the right side.
        for (int i = 0; i < outputNodes; i++) {
            NodeGene node = newNode();
            node.setX(0.9);
            node.setY((i + 1.0) / (outputNodes + 1.0));
        }
    }

    public double getFactor1() {
        return factor1;
    }

    public double getFactor2() {
        return factor2;
    }

    public double getFactor3() {
        return factor3;
    }

    public Genome newGenome() {
        Genome genome = new Genome(this);

        int visibleNodes = inputNodes + outputNodes;
        for (int i = 0; i < visibleNodes; i++) {
            NodeGene node = getNode(i);
            genome.add(node);
        }

        return genome;
    }

    public NodeGene newNode() {
        NodeGene node = new NodeGene(nodeCache.size() + 1);
        nodeCache.add(node);
        return node;
    }

    public ConnectionGene newConnectionGene(NodeGene from, NodeGene to) {
        ConnectionGene connection = new ConnectionGene(from, to);

        if (connectionCache.containsKey(connection)) {
            int id = connectionCache.get(connection).getId();
            connection.setId(id);
        } else {
            int id = connectionCache.size();
            connection.setId(id);
            connectionCache.put(connection, connection);
            connection = new ConnectionGene(connection);
        }

        return connection;
    }

    public NodeGene getNode(int id) {
        if (id < 0 || id > nodeCache.size()) {
            throw new ArrayIndexOutOfBoundsException("Invalid innovation id: " + id);
        } else if (id == nodeCache.size()) {
            return newNode();
        } else {
            return nodeCache.get(id);
        }
    }

    public double getRandomWeightStrength() {
        return randomWeightStrength;
    }

    public double getShiftWeightStrength() {
        return shiftWeightStrength;
    }
}
package me.cjcrafter.neat;

import me.cjcrafter.neat.genome.ConnectionGene;
import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.Mutation;
import me.cjcrafter.neat.genome.NodeGene;
import me.cjcrafter.neat.util.ProbabilityMap;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neat {

    public static final int MAX_NODE_BITS = 8;
    public static final int MAX_NODES = 1 << MAX_NODE_BITS;

    private double speciesDistance = 4.0;
    private double survivalChance = 0.80;
    private double factor1 = 1.0, factor2 = 1.0, factor3 = 1.0;
    private double randomWeightStrength = 1.0, shiftWeightStrength = 0.3;

    private Map<ConnectionGene, ConnectionGene> connectionCache;
    private List<NodeGene> nodeCache;
    private List<Client> clients;
    private List<Species> species;

    private int inputNodes;
    private int outputNodes;
    private int maxClients;

    public Neat(int inputNodes, int outputNodes, int clients) {
        init(inputNodes, outputNodes, clients);
    }

    public void init(int inputNodes, int outputNodes, int clients) {
        this.connectionCache = new HashMap<>();
        this.nodeCache = new ArrayList<>();
        this.clients = new ArrayList<>(clients);
        this.species = new ArrayList<>();

        this.inputNodes = inputNodes;
        this.outputNodes = outputNodes;
        this.maxClients = clients;

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

        for (int i = 0; i < maxClients; i++) {
            Client client = new Client();
            client.setGenome(newGenome());
            this.clients.add(client);
        }
    }

    public int getInputNodes() {
        return inputNodes;
    }

    public int getOutputNodes() {
        return outputNodes;
    }

    public double getSpeciesDistance() {
        return speciesDistance;
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

    public double getRandomWeightStrength() {
        return randomWeightStrength;
    }

    public double getShiftWeightStrength() {
        return shiftWeightStrength;
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

    public void evolve() {

        species.forEach(Species::reset);

        // We need to reset all of the species, and resort all of the clients
        // into their species. If no such species exists, we create new ones.
        for (Client client : clients) {
            if (client.getSpecies() == null) {
                for (Species species : species) {
                    if (species.put(client)) {
                        break;
                    }
                }

                // If there are no matching species, create a new one.
                if (client.getSpecies() == null) {
                    species.add(new Species(client));
                }
            }
        }

        // Evaluate the value of a species, then kill of it's lowest members
        species.forEach(species -> {
            species.evaluate();
            species.kill(1.0 - survivalChance);
        });

        for (Species species : species) {
            if (species.size() == 1) {
                species.getClients().remove(0);
            }
        }

        ProbabilityMap<Species> random = new ProbabilityMap<>();
        random.putAll(species, Species::getScore);

        for (Client client : clients) {
            if (client.getSpecies() == null) {
                Species species = random.get();
                client.setGenome(species.breed());
                client.mutate(Mutation.MUTATE);
                species.put(client, true);
            }
        }
    }

    public void temp() {
        double[] in = new double[5];
        for (int i = 0; i < in.length; i++)
            in[i] = Math.random();

        for (int i = 0; i < 100; i++) {
            clients.forEach(client -> client.setScore(client.getCalculator().calculate(in)[0]));
            evolve();
            printSpecies();
        }
    }

    public void printSpecies() {
        System.out.println("------------------------------");
        System.out.println("Score        | Clients");
        for (Species species : species) {
            System.out.printf("%-13s|  %-15s%n", round(species.getScore()), species.size());
        }
    }

    private double round(double a) {
        return new BigDecimal(a, new MathContext(10)).doubleValue();
    }
}
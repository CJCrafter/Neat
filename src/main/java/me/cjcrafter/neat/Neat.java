package me.cjcrafter.neat;

import me.cjcrafter.neat.file.ListDeserializer;
import me.cjcrafter.neat.file.Serializable;
import me.cjcrafter.neat.genome.ConnectionGene;
import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.Mutation;
import me.cjcrafter.neat.genome.NodeGene;
import me.cjcrafter.neat.ui.Frame;
import me.cjcrafter.neat.util.DoubleMap;
import me.cjcrafter.neat.util.ProbabilityMap;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Neat implements Serializable {

    public static final int MAX_NODE_BITS = 12;
    public static final int MAX_NODES = 1 << MAX_NODE_BITS;

    public static final String SPECIES_DISTANCE_PROPERTY = "speciesDistance";
    public static final String SURVIVAL_CHANCE_PROPERTY = "survivalChance";
    public static final String EXCESS_FACTOR_PROPERTY = "excessFactor";
    public static final String DISJOINT_FACTOR_PROPERTY = "disjointFactor";
    public static final String WEIGHT_DIFFERENCE_PROPERTY = "weightDifference";
    public static final String RANDOM_WEIGHT_STRENGTH_PROPERTY = "randomWeightStrength";
    public static final String SHIFT_WEIGHT_STRENGTH_PROPERTY = "shiftWeightStrength";

    private DoubleMap<String> properties;

    private Map<ConnectionGene, ConnectionGene> connectionCache;
    private List<NodeGene> nodeCache;
    private List<Client> clients;
    private List<Species> species;

    private int inputNodes;
    private int outputNodes;
    private int maxClients;

    public Neat() {

    }

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

        properties = new DoubleMap<>();
        properties.put(SPECIES_DISTANCE_PROPERTY, 6.0);
        properties.put(SURVIVAL_CHANCE_PROPERTY, 0.80);
        properties.put(EXCESS_FACTOR_PROPERTY, 2.0);
        properties.put(DISJOINT_FACTOR_PROPERTY, 2.0);
        properties.put(WEIGHT_DIFFERENCE_PROPERTY, 1.0);
        properties.put(RANDOM_WEIGHT_STRENGTH_PROPERTY, 1.0);
        properties.put(SHIFT_WEIGHT_STRENGTH_PROPERTY, 0.3);

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

    public double getProperty(String property) {
        return properties.get(property);
    }

    public double setDouble(String property, double value) {
        return properties.put(property, value);
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

    //public int getReplaceIndex(NodeGene from, NodeGene to) {
    //    ConnectionGene connection = connectionCache.get(new ConnectionGene(from, to));
    //    return connection == null ? -1 : connection.getReplaceId();
    //}
//
    //public void setReplaceIndex(NodeGene from, NodeGene to, int id) {
    //    connectionCache.get(new ConnectionGene(from, to)).setReplaceId(id);
    //}

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

        // Evaluate the value of a species, then kill of it's lowest members.
        // If a species becomes to small, it dies out.
        Iterator<Species> iterator = species.iterator();
        while (iterator.hasNext()) {
            Species next = iterator.next();

            next.evaluate();
            next.kill(1.0 - getProperty(SURVIVAL_CHANCE_PROPERTY));

            if (next.size() <= 1) {
                next.kill();
                iterator.remove();
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

        Function[] functions = new Function[]{
                new Function(0, 0),
                new Function(1, 0),
                new Function(0, 1),
                new Function(1, 1)
        };

        for (int i = 0; i < 1000; i++) {
            clients.forEach(client -> {
                double incorrectness = 0.0;
                for (Function function : functions) {
                    double output = client.getCalculator().calculate(function.input1, function.input2)[0];
                    incorrectness += Math.abs(function.output - output);
                }

                client.setScore(4.0 - incorrectness);
            });

            evolve();
            //printSpecies();
        }

        clients.sort(Comparator.comparingDouble(Client::getScore));
        Client client = clients.get(0);

        for (Function function : functions) {
            System.out.printf("%s ^ %s = %s%n", function.input1, function.input2, client.getCalculator().calculate(function.input1, function.input2)[0]);
        }

        JSONObject json = deserialize();
        try (FileWriter writer = new FileWriter(System.getProperty("user.dir") + File.separator + "neat.json")) {
            writer.write(json.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Frame frame = new Frame();

        int index = 0;
        while (true) {
            frame.setGenome(clients.get(index++ % clients.size()).getGenome());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Function {
        int input1;
        int input2;
        int output;
        public Function(int input1, int input2) {
            this.input1 = input1;
            this.input2 = input2;
            this.output = input1 ^ input2;
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

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject deserialize() {
        JSONObject json = new JSONObject();
        json.put("properties", properties.deserialize());
        json.put("inputNodes", inputNodes);
        json.put("outputNodes", outputNodes);
        json.put("maxClients", maxClients);

        json.put("connections", new ListDeserializer(connectionCache.keySet()).deserialize());
        json.put("nodes", new ListDeserializer(nodeCache).deserialize());
        json.put("clients", new ListDeserializer(clients).deserialize());
        return json;
    }

    @Override
    public void serialize(JSONObject data) {

    }
}
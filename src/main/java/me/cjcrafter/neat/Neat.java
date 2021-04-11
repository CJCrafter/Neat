package me.cjcrafter.neat;

import me.cjcrafter.neat.file.ListDeserializer;
import me.cjcrafter.neat.file.Serializable;
import me.cjcrafter.neat.genome.ConnectionGene;
import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.Mutation;
import me.cjcrafter.neat.genome.NodeGene;
import me.cjcrafter.neat.util.ProbabilityMap;
import me.cjcrafter.neat.util.primitive.DoubleMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Neat implements Serializable {

    public static final int MAX_NODE_BITS = 8;
    public static final int MAX_NODES = 1 << MAX_NODE_BITS;

    private DoubleMap<String> properties;

    private Map<ConnectionGene, ConnectionGene> connectionCache;
    private List<NodeGene> nodeCache;
    private List<Client> clients;
    private List<Species> species;

    private int inputNodes;
    private int outputNodes;
    private int maxClients;

    private int[] hiddenLayers;

    public Neat() {

    }

    public Neat(int inputNodes, int outputNodes, int clients) {
        init(inputNodes, outputNodes, clients);
    }

    @SuppressWarnings("all")
    public void init(int inputNodes, int outputNodes, int clients) {
        this.connectionCache = new HashMap<>();
        this.nodeCache = new ArrayList<>();
        this.clients = new ArrayList<>(clients);
        this.species = new ArrayList<>();

        this.inputNodes = inputNodes;
        this.outputNodes = outputNodes;
        this.maxClients = clients;

        try {
            JSONParser parser = new JSONParser();
            InputStream resource = getClass().getClassLoader().getResourceAsStream("initial-structure.json");
            Map map = (Map) parser.parse(new InputStreamReader(resource));

            hiddenLayers = ((List<Long>) map.get("layers")).stream().mapToInt(Long::intValue).toArray();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        properties = new DoubleMap<>();
        try {
            JSONParser parser = new JSONParser();
            InputStream resource = getClass().getClassLoader().getResourceAsStream("default-properties.json");
            Map map = (Map) parser.parse(new InputStreamReader(resource));
            JSONObject json = new JSONObject(map);
            properties.serialize(json);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

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

        for (int i = 0; i < hiddenLayers.length; i++) {
            int count = hiddenLayers[i];
            for (int j = 0; j < count; j++) {
                NodeGene node = newNode();
                node.setX(0.8 / (hiddenLayers.length + 1) * (i + 1) + 0.1);
                node.setY((j + 1.0) / (count + 1.0));
            }
        }

        for (int i = 0; i < maxClients; i++) {
            Client client = new Client(i);
            client.setGenome(newGenome());
            this.clients.add(client);
        }

        sortSpecies();
    }

    public int getInputNodes() {
        return inputNodes;
    }

    public int getOutputNodes() {
        return outputNodes;
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Species> getSpecies() {
        return species;
    }

    public double getProperty(String property) {
        return properties.get(property);
    }

    public double setProperty(String property, double value) {
        return properties.put(property, value);
    }

    public Genome newGenome() {
        Genome genome = new Genome(this);

        int nodes = inputNodes + outputNodes + IntStream.of(hiddenLayers).sum();
        for (int i = 0; i < nodes; i++) {
            NodeGene node = getNode(i);
            genome.add(node);
        }

        return genome;
    }

    public NodeGene newNode() {
        NodeGene node = new NodeGene(nodeCache.size());
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

    public int getReplaceIndex(NodeGene from, NodeGene to) {
        ConnectionGene connection = connectionCache.get(new ConnectionGene(from, to));
        return connection == null ? 0 : connection.getReplaceId();
    }

    public void setReplaceIndex(NodeGene from, NodeGene to, int id) {
        connectionCache.get(new ConnectionGene(from, to)).setReplaceId(id);
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

    public void sortSpecies() {
        species.forEach(Species::reset);

        // We need to reset all of the species, and resort all of the clients
        // into their species. If no such species exists, we create new ones.
        for (Client client : clients) {
            if (client.getSpecies() == null) {
                if (client.parentSpecies == null || !client.parentSpecies.put(client)) {
                    for (Species species : species) {
                        if (species.put(client)) {
                            break;
                        }
                    }
                }

                // If there are no matching species, create a new one.
                if (client.getSpecies() == null) {
                    species.add(new Species(client));
                }
            }
        }
    }

    public void evolve() {
        sortSpecies();

        // Evaluate the value of a species, then kill of it's lowest members.
        // If a species becomes to small, it dies out.
        Iterator<Species> iterator = species.iterator();
        while (iterator.hasNext()) {
            Species next = iterator.next();

            next.evaluate();
            next.kill(1.0 - getProperty("survivalChance"));

            if (next.size() <= 1) {
                next.kill();
                iterator.remove();
            }
        }

        ProbabilityMap<Species> random = new ProbabilityMap<>();
        random.putAll(species, Species::getScore);

        if (random.isEmpty()) {
            System.out.println("weewoo");
        }

        for (Client client : clients) {
            if (client.getSpecies() == null) {
                Species species = random.get();
                client.setGenome(species.breed());
                client.mutate(Mutation.MUTATE);
                species.put(client, true);
            }
        }
    }

    public String debugGenome() {
        int nodes = 0;
        int connections = 0;

        for (Client client : clients) {
            Genome genome = client.getGenome();
            nodes += genome.getNodes().size();
            connections += genome.getConnections().size();
        }

        nodes /= clients.size();
        connections /= clients.size();

        return "Avg Nodes: " + nodes + "; Avg Connections: " + connections;
    }

    public void printSpecies() {
        System.out.println("-----------------------");
        System.out.println(" Score       | Clients ");
        for (Species species : species) {
            System.out.printf("%-13s| %-9s%n", round(species.getScore()), species.size());
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
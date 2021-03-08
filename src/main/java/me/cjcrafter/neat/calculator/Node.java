package me.cjcrafter.neat.calculator;

import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node> {

    private double x;
    private double output;
    private List<Connection> connections;

    public Node(double x) {
        this.x = x;
        this.connections = new ArrayList<>();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public void calculate() {
        double temp = 0.0;
        for (Connection connection : connections) {
            if (connection.isEnabled()) {
                temp += connection.getWeight() * connection.getFrom().output;
            }
        }
        this.output = activationFunction(temp);
    }

    public void addConnection(Connection connection) {
        if (connection == null)
            throw new IllegalArgumentException("connection is null");

        connections.add(connection);
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.x, other.x);
    }

    private static double activationFunction(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
}

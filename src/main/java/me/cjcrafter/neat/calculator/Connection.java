package me.cjcrafter.neat.calculator;

public class Connection {

    private Node from;
    private Node to;
    private double weight;
    private boolean enabled;

    public Connection(Node from, Node to) {
        if (from == null || to == null)
            throw new IllegalArgumentException("null nodes");

        this.from = from;
        this.to = to;
        this.enabled = true;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

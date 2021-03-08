package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.Neat;

import java.util.Objects;

public class ConnectionGene extends Gene {

    private NodeGene from;
    private NodeGene to;
    private double weight;
    private boolean enabled;

    public ConnectionGene(NodeGene from, NodeGene to) {

        // We don't know the id yet, the main Neat class handles this.
        super(0);

        this.from = from;
        this.to = to;
        this.enabled = true;
    }

    public ConnectionGene(ConnectionGene other) {
        super(other.getId());

        this.from = other.from;
        this.to = other.to;
        this.weight = other.weight;
        this.enabled = other.enabled;
    }

    public NodeGene getFrom() {
        return from;
    }

    public void setFrom(NodeGene from) {
        this.from = from;
    }

    public NodeGene getTo() {
        return to;
    }

    public void setTo(NodeGene to) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionGene that = (ConnectionGene) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return from.getId() << (Neat.MAX_NODE_BITS) | to.getId();
    }
}

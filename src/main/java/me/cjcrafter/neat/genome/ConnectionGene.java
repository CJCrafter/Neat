package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.file.Serializable;
import org.json.simple.JSONObject;

import java.util.Objects;

public class ConnectionGene extends Gene implements Serializable {

    private NodeGene from;
    private NodeGene to;
    private double weight;
    private boolean enabled;

    private int replaceId;

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
        this.replaceId = other.replaceId;
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

    public int getReplaceId() {
        return replaceId;
    }

    public void setReplaceId(int id) {
        this.replaceId = id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject deserialize() {
        JSONObject json = super.deserialize();
        json.put("from", from.id);
        json.put("to", to.id);
        json.put("weight", weight);
        json.put("enabled", enabled);
        json.put("replaceId", replaceId);
        return json;
    }

    @Override
    public void serialize(JSONObject json) {
        if (from == null || to == null)
            throw new IllegalStateException("Nodes have not been initialized!");

        super.serialize(json);

        weight = (double) json.get("weight");
        enabled = (boolean) json.get("enabled");
        replaceId = ((Long) json.get("replaceId")).intValue();
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

    @Override
    public String toString() {
        return "ConnectionGene{" +
                "from=" + from.id +
                ", to=" + to.id +
                ", weight=" + weight +
                ", enabled=" + enabled +
                ", id=" + id +
                '}';
    }
}

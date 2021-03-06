package me.cjcrafter.neat.genome;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NodeGene extends Gene {

    private final NodeType type;
    private double x;
    private double y;

    List<ConnectionGene> entering;
    List<ConnectionGene> leaving;

    public NodeGene(NodeType type, int id) {
        super(id);

        this.type = type;
        this.entering = new ArrayList<>(5);
        this.leaving = new ArrayList<>(5);
    }

    public NodeType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y /*+ ThreadLocalRandom.current().nextDouble(-0.05, 0.05)*/;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject deserialize() {
        JSONObject json = super.deserialize();
        json.put("x", x);
        json.put("y", y);
        return json;
    }

    @Override
    public void serialize(JSONObject data) {
        super.serialize(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeGene nodeGene = (NodeGene) o;
        return id == nodeGene.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "NodeGene{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

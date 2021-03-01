package me.cjcrafter.neat.genome;

public class NodeGene extends Gene {

    private double x;
    private double y;

    public NodeGene(int id) {
        super(id);
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
        this.y = y;
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
}
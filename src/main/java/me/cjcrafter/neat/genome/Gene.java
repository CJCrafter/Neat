package me.cjcrafter.neat.genome;

public class Gene {

    protected int id;

    public Gene(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "id=" + id +
                '}';
    }
}

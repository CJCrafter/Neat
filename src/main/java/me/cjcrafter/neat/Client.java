package me.cjcrafter.neat;

import me.cjcrafter.neat.calculator.Calculator;
import me.cjcrafter.neat.file.Serializable;
import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.genome.Mutation;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.Objects;

public class Client implements Comparable<Client>, Serializable {

    private Calculator calculator;
    private Genome genome;
    private double score;
    private Species species;
    private int id;
    public Color speciesColor;

    public Species parentSpecies;
    public Neat neat;

    public Client(int id) {
        this.id = id;
    }

    public Client(Neat neat) {
        this.neat = neat;
    }

    public Calculator getCalculator() {
        if (calculator == null) {
            if (genome == null)
                throw new IllegalStateException();

            calculator = new Calculator(genome);
        }

        return calculator;
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
        this.parentSpecies = species;
    }

    public int getId() {
        return id;
    }

    public void mutate(Mutation mutation) {
        if (mutation.mutate(genome))
            calculator = null;

        System.out.println(genome.getConnections());
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSONObject deserialize() {
        JSONObject json = new JSONObject();
        json.put("genome", genome.deserialize());
        json.put("score", score);
        json.put("id", id);
        if (speciesColor != null) json.put("color", speciesColor.getRGB());

        neat = null; // we don't need this anymore... just for genome
        return json;
    }

    @Override
    public void serialize(JSONObject json) {
        genome = new Genome(neat);
        genome.serialize((JSONObject) json.get("genome"));

        id = ((Long) json.get("id")).intValue();
        score = (double) json.get("score");
        speciesColor = json.containsKey("color") ? new Color(((Long) json.get("color")).intValue()) : null;
    }

    @Override
    public int compareTo(Client o) {
        return Double.compare(score, o.score);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

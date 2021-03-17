package me.cjcrafter.neat;

import me.cjcrafter.neat.genome.Genome;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

public class Species {

    private List<Client> clients;
    private Client base;
    private double score;

    public Species(Client base) {
        if (base == null)
            throw new IllegalArgumentException();

        this.clients = new ArrayList<>();
        this.base = base;
        this.base.setSpecies(this);
        this.clients.add(base);
    }

    private Client random() {
        if (clients.size() == 0)
            throw new NoSuchElementException();

        return clients.get(ThreadLocalRandom.current().nextInt(clients.size()));
    }

    public List<Client> getClients() {
        return clients;
    }

    public Client getBase() {
        return base;
    }

    public double getScore() {
        return score;
    }

    public Neat getNeat() {
        return base.getGenome().getNeat();
    }

    public boolean matches(Client client) {
        double distance = base.getGenome().distance(client.getGenome());
        double max = getNeat().getProperty(Neat.SPECIES_DISTANCE_PROPERTY);
        return distance < max;
    }

    public boolean put(Client client) {
        return put(client, false);
    }

    public boolean put(Client client, boolean force) {
        if (force || matches(client)) {
            client.setSpecies(this);
            clients.add(client);
            return true;
        }
        return false;
    }

    public void evaluate() {
        score = 0;
        for (Client client : clients) {
            score += client.getScore();
        }
        score /= clients.size();
    }

    public void reset() {
        base = size() == 0 ? base : random();
        kill();
        clients.clear();

        clients.add(base);
        score = 0;
    }

    public void kill() {
        for (Client client : clients) {
            client.setSpecies(null);
        }
    }

    public void kill(double percentage) {
        clients.sort(null);

        int count = 0;
        int bound = (int) (percentage * clients.size());
        ListIterator<Client> iterator = clients.listIterator();

        while (count++ < bound && iterator.hasNext()) {
            iterator.next().setSpecies(null);
            iterator.remove();
        }
    }

    public Genome breed() {
        if (clients.isEmpty())
            return null;

        Client c1 = random();
        Client c2 = random();

        if (c1.getScore() > c2.getScore())
            return Genome.crossOver(c1.getGenome(), c2.getGenome());
        else
            return Genome.crossOver(c2.getGenome(), c1.getGenome());
    }

    public int size() {
        return clients.size();
    }
}
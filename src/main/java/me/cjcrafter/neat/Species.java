package me.cjcrafter.neat;

import me.cjcrafter.neat.genome.Genome;
import me.cjcrafter.neat.util.SortedList;

import java.util.ListIterator;

public class Species {

    private SortedList<Client> clients;
    private Client base;
    private double score;

    public Species(Client base) {
        this.base = base;
        this.base.setSpecies(this);
        this.clients.add(base);
    }

    public SortedList<Client> getClients() {
        return clients;
    }

    public Client getBase() {
        return base;
    }

    public double getScore() {
        return score;
    }

    public Neat getNeat() {
        return base.getSpecies().getNeat();
    }

    public boolean matches(Client client) {
        return base.getGenome().distance(client.getGenome()) < getNeat().getSpeciesDistance();
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
        base = clients.getRandomElement();
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
        clients.sort();

        int count = 0;
        int bound = (int) (percentage * clients.size());
        ListIterator<Client> iterator = clients.iterator();

        while (count++ < bound && iterator.hasNext()) {
            iterator.next().setSpecies(null);
            iterator.remove();
        }
    }

    public Genome breed() {
        Client c1 = clients.getRandomElement();
        Client c2 = clients.getRandomElement();

        if (c1.getScore() > c2.getScore())
            return Genome.crossOver(c1.getGenome(), c2.getGenome());
        else
            return Genome.crossOver(c2.getGenome(), c1.getGenome());
    }

    public int size() {
        return clients.size();
    }
}
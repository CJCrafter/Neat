package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.util.SortedList;

import java.util.concurrent.ThreadLocalRandom;

public enum Mutation {

    RANDOM_WEIGHT(0.002) {
        @Override
        public void mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return;

            ConnectionGene connection = genome.getConnections().getRandomElement();
            connection.setWeight((ThreadLocalRandom.current().nextDouble() * 2 - 1)
                    * genome.getNeat().getProperty(Neat.RANDOM_WEIGHT_STRENGTH_PROPERTY));
        }
    }, WEIGHT_SHIFT(0.002) {
        @Override
        public void mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return;

            ConnectionGene connection = genome.getConnections().getRandomElement();
            connection.setWeight(connection.getWeight() + (ThreadLocalRandom.current().nextDouble() * 2 - 1)
                    * genome.getNeat().getProperty(Neat.SHIFT_WEIGHT_STRENGTH_PROPERTY));
        }
    }, TOGGLE(0.0) {
        @Override
        public void mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return;

            ConnectionGene connection = genome.getConnections().getRandomElement();
            connection.setEnabled(!connection.isEnabled());
        }
    }, ADD_LINK(0.01) {
        @Override
        public void mutate(Genome genome) {

            // Try to mutate a new link 100 times. 100 times is most likely
            // always going to be enough tries to mutate a link.
            for (int i = 0; i < 100; i++) {
                SortedList<NodeGene> nodes = genome.getNodes();

                NodeGene a = nodes.getRandomElement();
                NodeGene b = nodes.getRandomElement();

                // Node connections go from left to right
                ConnectionGene connection;
                if (a.getX() == b.getX()) {
                    continue;
                } else if (a.getX() < b.getX()) {
                    connection = new ConnectionGene(a, b);
                } else {
                    connection = new ConnectionGene(b, a);
                }

                // If that connection already exists, try again
                if (genome.getConnections().contains(connection)) {
                    continue;
                }

                // Pull the connection from the neat pool of connections, or
                // add it if it does not yet exist
                connection = genome.getNeat().newConnectionGene(connection.getFrom(), connection.getTo());
                connection.setWeight(ThreadLocalRandom.current().nextDouble(-1, +1)
                        * genome.getNeat().getProperty(Neat.RANDOM_WEIGHT_STRENGTH_PROPERTY));

                genome.getConnections().addSorted(connection);
                break;
            }
        }
    }, ADD_NODE(0.003) {
        @Override
        public void mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return;

            ConnectionGene connection = genome.getConnections().getRandomElement();

            NodeGene from = connection.getFrom();
            NodeGene to = connection.getTo();
            NodeGene middle;

            //int replaceId = genome.getNeat().getReplaceIndex(from, to);
            //if (replaceId == -1) {
                middle = genome.getNeat().newNode();
                middle.setX((from.getX() + to.getX()) / 2);
                middle.setY((from.getY() + to.getY()) / 2);
            //    genome.getNeat().setReplaceIndex(from, to, middle.getId());
            //} else {
            //    middle = genome.getNeat().getNode(replaceId);
            //}

            ConnectionGene a = genome.getNeat().newConnectionGene(from, middle);
            ConnectionGene b = genome.getNeat().newConnectionGene(middle, to);

            a.setWeight(1);
            b.setWeight(connection.getWeight());
            b.setEnabled(connection.isEnabled());

            genome.getConnections().remove(connection);
            genome.getConnections().add(a);
            genome.getConnections().add(b);

            genome.getNodes().add(middle);
        }
    }, MUTATE(0.0) {
        @Override
        public void mutate(Genome genome) {
            for (int i = 0; i < 10; i++) {
                if (ADD_LINK.chance > ThreadLocalRandom.current().nextDouble())
                    ADD_LINK.mutate(genome);
                if (ADD_NODE.chance > ThreadLocalRandom.current().nextDouble())
                    ADD_NODE.mutate(genome);
                if (RANDOM_WEIGHT.chance > ThreadLocalRandom.current().nextDouble())
                    RANDOM_WEIGHT.mutate(genome);
                if (WEIGHT_SHIFT.chance > ThreadLocalRandom.current().nextDouble())
                    WEIGHT_SHIFT.mutate(genome);
                if (TOGGLE.chance > ThreadLocalRandom.current().nextDouble())
                    TOGGLE.mutate(genome);
            }
        }
    };

    public final String name;
    public final double chance;

    Mutation(double chance) {
        String[] split = name().toLowerCase().split("_");

        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            builder.append(s.substring(0, 1).toUpperCase());
            builder.append(s.substring(1));
            builder.append(" ");
        }
        this.name = builder.substring(0, builder.length() - 1);
        this.chance = chance;
    }

    public abstract void mutate(Genome genome);
}
